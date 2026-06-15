package com.optcg.app.data.remote.image;

import android.net.Uri;
import android.text.TextUtils;

/**
 * Resolves card image paths to Firebase Storage download URLs of the form
 * {@code <baseUrl><url-encoded path>?alt=media}, which serve publicly readable objects
 * without a token. The base URL is injected (from a string resource) so switching to a CDN
 * is a one-line configuration change.
 */
public class StorageImageUrlResolver implements CardImageUrlResolver {

    private final String baseUrl;

    public StorageImageUrlResolver(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String urlFor(String imgPath) {
        if (TextUtils.isEmpty(imgPath)) {
            return null;
        }
        // Uri.encode percent-encodes path separators ("/" -> "%2F"), which Storage requires.
        return baseUrl + Uri.encode(imgPath) + "?alt=media";
    }
}
