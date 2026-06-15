package com.optcg.app.sync;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.optcg.app.data.local.AppCounterEntity;
import com.optcg.app.data.local.CollectionEntryEntity;
import com.optcg.app.data.local.PortfolioSnapshotEntity;
import com.optcg.app.data.local.UserDataDao;
import com.optcg.app.data.local.UserDataStore;
import com.optcg.app.util.AppExecutors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Two-way sync of local user data to Firestore under {@code users/{uid}/...}. Conflict
 * resolution is last-write-wins by {@code updatedAt}. Runs on a background thread and
 * blocks on Firestore tasks; refreshes the in-memory mirror afterwards.
 *
 * <p>Limitations (documented for follow-up): no real-time listeners (syncs on sign-in,
 * app start, and manual triggers); deletions are not propagated; berries are client-trusted.
 */
public class FirestoreSyncManager implements SyncManager {

    private static final String TAG = "FirestoreSync";
    private static final int BATCH_LIMIT = 400;

    private final FirebaseFirestore firestore;
    private final AuthManager authManager;
    private final UserDataDao dao;
    private final UserDataStore store;
    private final AppExecutors executors;

    public FirestoreSyncManager(FirebaseFirestore firestore, AuthManager authManager,
                                UserDataDao dao, UserDataStore store, AppExecutors executors) {
        this.firestore = firestore;
        this.authManager = authManager;
        this.dao = dao;
        this.store = store;
        this.executors = executors;
    }

    @Override
    public boolean isEnabled() {
        return authManager.isSignedIn();
    }

    @Override
    public void syncNow() {
        if (!authManager.isSignedIn()) {
            return;
        }
        executors.networkIO().execute(this::runSync);
    }

    private void runSync() {
        UserSession session = authManager.currentSession();
        if (session.userId == null) {
            return;
        }
        String uid = session.userId;
        try {
            DocumentReference userDoc = firestore.collection("users").document(uid);
            syncEntries(userDoc.collection("collection"), uid);
            syncCounters(userDoc.collection("counters"), uid);
            syncSnapshots(userDoc.collection("portfolio"), uid);
            store.reload();
        } catch (Exception e) {
            Log.e(TAG, "Sync failed", e);
        }
    }

    private void syncEntries(CollectionReference ref, String uid) throws Exception {
        Map<String, DocumentSnapshot> remote = byId(Tasks.await(ref.get()));
        Map<String, CollectionEntryEntity> local = new HashMap<>();
        for (CollectionEntryEntity e : dao.getAllEntries()) {
            local.put(e.cardId, e);
        }
        Set<String> ids = union(remote.keySet(), local.keySet());
        List<WriteOp> pushes = new ArrayList<>();
        for (String id : ids) {
            CollectionEntryEntity l = local.get(id);
            DocumentSnapshot r = remote.get(id);
            long lu = l != null ? l.updatedAt : -1;
            long ru = r != null ? getLong(r, "updatedAt", -1) : -1;
            if (lu > ru) {
                Map<String, Object> data = new HashMap<>();
                data.put("collected", l.collected);
                data.put("count", l.count);
                data.put("updatedAt", l.updatedAt);
                data.put("ownerId", uid);
                pushes.add(new WriteOp(ref.document(id), data));
                dao.upsertEntry(new CollectionEntryEntity(id, l.collected, l.count, uid, l.updatedAt, false));
            } else if (ru > lu) {
                boolean collected = Boolean.TRUE.equals(r.getBoolean("collected"));
                int count = (int) getLong(r, "count", 0);
                dao.upsertEntry(new CollectionEntryEntity(id, collected, count, uid, ru, false));
            }
        }
        commitInChunks(pushes);
    }

