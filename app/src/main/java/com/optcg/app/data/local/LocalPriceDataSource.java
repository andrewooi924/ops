package com.optcg.app.data.local;

/**
 * Local (Room) data source for cached price quotes. Wraps {@link PriceQuoteDao}.
 */
public class LocalPriceDataSource {

    private final PriceQuoteDao priceQuoteDao;

    public LocalPriceDataSource(PriceQuoteDao priceQuoteDao) {
        this.priceQuoteDao = priceQuoteDao;
    }

    /** Synchronous read — call off the main thread. */
    public PriceQuoteEntity getQuote(String url) {
        return priceQuoteDao.getByUrl(url);
    }

    public void save(PriceQuoteEntity quote) {
        priceQuoteDao.upsert(quote);
    }
}
