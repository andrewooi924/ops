package com.optcg.app.data.repository;

/**
 * User profile state (currently just the in-app "berries" currency). Backed today by the
 * USER_PREFS SharedPreferences file; the interface is shaped so the backing store can move
 * to Room / a cloud account later without changing callers.
 */
public interface UserRepository {

    int getBerries();

    void addBerries(int amount);
}
