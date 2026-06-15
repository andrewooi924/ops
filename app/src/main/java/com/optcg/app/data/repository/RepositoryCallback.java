package com.optcg.app.data.repository;

import com.optcg.app.domain.result.Resource;

/**
 * One-shot callback used by repositories for non-lifecycle callers (e.g. RecyclerView
 * adapters). Always invoked on the main thread.
 */
public interface RepositoryCallback<T> {
    void onResult(Resource<T> result);
}
