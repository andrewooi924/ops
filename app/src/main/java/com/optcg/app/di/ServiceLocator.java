package com.optcg.app.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.optcg.app.Card;
import com.optcg.app.CardDatabase;
import com.optcg.app.data.local.AssetCardSeeder;
import com.optcg.app.data.local.LocalCardDataSource;
import com.optcg.app.data.local.CardImageIndex;
import com.optcg.app.data.local.LocalPriceDataSource;
import com.optcg.app.data.local.UserDataStore;
import com.optcg.app.data.remote.image.CardImageUrlResolver;
import com.optcg.app.data.remote.image.StorageImageUrlResolver;
import com.optcg.app.ui.image.CardImageLoader;
import com.optcg.app.data.remote.FirestoreCardDataSource;
import com.optcg.app.data.remote.RemoteCardDataSource;
import com.optcg.app.data.remote.RemotePriceDataSource;
import com.optcg.app.data.remote.scraper.ScraperHtmlClient;
import com.optcg.app.data.remote.scraper.ScraperPriceDataSource;
import com.optcg.app.data.repository.CardRepository;
import com.optcg.app.data.repository.CardRepositoryImpl;
import com.optcg.app.data.repository.CollectionRepository;
import com.optcg.app.data.repository.CollectionRepositoryImpl;
import com.optcg.app.data.repository.PortfolioRepository;
import com.optcg.app.data.repository.PortfolioRepositoryImpl;
import com.optcg.app.data.repository.PriceRepository;
import com.optcg.app.data.repository.PriceRepositoryImpl;
import com.optcg.app.data.repository.UserRepository;
import com.optcg.app.data.repository.UserRepositoryImpl;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optcg.app.R;
import com.optcg.app.sync.AuthManager;
import com.optcg.app.sync.FirebaseAuthManager;
import com.optcg.app.sync.FirestoreSyncManager;
import com.optcg.app.sync.LocalAuthManager;
import com.optcg.app.sync.NoOpSyncManager;
import com.optcg.app.sync.SyncManager;
import com.optcg.app.util.AppExecutors;

/**
 * Simple, hand-rolled dependency container (the app has no DI framework). Constructs
 * the data layer once and hands out shared instances. This is the single place to
 * swap implementations later — e.g. replacing the scraper-backed remote data sources
 * with a cloud backend, or wiring a real {@code AuthManager} for Google Sign-In —
 * without touching the UI.
 */
public class ServiceLocator {

    private final Context appContext;
    private final AppExecutors appExecutors;
    private final CardDatabase database;
    private final CardRepository cardRepository;
    private final PriceRepository priceRepository;
    private final CardImageLoader cardImageLoader;
    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final AuthManager authManager;
    private final SyncManager syncManager;

