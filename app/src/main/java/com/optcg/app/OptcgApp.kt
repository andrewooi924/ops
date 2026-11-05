package com.optcg.app

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import com.optcg.app.catalog.CatalogRepository
import com.optcg.app.data.AppDatabase
import com.optcg.app.data.CollectionRepository
import com.optcg.app.images.ImageStore
import com.optcg.app.work.Jobs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/** Hand-wired dependencies; small enough not to need a DI framework. */
class AppContainer(context: Context) {
    val scope = CoroutineScope(SupervisorJob())
    val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            chain.proceed(chain.request().newBuilder().header("User-Agent", "PieceLab/${BuildConfig.VERSION_NAME} (Android)").build())
        }
        .build()
    val prefs = context.getSharedPreferences("piecelab", Context.MODE_PRIVATE)
    val db = AppDatabase.create(context)
    val catalog = CatalogRepository(context, db, http, prefs)
    val images = ImageStore(context, http) { catalog.imageBase }
    val jobs = Jobs(context)
    val collection = CollectionRepository(db, prefs, jobs)
}

class OptcgApp : Application(), ImageLoaderFactory {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        removeOldAppData()
        container.jobs.scheduleCatalogSync()
    }

    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .okHttpClient(container.http)
        .diskCache { DiskCache.Builder().directory(cacheDir.resolve("coil")).maxSizeBytes(250L * 1024 * 1024).build() }
        .crossfade(true)
        .build()

    /** The rebuilt app starts fresh; the 1.x app's database and preferences are removed once. */
    private fun removeOldAppData() {
        val prefs = container.prefs
        if (prefs.getBoolean("old_data_removed", false)) return
        deleteDatabase("card_database")
        deleteSharedPreferences("COLLECTION_PREFS")
        deleteSharedPreferences("USER_PREFS")
        prefs.edit().putBoolean("old_data_removed", true).apply()
    }
}

val Context.container: AppContainer get() = (applicationContext as OptcgApp).container
