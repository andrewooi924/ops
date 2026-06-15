package com.optcg.app.data.remote.scraper;

import com.optcg.app.domain.model.PriceQuote;

/**
 * Strategy for scraping a single vendor site. Selection is by URL host, replacing the
 * resource-name-prefix branching the UI used to do inline.
 */
public interface PriceSource {

    boolean handles(String url);

    PriceQuote fetchQuote(String url) throws Exception;

    /** Shared yen-string parser (e.g. "1,234円" -> 1234). */
    static int parsePriceToInt(String priceText) {
        try {
            return Integer.parseInt(priceText.replace(",", "").replace("円", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
