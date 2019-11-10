package com.revosleap.samplemusicplayer.playback

import androidx.annotation.IntDef

abstract class PlaybackInfoListener {
    open fun onPositionChanged(position: Int) {}

    open fun onStateChanged(@State state: Int) {}

    open fun onPlaybackCompleted() {}

    @IntDef(State.INVALID, State.PLAYING, State.PAUSED, State.COMPLETED, State.RESUMED)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class State {
        companion object {

            const val INVALID = -1
            const val PLAYING = 0
            const val PAUSED = 1
            const val COMPLETED = 2
            const val RESUMED = 3
        }
    }
}
