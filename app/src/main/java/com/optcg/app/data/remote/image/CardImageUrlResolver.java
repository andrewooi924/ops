package com.optcg.app.data.remote.image;

/**
 * Maps a card's relative image path (e.g. {@code "cards/op01/op01_041.png"}) to a full URL.
 * This is the single swap point for the image backend: today {@link StorageImageUrlResolver}
 * targets Firebase Storage; a CDN later means providing a different resolver in the
 * {@code ServiceLocator}, with no changes to callers.
 */
public interface CardImageUrlResolver {

    /** Full URL for the given relative image path, or {@code null} if the path is empty. */
    String urlFor(String imgPath);
}
