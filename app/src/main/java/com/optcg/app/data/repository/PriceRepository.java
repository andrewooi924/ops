package com.optcg.app.data.repository;

import com.optcg.app.domain.model.PriceDetail;
import com.optcg.app.domain.model.PriceQuote;

/**
 * Repository for card prices. UI asks here instead of scraping. Backed by an offline-first
 * Room cache in front of a {@link com.optcg.app.data.remote.RemotePriceDataSource}
 * (currently the scraper). Callbacks are delivered on the main thread.
 */
public interface PriceRepository {

    /**
     * Offline-first quote lookup: emits the cached value immediately (as LOADING) when one
     * exists, then refreshes from the remote source and emits SUCCESS (or ERROR carrying the
     * stale cache). Suitable for adapters/fragments.
     */
    void getQuote(String url, RepositoryCallback<PriceQuote> callback);

    /**
     * Blocking quote lookup for background workers: fetches fresh, falls back to cache.
     * MUST be called off the main thread. May return {@code null} if both fail.
     */
    PriceQuote getQuoteSync(String url);

    /** Trend + vendor detail for the price dialog. */
    void getPriceDetail(String url, RepositoryCallback<PriceDetail> callback);
}
