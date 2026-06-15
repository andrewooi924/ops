package com.optcg.app.domain.model;

/**
 * A point-in-time price reading for a single card page, in yen. Carries both the parsed
 * numeric values (for currency math) and the exact display strings the original UI showed
 * in parentheses, so screens reproduce identical text without knowing where the data came
 * from (scraper today, cloud later).
 */
public class PriceQuote {

    public final String url;
    /** Average price parsed to an int (yen). */
    public final int avgYen;
    /** Exact text shown in parentheses next to the average (e.g. "1,234円"). */
    public final String avgDisplayText;
    /** Monthly "soaring" movement (yen); 0 when unavailable. */
    public final int soaringYen;
    /** Raw soaring text (e.g. "1,234円" or "0円"). */
    public final String soaringText;
    /** Monthly "crash" movement (yen); 0 when unavailable. */
    public final int crashYen;
    /** Raw crash text (e.g. "1,234円" or "0円"). */
    public final String crashText;
    /** When this quote was fetched (epoch millis). */
    public final long fetchedAt;

    public PriceQuote(String url, int avgYen, String avgDisplayText,
                      int soaringYen, String soaringText,
                      int crashYen, String crashText, long fetchedAt) {
        this.url = url;
        this.avgYen = avgYen;
        this.avgDisplayText = avgDisplayText;
        this.soaringYen = soaringYen;
        this.soaringText = soaringText;
        this.crashYen = crashYen;
        this.crashText = crashText;
        this.fetchedAt = fetchedAt;
    }
}
