package com.optcg.app.sync;

/**
 * Default {@link SyncManager} used while the app is local-only: it performs no sync. Swap
 * for a real implementation when the cloud backend and authentication are added.
 */
public class NoOpSyncManager implements SyncManager {

    @Override
    public void syncNow() {
        // No-op until a cloud backend exists.
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