    public ServiceLocator(Context context) {
        this.appContext = context.getApplicationContext();
        this.appExecutors = new AppExecutors();
        this.database = CardDatabase.getDatabase(appContext);

        LocalCardDataSource localCardDataSource = new LocalCardDataSource(database.cardDao());
        AssetCardSeeder assetCardSeeder = new AssetCardSeeder(appContext);
        // Cloud card catalog (Firestore) if available; null falls back to asset-only.
        RemoteCardDataSource remoteCardDataSource;
        try {
            remoteCardDataSource = new FirestoreCardDataSource(FirebaseFirestore.getInstance());
        } catch (Throwable t) {
            remoteCardDataSource = null;
        }
        SharedPreferences catalogPrefs = appContext.getSharedPreferences("card_catalog", Context.MODE_PRIVATE);
        this.cardRepository = new CardRepositoryImpl(localCardDataSource, assetCardSeeder, appExecutors,
                remoteCardDataSource, catalogPrefs);
        // Seed the local cache from the bundled asset on first run, then refresh from the cloud
        // catalog if a newer version is published.
        this.cardRepository.ensureSeeded();
        this.cardRepository.refreshFromRemote();

        // Card image loading: remote (Firebase Storage) + Glide disk cache, swappable to a CDN
        // by changing the base URL string. The id->img index is populated from Room after seeding.
        CardImageIndex cardImageIndex = new CardImageIndex();
        CardImageUrlResolver imageUrlResolver =
                new StorageImageUrlResolver(appContext.getString(R.string.card_image_base_url));
        this.cardImageLoader = new CardImageLoader(imageUrlResolver, cardImageIndex);
        appExecutors.diskIO().execute(() -> {
            java.util.List<Card> cards = cardRepository.getCardsSync();
            if (cards != null) {
                java.util.Map<String, String> map = new java.util.HashMap<>();
                for (Card c : cards) {
                    if (c.getImg() != null) {
                        map.put(c.getId(), c.getImg());
                    }
                }
                cardImageIndex.setAll(map);
            }
        });

        // Price layer: offline-first Room cache in front of a remote source.
        // TODO(cloud): swap ScraperPriceDataSource for a cloud-backed RemotePriceDataSource here.
        LocalPriceDataSource localPriceDataSource = new LocalPriceDataSource(database.priceQuoteDao());
        RemotePriceDataSource remotePriceDataSource = new ScraperPriceDataSource(new ScraperHtmlClient());
        this.priceRepository = new PriceRepositoryImpl(localPriceDataSource, remotePriceDataSource, appExecutors);

        // User-owned state, still backed by SharedPreferences (no migration) behind repositories.
        // TODO(cloud): move these local sources to Room / a synced cloud store behind the same interfaces.
        SharedPreferences collectionPrefs = appContext.getSharedPreferences("COLLECTION_PREFS", Context.MODE_PRIVATE);
        SharedPreferences userPrefs = appContext.getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE);
        SharedPreferences portfolioPrefs = appContext.getSharedPreferences("PortfolioData", Context.MODE_PRIVATE);
        // Local user-data store: Room-backed, in-memory mirror, one-time import from the
        // legacy SharedPreferences files (left intact as a backstop).
        UserDataStore userDataStore = new UserDataStore(database.userDataDao(), appExecutors,
                collectionPrefs, userPrefs, portfolioPrefs);
        this.collectionRepository = new CollectionRepositoryImpl(userDataStore);
        this.userRepository = new UserRepositoryImpl(userDataStore);
        this.portfolioRepository = new PortfolioRepositoryImpl(userDataStore);

        // Authentication: Firebase + Google Sign-In, with a signed-out local fallback if
        // Firebase can't initialize (e.g. missing/invalid config).
        AuthManager auth;
        try {
            auth = new FirebaseAuthManager(appContext, appContext.getString(R.string.web_client_id));
        } catch (Throwable t) {
            auth = new LocalAuthManager();
        }
        this.authManager = auth;

        // Cloud sync: Firestore-backed when Firebase auth is active, otherwise no-op.
        SyncManager sync;
        if (authManager instanceof FirebaseAuthManager) {
            try {
                sync = new FirestoreSyncManager(FirebaseFirestore.getInstance(), authManager,
                        database.userDataDao(), userDataStore, appExecutors);
            } catch (Throwable t) {
                sync = new NoOpSyncManager();
            }
        } else {
            sync = new NoOpSyncManager();
        }
        this.syncManager = sync;

        // Sync on startup if a session is already signed in from a previous launch.
        if (this.authManager.isSignedIn()) {
            this.syncManager.syncNow();
        }
    }

    public AppExecutors appExecutors() {
        return appExecutors;
    }

    public CardDatabase database() {
        return database;
    }

    public CardRepository cardRepository() {
        return cardRepository;
    }

    public PriceRepository priceRepository() {
        return priceRepository;
    }

    public CardImageLoader cardImageLoader() {
        return cardImageLoader;
    }

    public CollectionRepository collectionRepository() {
        return collectionRepository;
    }

    public UserRepository userRepository() {
        return userRepository;
    }

    public PortfolioRepository portfolioRepository() {
        return portfolioRepository;
    }

    public AuthManager authManager() {
        return authManager;
    }

    public SyncManager syncManager() {
        return syncManager;
    }

    /** Convenience accessor used by UI classes that only have a {@link Context}. */
    public static ServiceLocator get(Context context) {
        return ((OptcgApplication) context.getApplicationContext()).getServiceLocator();
    }
}
