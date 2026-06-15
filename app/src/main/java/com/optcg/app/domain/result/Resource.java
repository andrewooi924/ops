package com.optcg.app.domain.result;

import androidx.annotation.Nullable;

/**
 * A generic wrapper describing the state of data that may come from a cache and/or
 * a remote source: it can be loading (optionally with cached data already available),
 * a success, or an error (optionally still carrying stale cached data).
 *
 * <p>This is what repositories hand to the UI so screens can render cached values
 * immediately while a refresh happens in the background (offline-first).
 */
public class Resource<T> {

    public enum Status {LOADING, SUCCESS, ERROR}

    public final Status status;
    @Nullable
    public final T data;
    @Nullable
    public final String message;

    private Resource(Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String message, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, message);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}
