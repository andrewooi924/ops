package com.optcg.app.data.repository;

import com.optcg.app.data.local.UserDataStore;

/**
 * Room-backed {@link UserRepository} (via {@link UserDataStore}). Berries are stored as a
 * named counter; behaviour is identical to the original "berries" preference.
 */
public class UserRepositoryImpl implements UserRepository {

    private static final String KEY_BERRIES = "berries";

    private final UserDataStore store;

    public UserRepositoryImpl(UserDataStore store) {
        this.store = store;
    }

    @Override
    public int getBerries() {
        return store.getCounter(KEY_BERRIES);
    }

    @Override
    public void addBerries(int amount) {
        store.setCounter(KEY_BERRIES, store.getCounter(KEY_BERRIES) + amount);
    }
}
