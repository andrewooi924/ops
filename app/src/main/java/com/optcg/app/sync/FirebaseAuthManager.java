package com.optcg.app.sync;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Firebase + Google Sign-In implementation of {@link AuthManager}. Exchanges a Google ID
 * token for a Firebase credential; the resulting Firebase {@code uid} is the owner id used to
 * partition synced data per user.
 */
public class FirebaseAuthManager implements AuthManager {

    private final FirebaseAuth firebaseAuth;
    private final GoogleSignInClient googleSignInClient;
    private final MutableLiveData<UserSession> session = new MutableLiveData<>();

    public FirebaseAuthManager(Context context, String webClientId) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();
        this.googleSignInClient = GoogleSignIn.getClient(context.getApplicationContext(), gso);
        session.setValue(toSession(firebaseAuth.getCurrentUser()));
        firebaseAuth.addAuthStateListener(fa -> session.postValue(toSession(fa.getCurrentUser())));
    }

    @Override
    public UserSession currentSession() {
        return toSession(firebaseAuth.getCurrentUser());
    }

    @Override
    public boolean isSignedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    @Override
    public LiveData<UserSession> session() {
        return session;
    }

    @Override
    public Intent getSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    @Override
    public void handleSignInResult(Intent data, AuthCallback callback) {
        try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onComplete(true, null);
                } else {
                    String msg = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                    callback.onComplete(false, msg);
                }
            });
        } catch (ApiException e) {
            callback.onComplete(false, "Google sign-in failed (code " + e.getStatusCode() + ")");
        }
    }

    @Override
    public void signOut(Runnable onComplete) {
        firebaseAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    private UserSession toSession(FirebaseUser user) {
        return user == null
                ? UserSession.signedOut()
                : UserSession.signedIn(user.getUid(), user.getDisplayName(), user.getEmail());
    }
}
