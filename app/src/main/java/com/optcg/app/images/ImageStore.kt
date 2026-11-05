package com.optcg.app.images

import android.content.Context
import com.optcg.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

/**
 * Card images kept for offline use (owned and wishlisted cards, and sets the user downloads).
 * Everything else is loaded on demand and kept only in Coil's size-limited cache.
 */
class ImageStore(context: Context, private val http: OkHttpClient) {
    private val dir = File(context.filesDir, "images").apply { mkdirs() }
    private val _saved = MutableStateFlow(scan())

    /** Ids of printings with an offline image. */
    val saved: StateFlow<Set<String>> = _saved.asStateFlow()

    private val base get() = BuildConfig.IMAGE_BASE_URL

    /** What to give Coil for a grid tile: the offline file when there is one. */
    fun thumbModel(card: HasImage): Any? = offline(card) ?: ImageUrls.thumb(base, card)

    fun detailModel(card: HasImage): Any? = offline(card) ?: ImageUrls.detail(base, card)

    fun hasImage(card: HasImage): Boolean = ImageUrls.detail(base, card) != null

    private fun offline(card: HasImage): File? = if (card.id in _saved.value) file(card.id) else null

    /** Downloads the detail image for offline use. Returns false if the card has no image. */
    suspend fun save(card: HasImage): Boolean = withContext(Dispatchers.IO) {
        if (card.id in _saved.value) return@withContext true
        val url = ImageUrls.detail(base, card) ?: return@withContext false
        val target = file(card.id)
        val partial = File(dir, target.name + ".part")
        http.newCall(Request.Builder().url(url).build()).execute().use { response ->
            if (!response.isSuccessful) throw IOException("HTTP ${response.code} for ${card.id}")
            val body = response.body ?: throw IOException("Empty image for ${card.id}")
            partial.outputStream().use { out -> body.byteStream().copyTo(out) }
        }
        if (!partial.renameTo(target)) throw IOException("Could not save image for ${card.id}")
        _saved.update { it + card.id }
        true
    }

    fun bytesUsed(): Long = dir.listFiles()?.sumOf { it.length() } ?: 0L

    suspend fun clear() = withContext(Dispatchers.IO) {
        dir.listFiles()?.forEach { it.delete() }
        _saved.value = emptySet()
    }

    // Printing ids are ASCII codes like OP01-001_p2 or DON-0012, safe as file names.
    private fun file(id: String) = File(dir, "$id.img")

    private fun scan(): Set<String> =
        dir.listFiles { f -> f.name.endsWith(".img") }?.mapTo(HashSet()) { it.name.removeSuffix(".img") } ?: emptySet()
}
