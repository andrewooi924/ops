package com.optcg.app.sync;

import androidx.annotation.Nullable;

/**
 * Immutable snapshot of the signed-in user. The seam for future Google Sign-In; today the
 * app is always signed out (a local, device-only user).
 */
public class UserSession {

    public final boolean signedIn;
    @Nullable
    public final String userId;
    @Nullable
    public final String displayName;
    @Nullable
    public final String email;

    private UserSession(boolean signedIn, @Nullable String userId, @Nullable String displayName, @Nullable String email) {
        this.signedIn = signedIn;
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
    }

    public static UserSession signedOut() {
        return new UserSession(false, null, null, null);
    }

    public static UserSession signedIn(String userId, String displayName, String email) {
        return new UserSession(true, userId, displayName, email);
    }
}
