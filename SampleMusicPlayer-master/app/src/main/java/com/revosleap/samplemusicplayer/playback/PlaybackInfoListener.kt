package com.revosleap.samplemusicplayer.playback

import android.support.annotation.IntDef

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

abstract class PlaybackInfoListener {
    open fun onPositionChanged(position: Int) {}

    open fun onStateChanged(@State state: Int) {}

    open fun onPlaybackCompleted() {}

    @IntDef(State.INVALID, State.PLAYING, State.PAUSED, State.COMPLETED, State.RESUMED)
    @Retention(RetentionPolicy.SOURCE)
    annotation class State {
        companion object {

            val INVALID = -1
            val PLAYING = 0
            val PAUSED = 1
            val COMPLETED = 2
            val RESUMED = 3
        }
    }
}
