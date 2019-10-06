package com.revosleap.samplemusicplayer.ViewModel

import android.media.MediaPlayer

import com.revosleap.samplemusicplayer.Model.Song

abstract class PlayerAdapter {

    var isMediaPlayer: Boolean = false

    var isPlaying: Boolean = true

    var isReset: Boolean = true

    @get:PlaybackInfoListener.State
    var state: Int = 0

    var playerPosition: Int = 0

    var mediaPlayer: MediaPlayer? = null
    abstract fun initMediaPlayer()

    abstract fun release()

    abstract fun resumeOrPause()

    abstract fun reset()

    abstract fun instantReset()

    abstract fun skip(isNext: Boolean)

    abstract fun seekTo(position: Int)

    abstract fun setPlaybackInfoListener(playbackInfoListener: PlaybackInfoListener)

    abstract fun registerNotificationActionsReceiver(isRegister: Boolean)


    abstract fun setCurrentSong(song: Song, songs: List<Song>)

    abstract fun onPauseActivity()

    abstract fun onResumeActivity()
    internal abstract fun getCurrentSong() : Song
    internal abstract fun isPlaying(): Boolean
    internal abstract fun getState(): Int
    internal abstract fun isReset(): Boolean
    internal abstract fun isMediaPlayer(): Boolean
    internal abstract fun getPlayerPosition(): Int
}
