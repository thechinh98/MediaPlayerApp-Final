package com.revosleap.samplemusicplayer.ViewModel

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

abstract class PlaybackInfoListener {
    open fun onPositionChanged(position: Int) {}

    open fun onStateChanged(@State state: Int) {}

    open fun onPlaybackCompleted() {}

    @Retention(RetentionPolicy.SOURCE)
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
