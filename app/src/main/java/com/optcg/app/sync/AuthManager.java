package com.optcg.app.sync;

import android.content.Intent;

import androidx.lifecycle.LiveData;

/**
 * Authentication abstraction for Google Sign-In / account linking. UI and the sync layer
 * depend only on this interface; the concrete implementation is chosen in the
 * {@code ServiceLocator}. {@link FirebaseAuthManager} is the real implementation;
 * {@link LocalAuthManager} is a signed-out, device-only fallback.
 */
public interface AuthManager {

    UserSession currentSession();

    boolean isSignedIn();

    /** Observable session, so UI can react to sign-in / sign-out. */
    LiveData<UserSession> session();

    /** Intent to launch the Google sign-in flow (via an ActivityResultLauncher). */
    Intent getSignInIntent();

    /** Handle the result returned by the sign-in intent. */
    void handleSignInResult(Intent data, AuthCallback callback);

    void signOut(Runnable onComplete);

    interface AuthCallback {
        void onComplete(boolean success, String error);
    }
}
