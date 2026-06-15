package com.optcg.app.data.remote.scraper;

import com.optcg.app.domain.model.PriceQuote;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Scrapes tier-one-onepiece.jp (a makeshop store): a single listed price, no movement.
 * Mirrors the original {@code fetchCardPricesB} parsing exactly. The displayed text is the
 * numeric value with a "円" suffix appended, as the original UI did.
 */
public class TierOnePriceSource implements PriceSource {

    private final ScraperHtmlClient client;

    public TierOnePriceSource(ScraperHtmlClient client) {
        this.client = client;
    }

    @Override
    public boolean handles(String url) {
        return url != null && url.contains("tier-one");
    }

    @Override
    public PriceQuote fetchQuote(String url) throws Exception {
        Document doc = client.fetch(url);

        Element priceElement = doc.selectFirst(".item-price-wrap .item-price span[data-id^='makeshop-item-price']");
        String numeric = priceElement != null ? priceElement.text().replaceAll("[^\\d]", "") : "0";
        int avgYen = PriceSource.parsePriceToInt(numeric);

        return new PriceQuote(url, avgYen, numeric + "円", 0, "0円", 0, "0円",
                System.currentTimeMillis());
    }
}
