package com.studio.gladetowns.core.audio

import android.content.Context
import android.media.MediaPlayer

/**
 * Looping, low-volume background music. The track is resolved by NAME at
 * runtime (`res/raw/chill_music.*`), so the app compiles and runs even before a
 * track is bundled — if the file is missing this is simply a no-op.
 *
 * To enable: drop an audio file at
 *   app/src/main/res/raw/chill_music.mp3   (or .ogg / .m4a)
 * (raw resource names must be lowercase letters, digits, and underscores.)
 *
 * Driven from MainActivity's lifecycle: [start] on resume, [pause] on stop.
 */
object MusicManager {

    private const val TRACK_NAME = "chill_music"
    private const val VOLUME = 0.35f

    private var player: MediaPlayer? = null
    private var muted = false

    fun start(context: Context) {
        if (muted) return
        player?.let { if (!it.isPlaying) it.start(); return }

        val appContext = context.applicationContext
        val resId = appContext.resources.getIdentifier(TRACK_NAME, "raw", appContext.packageName)
        if (resId == 0) return // no track bundled yet

        player = MediaPlayer.create(appContext, resId)?.apply {
            isLooping = true
            setVolume(VOLUME, VOLUME)
            start()
        }
    }

    fun pause() {
        player?.takeIf { it.isPlaying }?.pause()
    }

    /** Toggle mute; returns the new muted state. */
    fun toggleMute(context: Context): Boolean {
        muted = !muted
        if (muted) pause() else start(context)
        return muted
    }

    fun isMuted(): Boolean = muted

    fun release() {
        player?.release()
        player = null
    }
}
