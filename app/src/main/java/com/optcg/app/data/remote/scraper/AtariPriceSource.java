package com.optcg.app.data.remote.scraper;

import com.optcg.app.domain.model.PriceQuote;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Scrapes onepiece-card-atari.jp: average price from the info table plus the monthly
 * soaring/crash movement. Mirrors the original {@code fetchCardPricesA} parsing exactly.
 */
public class AtariPriceSource implements PriceSource {

    private final ScraperHtmlClient client;

    public AtariPriceSource(ScraperHtmlClient client) {
        this.client = client;
    }

    @Override
    public boolean handles(String url) {
        return url != null && url.contains("card-atari");
    }

    @Override
    public PriceQuote fetchQuote(String url) throws Exception {
        Document doc = client.fetch(url);

        String avgRaw = doc.select("table.table_info tbody tr td").first().text();
        int avgYen = PriceSource.parsePriceToInt(avgRaw);

        Element soaringElement = doc.selectFirst(".movement_price_box .soaring");
        Element crashElement = doc.selectFirst(".movement_price_box .crash");

        String soaringText = soaringElement != null
                ? soaringElement.text().replace("月間高騰差額", "").replace("+", "").trim() : "0円";
        String crashText = crashElement != null
                ? crashElement.text().replace("月間暴落差額", "").replace("-", "").trim() : "0円";

        int soaringYen = PriceSource.parsePriceToInt(soaringText);
        int crashYen = PriceSource.parsePriceToInt(crashText);

        // avgRaw is shown verbatim in parentheses by the UI (matches the original behaviour).
        return new PriceQuote(url, avgYen, avgRaw, soaringYen, soaringText, crashYen, crashText,
                System.currentTimeMillis());
    }
}
