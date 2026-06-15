package com.optcg.app.data.repository;

import com.optcg.app.data.local.LocalPriceDataSource;
import com.optcg.app.data.local.PriceQuoteEntity;
import com.optcg.app.data.remote.RemotePriceDataSource;
import com.optcg.app.domain.model.PriceDetail;
import com.optcg.app.domain.model.PriceQuote;
import com.optcg.app.domain.result.Resource;
import com.optcg.app.util.AppExecutors;

/**
 * Default {@link PriceRepository}: an offline-first cache over a remote source.
 *
 * <p>Reads serve the Room-cached quote immediately, then refresh from the remote source
 * (scraper today) if the cache is missing or older than {@link #STALE_MS}, persisting the
 * fresh value back to Room. This replaces the per-adapter in-memory HashMap caches with a
 * single shared, persistent cache that also works offline.
 */
public class PriceRepositoryImpl implements PriceRepository {

    /** How long a cached quote is considered fresh before a background refresh. */
    private static final long STALE_MS = 60 * 60 * 1000L; // 1 hour

    private final LocalPriceDataSource local;
    private final RemotePriceDataSource remote;
    private final AppExecutors executors;

    public PriceRepositoryImpl(LocalPriceDataSource local, RemotePriceDataSource remote, AppExecutors executors) {
        this.local = local;
        this.remote = remote;
        this.executors = executors;
    }

    @Override
    public void getQuote(String url, RepositoryCallback<PriceQuote> callback) {
        executors.diskIO().execute(() -> {
            PriceQuoteEntity cached = local.getQuote(url);
            PriceQuote cachedQuote = cached != null ? toQuote(cached) : null;
            boolean fresh = cached != null && (System.currentTimeMillis() - cached.fetchedAt) < STALE_MS;

            if (cachedQuote != null) {
                deliver(callback, Resource.loading(cachedQuote));
            }
            if (fresh) {
                deliver(callback, Resource.success(cachedQuote));
                return;
            }

            executors.networkIO().execute(() -> {
                try {
                    PriceQuote fetched = remote.fetchQuote(url);
                    if (fetched != null) {
                        executors.diskIO().execute(() -> local.save(toEntity(fetched)));
                        deliver(callback, Resource.success(fetched));
                    } else {
                        deliver(callback, Resource.error("No price source for URL", cachedQuote));
                    }
                } catch (Exception e) {
                    deliver(callback, Resource.error(e.getMessage(), cachedQuote));
                }
            });
        });
    }

    @Override
    public PriceQuote getQuoteSync(String url) {
        try {
            PriceQuote fetched = remote.fetchQuote(url);
            if (fetched != null) {
                local.save(toEntity(fetched));
                return fetched;
            }
        } catch (Exception e) {
            // fall through to cache
        }
        PriceQuoteEntity cached = local.getQuote(url);
        return cached != null ? toQuote(cached) : null;
    }

    @Override
    public void getPriceDetail(String url, RepositoryCallback<PriceDetail> callback) {
        executors.networkIO().execute(() -> {
            try {
                PriceDetail detail = remote.fetchDetail(url);
                deliver(callback, Resource.success(detail));
            } catch (Exception e) {
                deliver(callback, Resource.error(e.getMessage(), null));
            }
        });
    }

    private <T> void deliver(RepositoryCallback<T> callback, Resource<T> result) {
        executors.mainThread().execute(() -> callback.onResult(result));
    }

    private static PriceQuote toQuote(PriceQuoteEntity e) {
        return new PriceQuote(e.url, e.avgYen, e.avgDisplayText, e.soaringYen, e.soaringText,
                e.crashYen, e.crashText, e.fetchedAt);
    }

    private static PriceQuoteEntity toEntity(PriceQuote q) {
        return new PriceQuoteEntity(q.url, q.avgYen, q.avgDisplayText, q.soaringYen, q.soaringText,
                q.crashYen, q.crashText, q.fetchedAt);
    }
}
