package com.optcg.app.catalog

import android.content.Context
import android.content.SharedPreferences
import com.optcg.app.BuildConfig
import com.optcg.app.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

data class CatalogInfo(val version: String, val generatedAt: String, val source: String, val checkedAt: Long)

sealed interface UpdateResult {
    data object UpToDate : UpdateResult
    data class Updated(val version: String) : UpdateResult
    data object NeedsAppUpdate : UpdateResult
    data object NotConfigured : UpdateResult
    data class Failed(val message: String) : UpdateResult
}

/**
 * Keeps the card catalog in the local database. The app ships with a catalog snapshot
 * (assets/catalog.json.gzip), so it works offline from the first launch; newer catalogs are
 * downloaded when online and replace the snapshot. The user's collection lives in separate
 * tables and is never touched by an update.
 */
class CatalogRepository(
    private val context: Context,
    private val db: AppDatabase,
    private val http: OkHttpClient,
    private val prefs: SharedPreferences,
) {
    private val mutex = Mutex()
    private val profilesFile = File(context.filesDir, "pull_profiles.json")
    private val _info = MutableStateFlow(readInfo())
    val info: StateFlow<CatalogInfo?> = _info.asStateFlow()

    @Volatile private var profiles: PullProfiles? = null

    /** Installs the bundled snapshot on first launch, or after an app update ships a newer one. */
    suspend fun ensureInstalled() = mutex.withLock {
        withContext(Dispatchers.IO) {
            val current = _info.value
            val appVersion = BuildConfig.VERSION_CODE
            if (current != null && prefs.getInt(KEY_BUNDLE_CHECKED, -1) == appVersion &&
                db.catalog().printingCount() > 0
            ) return@withContext
            val bundled = context.assets.open(BUNDLED_ASSET).use(CatalogJson::decodeGzip)
            if (current == null || bundled.generatedAt > current.generatedAt || db.catalog().printingCount() == 0) {
                install(bundled, source = "bundled")
            }
            prefs.edit().putInt(KEY_BUNDLE_CHECKED, appVersion).apply()
        }
    }

    suspend fun checkForUpdate(): UpdateResult = mutex.withLock {
        withContext(Dispatchers.IO) {
            if (BuildConfig.CATALOG_URL.isBlank()) return@withContext UpdateResult.NotConfigured
            try {
                val remoteVersion = fetch(BuildConfig.CATALOG_VERSION_URL) { it.string().trim() }
                markChecked()
                if (remoteVersion == _info.value?.version) return@withContext UpdateResult.UpToDate
                val catalog = fetch(BuildConfig.CATALOG_URL) { body -> CatalogJson.decodeGzip(body.byteStream()) }
                if (catalog.schemaVersion > SUPPORTED_SCHEMA_VERSION) return@withContext UpdateResult.NeedsAppUpdate
                install(catalog, source = "downloaded")
                UpdateResult.Updated(catalog.version)
            } catch (error: java.net.UnknownHostException) {
                UpdateResult.Failed("You're offline")
            } catch (error: java.net.ConnectException) {
                UpdateResult.Failed("You're offline")
            } catch (error: IOException) {
                UpdateResult.Failed(error.message ?: error.javaClass.simpleName)
            } catch (error: kotlinx.serialization.SerializationException) {
                UpdateResult.Failed("The downloaded catalog could not be read")
            }
        }
    }

    fun pullProfiles(): PullProfiles {
        profiles?.let { return it }
        val loaded = runCatching {
            CatalogJson.json.decodeFromString(PullProfiles.serializer(), profilesFile.readText())
        }.getOrDefault(PullProfiles())
        profiles = loaded
        return loaded
    }

    private suspend fun install(catalog: Catalog, source: String) {
        db.catalog().replaceCatalog(CatalogMapper.sets(catalog), CatalogMapper.printings(catalog))
        profilesFile.writeText(CatalogJson.json.encodeToString(PullProfiles.serializer(), catalog.pullProfiles))
        profiles = catalog.pullProfiles
        prefs.edit()
            .putString(KEY_VERSION, catalog.version)
            .putString(KEY_GENERATED, catalog.generatedAt)
            .putString(KEY_SOURCE, source)
            .apply()
        _info.value = readInfo()
    }

    private fun markChecked() {
        prefs.edit().putLong(KEY_CHECKED, System.currentTimeMillis()).apply()
        _info.value = readInfo()
    }

    private fun <T> fetch(url: String, read: (okhttp3.ResponseBody) -> T): T {
        val request = Request.Builder().url(url).build()
        http.newCall(request).execute().use { response ->
            if (response.code == 404) throw IOException("No catalog has been published yet")
            if (!response.isSuccessful) throw IOException("Server error ${response.code}")
            return read(response.body ?: throw IOException("Empty response"))
        }
    }

    private fun readInfo(): CatalogInfo? {
        val version = prefs.getString(KEY_VERSION, null) ?: return null
        return CatalogInfo(
            version = version,
            generatedAt = prefs.getString(KEY_GENERATED, "") ?: "",
            source = prefs.getString(KEY_SOURCE, "") ?: "",
            checkedAt = prefs.getLong(KEY_CHECKED, 0L),
        )
    }

    private companion object {
        const val BUNDLED_ASSET = "catalog.json.gzip"
        const val KEY_VERSION = "catalog_version"
        const val KEY_GENERATED = "catalog_generated_at"
        const val KEY_SOURCE = "catalog_source"
        const val KEY_CHECKED = "catalog_checked_at"
        const val KEY_BUNDLE_CHECKED = "bundle_checked_for_app_version"
    }
}
