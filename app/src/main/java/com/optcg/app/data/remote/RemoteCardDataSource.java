package com.optcg.app.data.remote;

import com.optcg.app.Card;

import java.util.List;

/**
 * Remote source of card metadata (the cloud card catalog). The repository checks
 * {@link #fetchCatalogVersion()} and only pulls the full catalog when the remote version is
 * newer, then upserts it into the local Room cache. Today this is the seam for the "updated
 * card database" feature; {@link com.optcg.app.data.remote.FirestoreCardDataSource} is the
 * implementation.
 */
public interface RemoteCardDataSource {

    /** Current catalog version on the backend (0 if none). Call off the main thread. */
    long fetchCatalogVersion() throws Exception;

    /** The full card catalog. Call off the main thread. */
    List<Card> fetchAllCards() throws Exception;
}
