package com.optcg.app.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Shared application executors. Centralizes the threading that was previously
 * scattered as ad-hoc {@code new Thread(...)} / per-class {@code ExecutorService}
 * instances across the UI. Repositories and data sources use these instead.
 */
public class AppExecutors {

    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainThread;

    public AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public AppExecutors() {
        this(Executors.newSingleThreadExecutor(),
                Executors.newFixedThreadPool(4),
                new MainThreadExecutor());
    }

    /** Serialized disk / database work. */
    public Executor diskIO() {
        return diskIO;
    }

    /** Network (scraper) work, bounded pool. */
    public Executor networkIO() {
        return networkIO;
    }

    /** Posts back to the Android main thread. */
    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