    private void syncCounters(CollectionReference ref, String uid) throws Exception {
        Map<String, DocumentSnapshot> remote = byId(Tasks.await(ref.get()));
        Map<String, AppCounterEntity> local = new HashMap<>();
        for (AppCounterEntity c : dao.getAllCounters()) {
            local.put(c.key, c);
        }
        Set<String> ids = union(remote.keySet(), local.keySet());
        List<WriteOp> pushes = new ArrayList<>();
        for (String id : ids) {
            AppCounterEntity l = local.get(id);
            DocumentSnapshot r = remote.get(id);
            long lu = l != null ? l.updatedAt : -1;
            long ru = r != null ? getLong(r, "updatedAt", -1) : -1;
            if (lu > ru) {
                Map<String, Object> data = new HashMap<>();
                data.put("value", l.value);
                data.put("updatedAt", l.updatedAt);
                data.put("ownerId", uid);
                pushes.add(new WriteOp(ref.document(id), data));
                dao.upsertCounter(new AppCounterEntity(id, l.value, uid, l.updatedAt, false));
            } else if (ru > lu) {
                int value = (int) getLong(r, "value", 0);
                dao.upsertCounter(new AppCounterEntity(id, value, uid, ru, false));
            }
        }
        commitInChunks(pushes);
    }

    private void syncSnapshots(CollectionReference ref, String uid) throws Exception {
        Map<String, DocumentSnapshot> remote = byId(Tasks.await(ref.get()));
        Map<String, PortfolioSnapshotEntity> local = new HashMap<>();
        for (PortfolioSnapshotEntity s : dao.getAllSnapshots()) {
            local.put(s.date, s);
        }
        Set<String> ids = union(remote.keySet(), local.keySet());
        List<WriteOp> pushes = new ArrayList<>();
        for (String id : ids) {
            PortfolioSnapshotEntity l = local.get(id);
            DocumentSnapshot r = remote.get(id);
            long lu = l != null ? l.updatedAt : -1;
            long ru = r != null ? getLong(r, "updatedAt", -1) : -1;
            if (lu > ru) {
                Map<String, Object> data = new HashMap<>();
                data.put("value", l.value);
                data.put("updatedAt", l.updatedAt);
                data.put("ownerId", uid);
                pushes.add(new WriteOp(ref.document(id), data));
                dao.upsertSnapshot(new PortfolioSnapshotEntity(id, l.value, uid, l.updatedAt, false));
            } else if (ru > lu) {
                double value = getDouble(r, "value", 0d);
                dao.upsertSnapshot(new PortfolioSnapshotEntity(id, (float) value, uid, ru, false));
            }
        }
        commitInChunks(pushes);
    }

    private void commitInChunks(List<WriteOp> ops) throws Exception {
        for (int i = 0; i < ops.size(); i += BATCH_LIMIT) {
            WriteBatch batch = firestore.batch();
            int end = Math.min(i + BATCH_LIMIT, ops.size());
            for (int j = i; j < end; j++) {
                batch.set(ops.get(j).ref, ops.get(j).data);
            }
            Tasks.await(batch.commit());
        }
    }

    private static Map<String, DocumentSnapshot> byId(QuerySnapshot qs) {
        Map<String, DocumentSnapshot> map = new HashMap<>();
        for (DocumentSnapshot d : qs.getDocuments()) {
            map.put(d.getId(), d);
        }
        return map;
    }

    private static Set<String> union(Set<String> a, Set<String> b) {
        Set<String> s = new HashSet<>(a);
        s.addAll(b);
        return s;
    }

    private static long getLong(DocumentSnapshot d, String field, long def) {
        Long v = d.getLong(field);
        return v != null ? v : def;
    }

    private static double getDouble(DocumentSnapshot d, String field, double def) {
        Double v = d.getDouble(field);
        return v != null ? v : def;
    }

    private static class WriteOp {
        final DocumentReference ref;
        final Map<String, Object> data;

        WriteOp(DocumentReference ref, Map<String, Object> data) {
            this.ref = ref;
            this.data = data;
        }
    }
}
