package com.optcg.app.sync;

import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Signed-out, device-only {@link AuthManager} fallback (used if Firebase auth is unavailable).
 * Sign-in is a no-op.
 */
public class LocalAuthManager implements AuthManager {

    private final MutableLiveData<UserSession> session = new MutableLiveData<>(UserSession.signedOut());

    @Override
    public UserSession currentSession() {
        return UserSession.signedOut();
    }

    @Override
    public boolean isSignedIn() {
        return false;
    }

    @Override
    public LiveData<UserSession> session() {
        return session;
    }

    @Override
    public Intent getSignInIntent() {
        return null;
    }

    @Override
    public void handleSignInResult(Intent data, AuthCallback callback) {
        callback.onComplete(false, "Sign-in is not available");
    }

    @Override
    public void signOut(Runnable onComplete) {
        if (onComplete != null) {
            onComplete.run();
        }
    }
}
