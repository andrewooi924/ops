package com.optcg.app.data.remote;

import com.optcg.app.domain.model.PriceDetail;
import com.optcg.app.domain.model.PriceQuote;

/**
 * Remote source of price data. Today this is implemented by the scraper; tomorrow it can
 * be a cloud API. The repository depends only on this interface, so swapping the backend
 * is a one-line change in the {@code ServiceLocator}.
 */
public interface RemotePriceDataSource {

    /** A current price reading, or {@code null} if no source handles the URL. Call off the main thread. */
    PriceQuote fetchQuote(String url) throws Exception;

    /** Historical trend + per-vendor listing for the price dialog. Call off the main thread. */
    PriceDetail fetchDetail(String url) throws Exception;
}
