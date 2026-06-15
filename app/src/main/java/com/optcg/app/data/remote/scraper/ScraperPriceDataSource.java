package com.optcg.app.data.remote.scraper;

import static org.jsoup.internal.StringUtil.isNumeric;

import android.util.Log;

import com.optcg.app.VendorPrice;
import com.optcg.app.data.remote.RemotePriceDataSource;
import com.optcg.app.domain.model.PriceDetail;
import com.optcg.app.domain.model.PriceQuote;
import com.optcg.app.domain.model.PriceTrendPoint;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link RemotePriceDataSource} backed by the existing site scrapers. Quote fetching is
 * delegated to per-host {@link PriceSource} strategies; the richer trend/vendor detail
 * (only shown for atari cards) is parsed from a single page fetch.
 *
 * <p>This is the temporary remote implementation — replacing it with a cloud backend means
 * providing a different {@link RemotePriceDataSource} in the {@code ServiceLocator}.
 */
public class ScraperPriceDataSource implements RemotePriceDataSource {

    private final ScraperHtmlClient client;
    private final List<PriceSource> sources;

    public ScraperPriceDataSource(ScraperHtmlClient client) {
        this.client = client;
        this.sources = Arrays.asList(
                new AtariPriceSource(client),
                new TierOnePriceSource(client)
        );
    }

    @Override
    public PriceQuote fetchQuote(String url) throws Exception {
        for (PriceSource source : sources) {
            if (source.handles(url)) {
                return source.fetchQuote(url);
            }
        }
        return null;
    }

    @Override
    public PriceDetail fetchDetail(String url) throws Exception {
        Document doc = client.fetch(url);
        return new PriceDetail(parseTrend(doc), parseVendors(doc));
    }

    private List<PriceTrendPoint> parseTrend(Document document) {
        List<PriceTrendPoint> points = new ArrayList<>();
        try {
            Elements rows = document.select("table.table_info tbody tr");
            for (int i = rows.size() - 9; i >= 0; i--) {
                Element row = rows.get(i);
                String priceStr = row.select("td").get(0).text().replace("円", "").replace(",", "");
                float price = Float.parseFloat(priceStr);
                points.add(new PriceTrendPoint((rows.size() - 9) - i, price)); // index as X value
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return points;
    }

    private List<VendorPrice> parseVendors(Document document) {
        List<VendorPrice> vendorPrices = new ArrayList<>();
        try {
            Elements rows = document.select("table.table_info tbody tr");
            for (Element row : rows) {
                Elements cells = row.select("td");
                if (cells.size() >= 2) {
                    String vendor = cells.get(0).text();
                    String priceStr = cells.get(1).text().replace("円", "").replace(",", "");
                    if (isNumeric(priceStr)) {
                        float price = Float.parseFloat(priceStr);
                        vendorPrices.add(new VendorPrice(vendor, price));
                    } else {
                        Log.w("ScraperPriceDataSource", "Invalid price format: " + priceStr);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vendorPrices;
    }
}
