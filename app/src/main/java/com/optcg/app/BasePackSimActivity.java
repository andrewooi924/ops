package com.optcg.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.optcg.app.data.repository.CollectionRepository;
import com.optcg.app.data.repository.UserRepository;
import com.optcg.app.di.ServiceLocator;
import com.optcg.app.ui.image.CardImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared implementation of a pack-opening simulator. Behaviour (the gacha probability
 * tree, the pity system, rewards and collection bookkeeping) is identical to the
 * original hand-written OPxxSimActivity screens; everything that varied between sets
 * is supplied as data via {@link #getConfig()}.
 */
public abstract class BasePackSimActivity extends AppCompatActivity {

    /** Each set supplies its pools, prefs prefix, layout and pack art here. */
    protected abstract PackSimConfig getConfig();

    private FrameLayout cardContainer;
    private FrameLayout resultContainer;
    private Button resetButton;
    private Button backButton;
    private TextView tvPacksOpened;
    private long lastClickTime = 0;
    private boolean pity;
    private final List<Integer> pulledCards = new ArrayList<>();
    private final List<Boolean> isNew = new ArrayList<>();
    private int[] cardResources;
    private CardViewModel cardViewModel;
    private CollectionRepository collectionRepository;
    private UserRepository userRepository;
    private CardImageLoader cardImageLoader;
    private String rarity;
    private boolean isAA = false;

    private PackSimConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = getConfig();

        EdgeToEdge.enable(this);
        setContentView(config.layoutResId);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> {
            resetButton.clearAnimation(); // Animation needs to be cleared so that button is not permanently visible for animation
            resetButton.setVisibility(View.GONE);
            backButton.clearAnimation();
            backButton.setVisibility(View.GONE);
            resultContainer.removeAllViews();
            resultContainer.setVisibility(View.GONE);
            pulledCards.clear();
            isNew.clear();
            handleCardStack();
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        cardContainer = findViewById(R.id.cardContainer);
        resultContainer = findViewById(R.id.resultContainer);
        pity = false;

        // Collection and currency state via repositories (still SharedPreferences-backed).
        collectionRepository = ServiceLocator.get(this).collectionRepository();
        userRepository = ServiceLocator.get(this).userRepository();
        cardImageLoader = ServiceLocator.get(this).cardImageLoader();

        tvPacksOpened = findViewById(R.id.tvPacksOpened);
        handleCardStack();

        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);
    }

    private void handleCardStack() {
        // Increment pack count
        int packsOpened = collectionRepository.incrementPacksOpened(config.prefix);
        tvPacksOpened.setText(String.valueOf(packsOpened));

        // Reset flags
        isAA = false;

        // Handle pity system
        handlePitySystem();

        if (!pity) {
            // Handling last (feature) card
            if (Math.random() < 0.50) {
                cardResources = config.rCards;
                rarity = "R";
            } else {
                double prob = Math.random();
                if (config.hasUltra() && prob < (1.0 / 11520)) {
                    cardResources = config.ultraCards;
                    rarity = config.ultraRarity;
                } else if (prob < (1.0 / 1728)) {
                    cardResources = config.mrCards;
                    rarity = "MR";
                } else if (prob < ((1.0 / 1728) + (1.0 / 144))) {
                    cardResources = config.secCards;
                    rarity = "SEC";
                } else if (prob < ((1.0 / 1728) + (1.0 / 144) + (1.0 / 72))) {
                    if (config.hasSpSplit() && Math.random() < 0.5) {
                        cardResources = config.spCards;
                        rarity = "SP";
                    } else {
                        cardResources = config.aalCards;
                        rarity = "L";
                        isAA = true;
                    }
                } else if (prob < ((1.0 / 1728) + (1.0 / 144) + (1.0 / 72) + (2.0 / 48))) { // 1.0 / 48 -> 2.0 / 48
                    if (Math.random() < 0.33) {
                        cardResources = config.aasecCards;
                        rarity = "SEC";
                    } else if (Math.random() < 0.66) {
                        cardResources = config.aasrCards;
                        rarity = "SR";
                    } else {
                        cardResources = config.aarCards;
                        rarity = "R";
                    }
                    isAA = true;
                } else {
                    if (Math.random() < 0.75) {
                        cardResources = config.srCards;
                        rarity = "SR";
                    } else {
                        cardResources = config.lCards;
                        rarity = "L";
                    }
                }
            }
        }

        pity = false;

        addFeatureCard();
        addGuaranteedRareCard();
        addCommonCard();
        addCommonCard();
        addCommonCard();
        addCommonCard();
        addPack();
    }

    private void handlePitySystem() {
        int packsOpened = collectionRepository.getPacksOpened(config.prefix);
        if (packsOpened > 0) {
            if (config.hasUltra() && packsOpened % 11520 == 0) {
                cardResources = config.ultraCards;
                rarity = config.ultraRarity;
                pity = true;
            } else if (packsOpened % 1728 == 0) {
                cardResources = config.mrCards;
                rarity = "MR";
                pity = true;
            } else if (packsOpened % 288 == 0) {
                if (config.hasSpSplit() && Math.random() < 0.5) {
                    cardResources = config.spCards;
                    rarity = "SP";
                } else {
                    cardResources = config.aalCards;
                    rarity = "L";
                    isAA = true;
                }
                pity = true;
            } else if (packsOpened % 24 == 0) {
                if (Math.random() < 0.99) {
                    if (Math.random() < 0.1) {
                        cardResources = config.secCards;
                        rarity = "SEC";
                    } else if (Math.random() < 0.5) {
                        cardResources = config.aasrCards;
                        isAA = true;
                        rarity = "SR";
                    } else {
                        cardResources = config.aarCards;
                        isAA = true;
                        rarity = "R";
                    }
                    pity = true;
                } else {
                    cardResources = config.aasecCards;
                    isAA = true;
                    rarity = "SEC";
                    pity = true;
                }
            }
        }
    }

    /** The variable-rarity "feature" slot (card 6). */
    private void addFeatureCard() {
        final boolean featureIsAA = isAA;
        final String rarity6 = rarity;
        int randomCard = pickUnique(cardResources);
        final String cardId6 = getResources().getResourceEntryName(randomCard);
        ImageView card6 = buildCardView(randomCard);

        if (!collectionRepository.isCollected(cardId6)) {
            isNew.add(true);
        } else {
            isNew.add(false);
            int reward = berryRewardFor(rarity6, featureIsAA);
            if (reward != 0) {
                userRepository.addBerries(reward);
            }
        }

        card6.setOnClickListener(v -> {
            animate(card6);

            // Set delay to prevent background touches from interfering with animation (reset button)
            if (System.currentTimeMillis() - lastClickTime < 1000) {
                return;
            }
            lastClickTime = System.currentTimeMillis();
            showButtons();

            collectionRepository.recordRevealedCard(cardId6, config.prefix, raritySuffixFor(rarity6));
        });
    }

    /** The guaranteed-R slot (card 5). */
    private void addGuaranteedRareCard() {
        cardResources = config.rCards;
        rarity = "R";
        int randomCard = pickUnique(cardResources);
        final String cardId5 = getResources().getResourceEntryName(randomCard);
        ImageView card5 = buildCardView(randomCard);

        if (!collectionRepository.isCollected(cardId5)) {
            isNew.add(true);
        } else {
            isNew.add(false);
            userRepository.addBerries(300);
        }

        card5.setOnClickListener(v -> {
            animate(card5);
            collectionRepository.recordRevealedCard(cardId5, config.prefix, "r");
        });
    }

    /** A common slot (cards 1-4): UC at 33%, otherwise C. */
    private void addCommonCard() {
        if (Math.random() < 0.33) {
            cardResources = config.ucCards;
            rarity = "UC";
        } else {
            cardResources = config.cCards;
            rarity = "C";
        }
        int randomCard = pickUnique(cardResources);
        final String cardId = getResources().getResourceEntryName(randomCard);
        final String rarityC = rarity;
        ImageView card = buildCardView(randomCard);

        if (!collectionRepository.isCollected(cardId)) {
            isNew.add(true);
            collectionRepository.recordNewCommon(cardId, config.prefix, rarityC.equals("C") ? "c" : "uc");
        } else {
            isNew.add(false);
            if (rarityC.equals("C")) {
                userRepository.addBerries(100);
            } else if (rarityC.equals("UC")) {
                userRepository.addBerries(200);
            }
        }

        card.setOnClickListener(v -> {
            animate(card);
            collectionRepository.incrementCount(cardId);
        });
    }

    private void addPack() {
        ImageView pack = new ImageView(this);
        // Load synchronously (it's a single local drawable). With Glide, on a cold first
        // pack the art could lag behind the 6 card decodes and the on-top pack would show
        // blank until the next pack warmed the cache. setImageResource shows it immediately.
        pack.setImageResource(config.packDrawable);
        pack.setLayoutParams(new FrameLayout.LayoutParams(1350, 2700));
        pack.setScaleX(config.packScale);
        pack.setScaleY(config.packScale);
        cardContainer.addView(pack);
        pack.setOnClickListener(v -> animate(pack));
    }

    /** Builds a card ImageView, loads its art and adds it to the stack. */
    private ImageView buildCardView(int randomCard) {
        ImageView card = new ImageView(this);
        cardImageLoader.loadById(getResources().getResourceEntryName(randomCard), card);
        card.setLayoutParams(new FrameLayout.LayoutParams(890, 2700));
        card.setTranslationX(230);
        cardContainer.addView(card);
        return card;
    }

    /** Picks a resource from the pool that has not already been pulled this pack. */
    private int pickUnique(int[] pool) {
        int randomIndex, randomCard;
        do {
            randomIndex = (int) (Math.random() * pool.length);
            randomCard = pool[randomIndex];
        } while (pulledCards.contains(randomCard));
        pulledCards.add(randomCard);
        return randomCard;
    }

    /** Berry reward when a duplicate is pulled, by rarity (matches the original switch). */
    private int berryRewardFor(String r, boolean aa) {
        switch (r) {
            case "R":
                return aa ? 1000 : 300;
            case "SR":
                return aa ? 3000 : 500;
            case "L":
                return aa ? 30000 : 300;
            case "SEC":
                return aa ? 10000 : 5000;
            case "MR":
                return 50000;
            case "SP":
                return 30000;
            default:
                if (config.ultraRarity != null && config.ultraRarity.equals(r)) {
                    return 3000000;
                }
                return 0;
        }
    }

    /** Maps a rarity to its per-rarity counter suffix, or null if it has no counter. */
    private String raritySuffixFor(String r) {
        switch (r) {
            case "C":
                return "c";
            case "UC":
                return "uc";
            case "R":
                return "r";
            case "SR":
                return "sr";
            case "L":
                return "l";
            case "SEC":
                return "sec";
            case "MR":
                return "mr";
            case "SP":
                return "sp";
            default:
                if (config.ultraRarity != null && config.ultraRarity.equals(r)) {
                    return config.ultraRarity.toLowerCase();
                }
                return null;
        }
    }

    private void animate(ImageView card) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        ObjectAnimator translationX = ObjectAnimator.ofFloat(card, "translationX", -screenWidth * 3);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(card, "translationY", screenHeight / 2);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(card, "rotation", 0f, -90f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationX, translationY, rotation);
        animatorSet.setDuration(1000);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cardContainer.removeView(card);
            }
        });

        animatorSet.start();
    }

    private void showButtons() {
        resetButton.setVisibility(View.VISIBLE);
        TranslateAnimation slideResetUp = new TranslateAnimation(0, 0, resetButton.getHeight() + getResources().getDisplayMetrics().heightPixels, 0);
        slideResetUp.setInterpolator(new AccelerateDecelerateInterpolator());
        slideResetUp.setDuration(500);
        slideResetUp.setFillAfter(true);
        resetButton.startAnimation(slideResetUp);

        backButton.setVisibility(View.VISIBLE);
        TranslateAnimation slideBackUp = new TranslateAnimation(0, 0, backButton.getHeight() + getResources().getDisplayMetrics().heightPixels, 0);
        slideBackUp.setInterpolator(new AccelerateDecelerateInterpolator());
        slideBackUp.setDuration(500);
        slideBackUp.setFillAfter(true);
        backButton.startAnimation(slideBackUp);

        showResults();
    }

    @Override
    public void onBackPressed() {
    }

    private void showResults() {
        GridLayout gridLayout = new GridLayout(this);
        FrameLayout.LayoutParams gridParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // Full width
                ViewGroup.LayoutParams.WRAP_CONTENT  // Adjust height dynamically
        );

        // Add padding to the grid layout (for left and right spacing)
        gridParams.setMargins(32, 650, 32, 16); // Left, Top, Right, Bottom margins
        gridLayout.setLayoutParams(gridParams);
        gridLayout.setColumnCount(3); // 3 columns for a 2x3 layout
        gridLayout.setRowCount(2);    // 2 rows
        gridLayout.setPadding(16, 16, 16, 16); // Internal padding

        // Add the cards in reverse order
        for (int i = pulledCards.size() - 1; i >= 0; i--) {
            int cardResourceId = pulledCards.get(i);
            boolean isNewCard = isNew.get(i);

            // Create a FrameLayout to hold both the card and the symbol
            FrameLayout cardFrame = new FrameLayout(this);

            // Create ImageView for the card
            ImageView cardView = new ImageView(this);
            cardImageLoader.loadById(getResources().getResourceEntryName(cardResourceId), cardView);

            // Set proper size for the card (adjust size as necessary)
            GridLayout.LayoutParams cardParams = new GridLayout.LayoutParams();
            cardParams.width = 400; // Use 0 width and weight to fill the cell equally
            cardParams.height = 600; // Fixed height as before
            cardParams.setMargins(8, 0, 8, 0); // Margins around the card
            cardParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Fill column equally
            cardParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED); // Fill row equally
            cardView.setLayoutParams(cardParams);
            cardView.setScaleType(ImageView.ScaleType.FIT_CENTER); // Scale the image to fit inside the bounds
            cardView.setPadding(2, 0, 2, 0); // Padding for the card

            // Create ImageView for the symbol (circle or any other symbol)
            ImageView symbolView = new ImageView(this);
            Glide.with(this).load(R.drawable.ic_new).into(symbolView);
            FrameLayout.LayoutParams symbolParams = new FrameLayout.LayoutParams(
                    80, 80 // Size of the symbol (adjust as needed)
            );
            symbolParams.leftMargin = -3; // Distance from the right edge
            symbolParams.topMargin = 0;   // Distance from the top edge
            symbolView.setLayoutParams(symbolParams);

            // Add both the card and the symbol to the FrameLayout
            cardFrame.addView(cardView);

            if (isNewCard) {
                cardFrame.addView(symbolView);
            }

            // Allow card popup on pressing card
            cardFrame.setOnClickListener(v -> showCardPopup(cardResourceId));

            // Add the cardFrame (which contains both card and symbol) to the GridLayout
            gridLayout.addView(cardFrame);
        }

        // Add the grid layout to the container
        resultContainer.removeAllViews(); // Clear previous grids if any
        resultContainer.addView(gridLayout);
        resultContainer.setVisibility(View.VISIBLE);

        // Optional: Add an animation for the grid appearance
        TranslateAnimation slideUp = new TranslateAnimation(0, 0, resultContainer.getHeight(), 0);
        slideUp.setInterpolator(new AccelerateDecelerateInterpolator());
        slideUp.setDuration(500);
        slideUp.setFillAfter(true);
        resultContainer.startAnimation(slideUp);
    }

    private void showCardPopup(int cardResId) {
        // Create a dimmed full-screen overlay
        FrameLayout overlay = new FrameLayout(this);
        overlay.setBackgroundColor(Color.parseColor("#AA000000"));
        overlay.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Create the enlarged card view
        ImageView largeCard = new ImageView(this);
        cardImageLoader.loadById(getResources().getResourceEntryName(cardResId), largeCard);
        FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(
                1000, 1500  // Adjust size as needed
        );
        cardParams.gravity = Gravity.CENTER;
        largeCard.setLayoutParams(cardParams);
        largeCard.setScaleType(ImageView.ScaleType.FIT_CENTER);
        largeCard.setPadding(16, 16, 16, 16);

        // Add the card to the overlay
        overlay.addView(largeCard);

        // Add close-on-tap functionality
        overlay.setOnClickListener(v -> ((ViewGroup) overlay.getParent()).removeView(overlay));

        // Add the overlay to your root container
        ((ViewGroup) findViewById(android.R.id.content)).addView(overlay);

        // Optional: Fade in animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(200);
        overlay.startAnimation(fadeIn);
    }
}
