package com.optcg.app.ui.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.optcg.app.data.local.CardImageIndex;
import com.optcg.app.data.remote.image.CardImageUrlResolver;

/**
 * Single entry point for loading card images. Loads from the remote source (resolved by
 * {@link CardImageUrlResolver}) and caches on disk via Glide ({@link DiskCacheStrategy#ALL}),
 * giving offline-first behaviour: the first view downloads, later views (including offline)
 * serve from cache.
 *
 * <p>During the migration it falls back to the bundled drawable (named by card id) when a
 * remote image isn't available; once the bundled PNGs are removed (Phase 3) the fallback
 * simply resolves to nothing/placeholder.
 */
public class CardImageLoader {

    private final CardImageUrlResolver resolver;
    private final CardImageIndex index;

    public CardImageLoader(CardImageUrlResolver resolver, CardImageIndex index) {
        this.resolver = resolver;
        this.index = index;
    }

    /** Load by card id; resolves the image path via the index. */
    public void loadById(String cardId, ImageView into) {
        load(index.pathFor(cardId), cardId, into);
    }

    /**
     * Load by explicit image path (e.g. {@code Card.getImg()}). {@code cardId} is used only to
     * locate the bundled fallback drawable; pass null if not applicable.
     */
    public void load(String imgPath, String cardId, ImageView into) {
        Context ctx = into.getContext();
        String url = imgPath != null ? resolver.urlFor(imgPath) : null;
        RequestBuilder<Drawable> fallback = bundledFallback(ctx, cardId);

        if (url == null) {
            if (fallback != null) {
                fallback.into(into);
            } else {
                Glide.with(ctx).clear(into);
            }
            return;
        }

        RequestBuilder<Drawable> request = Glide.with(ctx)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        if (fallback != null) {
            request = request.error(fallback);
        }
        request.into(into);
    }

    /** A Glide request for the bundled drawable matching the card id, or null if none exists. */
    private RequestBuilder<Drawable> bundledFallback(Context ctx, String cardId) {
        if (cardId == null) {
            return null;
        }
        int resId = ctx.getResources().getIdentifier(cardId, "drawable", ctx.getPackageName());
        if (resId == 0) {
            return null;
        }
        return Glide.with(ctx).load(resId);
    }
}
