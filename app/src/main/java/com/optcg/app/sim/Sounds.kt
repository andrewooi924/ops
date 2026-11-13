package com.optcg.app.sim

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.optcg.app.R

/** Short simulator sound effects, loaded once. They play at media volume. */
class Sounds(context: Context) {
    private val pool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()
    private val slice = pool.load(context, R.raw.pack_slice, 1)
    private val pop = pool.load(context, R.raw.pack_pop, 1)

    /** The pack being cut open along its top. */
    fun slice() { pool.play(slice, 0.9f, 0.9f, 1, 0, 1f) }

    /** The torn strip snapping off. */
    fun pop() { pool.play(pop, 1f, 1f, 1, 0, 1f) }
}
