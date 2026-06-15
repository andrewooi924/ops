package com.optcg.app.sync;

/**
 * Cloud-sync seam for pushing/pulling user data (collections, decks, wishlists, portfolio,
 * future AI feedback) once a backend and {@link AuthManager} exist. The default
 * {@link NoOpSyncManager} does nothing, so the app runs fully offline today.
 */
public interface SyncManager {

    /** Triggers a sync with the cloud. No-op until a backend is wired in. */
    void syncNow();

    boolean isEnabled();
}
