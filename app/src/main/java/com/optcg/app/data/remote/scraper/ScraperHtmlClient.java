package com.optcg.app.data.remote.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Thin wrapper over the HTML fetch used by the scraper sources. Centralizes networking
 * so the rest of the app never calls Jsoup / HttpURLConnection directly. When a real
 * backend exists, the scraper data source (and this client) are simply not used.
 */
public class ScraperHtmlClient {

    /** Fetches and parses a page. Call off the main thread. */
    public Document fetch(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}
