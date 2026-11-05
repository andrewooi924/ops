package com.optcg.app.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.optcg.app.catalog.UpdateResult
import com.optcg.app.container
import com.optcg.app.data.PrintingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Background work: daily catalog checks and offline image downloads. */
class Jobs(context: Context) {
    private val work = WorkManager.getInstance(context)
    private val online = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

    fun scheduleCatalogSync() {
        val request = PeriodicWorkRequestBuilder<CatalogSyncWorker>(1, TimeUnit.DAYS)
            .setConstraints(online)
            .build()
        work.enqueueUniquePeriodicWork("catalog-sync", ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun saveSetImages(setId: String) = saveImages(SET_PREFIX + setId)

    fun saveCollectionImages() = saveImages(COLLECTION)

    /** Progress of a set's offline download: null when none is running. */
    fun setDownload(setId: String): Flow<Progress?> =
        work.getWorkInfosForUniqueWorkFlow(uniqueName(SET_PREFIX + setId)).map { infos ->
            infos.firstOrNull { !it.state.isFinished }?.let {
                Progress(it.progress.getInt(DONE, 0), it.progress.getInt(TOTAL, 0), it.state == WorkInfo.State.ENQUEUED)
            }
        }

    private fun saveImages(target: String) {
        val request = OneTimeWorkRequestBuilder<OfflineImagesWorker>()
            .setConstraints(online)
            .setInputData(workDataOf(TARGET to target))
            .build()
        // Appending means cards added while a download runs are picked up by the next run.
        work.enqueueUniqueWork(uniqueName(target), ExistingWorkPolicy.APPEND_OR_REPLACE, request)
    }

    data class Progress(val done: Int, val total: Int, val waitingForNetwork: Boolean)

    internal companion object {
        const val TARGET = "target"
        const val DONE = "done"
        const val TOTAL = "total"
        const val COLLECTION = "collection"
        const val SET_PREFIX = "set:"
        fun uniqueName(target: String) = "images-$target"
    }
}

class CatalogSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val container = applicationContext.container
        container.catalog.ensureInstalled()
        return when (container.catalog.checkForUpdate()) {
            is UpdateResult.Failed -> if (runAttemptCount < 3) Result.retry() else Result.failure()
            else -> Result.success()
        }
    }
}

class OfflineImagesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val container = applicationContext.container
        val target = inputData.getString(Jobs.TARGET) ?: return Result.failure()
        val printings: List<PrintingEntity> = when {
            target == Jobs.COLLECTION -> container.db.catalog().collectionPrintings()
            target.startsWith(Jobs.SET_PREFIX) -> container.db.catalog().printingsInSet(target.removePrefix(Jobs.SET_PREFIX))
            else -> return Result.failure()
        }
        val saved = container.images.saved.value
        val todo = printings.filter { it.id !in saved && container.images.hasImage(it) }
        var failures = 0
        todo.forEachIndexed { index, printing ->
            setProgress(workDataOf(Jobs.DONE to index, Jobs.TOTAL to todo.size))
            try {
                container.images.save(printing)
            } catch (error: IOException) {
                failures += 1
            }
        }
        return if (failures > 0 && runAttemptCount < 3) Result.retry() else Result.success()
    }
}
