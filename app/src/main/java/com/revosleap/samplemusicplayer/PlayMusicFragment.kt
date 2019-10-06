package com.revosleap.samplemusicplayer

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.revosleap.samplemusicplayer.Model.Song
import com.revosleap.samplemusicplayer.ViewModel.MusicNotificationManager
import com.revosleap.samplemusicplayer.ViewModel.PlaybackInfoListener
import com.revosleap.samplemusicplayer.ViewModel.PlayerAdapter
import com.revosleap.samplemusicplayer.utils.EqualizerUtils
import com.revosleap.samplemusicplayer.utils.SongProvider
import kotlinx.android.synthetic.main.fragment_play_music.*

class PlayMusicFragment : Fragment() {

    private var mUserIsSeeking = false
    private lateinit var mPlayerAdapter: PlayerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeSeekBar()

        btn_play_pause.setOnClickListener {
            resumeOrPause()
            if (activity is MainActivity) {
                val activity = activity as MainActivity
                if (activity.mPlayerAdapter!!.isPlaying) {
                    btn_play_pause.setImageResource(R.drawable.ic_pause_button)
                } else {
                    btn_play_pause.setImageResource(R.drawable.ic_play_button)
                }
            }

        }
        btn_next.setOnClickListener {
            skipNext()
        }
        btn_prev.setOnClickListener {
            skipPrev()
        }
    }

    private fun updatePlayingInfo(restore: Boolean, startPlay: Boolean) {
        if (activity is MainActivity) {
            val activity = activity as MainActivity
            mPlayerAdapter = activity.mPlayerAdapter!!
            var selectedSong = mPlayerAdapter.getCurrentSong()
            txt_title.text = selectedSong.title
            txt_author.text = selectedSong.artistName
            val duration = selectedSong?.duration
            seek_bar!!.max = duration!!

            if (startPlay) {
                mPlayerAdapter.mediaPlayer?.start()

                Handler().postDelayed({
                    activity.mMusicService!!.startForeground(MusicNotificationManager.NOTIFICATION_ID,
                            activity.mMusicNotificationManager?.createNotification())
                }, 250)
            }


            if (restore) {
                seek_bar!!.progress = activity.mPlayerAdapter!!.playerPosition
                updatePlayingStatus()


                Handler().postDelayed({
                    //stop foreground if coming from pause state
                    if (activity.mMusicService!!.isRestoredFromPause) {
                        activity.mMusicService!!.stopForeground(false)
                        activity.mMusicService!!.musicNotificationManager!!.notificationManager
                                .notify(MusicNotificationManager.NOTIFICATION_ID,
                                        activity.mMusicService!!.musicNotificationManager!!.notificationBuilder!!.build())
                        activity.mMusicService!!.isRestoredFromPause = false
                    }
                }, 250)
            }
        }
    }

    private fun updatePlayingStatus() {
        btn_play_pause.setImageResource(R.drawable.ic_pause_button)
    }

    fun restorePlayerStatus() {
        if (activity is Activity) {
            var activity = activity as MainActivity
            mPlayerAdapter = activity.mPlayerAdapter!!
            seek_bar!!.isEnabled = mPlayerAdapter.isMediaPlayer

        }

        //if we are playing and the activity was restarted
        //update the controls panel
        if (mPlayerAdapter.isMediaPlayer) {
            mPlayerAdapter.onResumeActivity()
            updatePlayingInfo(restore = true, startPlay = false)
        }
    }

    private fun onSongSelected(song: Song, songs: List<Song>) {
        if (!seek_bar.isEnabled) {
            seek_bar.isEnabled = true
        }
        if (activity is Activity) {
            val activity = activity as MainActivity
            mPlayerAdapter = activity.mPlayerAdapter!!
            mPlayerAdapter.setCurrentSong(song, songs)
            mPlayerAdapter.initMediaPlayer()
            activity.mPlayerAdapter = mPlayerAdapter
        }
    }

    private fun initializeSeekBar() {
        seek_bar!!.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    var userSelectedPosition = 0

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        mUserIsSeeking = true
                    }

                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                        if (fromUser) {
                            userSelectedPosition = progress

                        }

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {

                        if (mUserIsSeeking) {

                        }
                        mUserIsSeeking = false
                        mPlayerAdapter.seekTo(userSelectedPosition)
                    }
                })
    }

    inner class PlaybackListener : PlaybackInfoListener() {

        override fun onPositionChanged(position: Int) {
            if (!mUserIsSeeking) {
                seek_bar!!.progress = position
            }
        }

        override fun onStateChanged(@State state: Int) {

            updatePlayingStatus()
            updatePlayingInfo(restore = false, startPlay = true)
        }

        override fun onPlaybackCompleted() {
            //After playback is complete
        }
    }


    private fun skipPrev() {
        if (checkIsPlayer()) {
            mPlayerAdapter.instantReset()
        }
    }

    private fun resumeOrPause() {
        if (checkIsPlayer()) {
            mPlayerAdapter.resumeOrPause()
        }
    }

    private fun skipNext() {
        if (checkIsPlayer()) {
            mPlayerAdapter.skip(true)
        }
    }

    private fun checkIsPlayer(): Boolean {
        val isPlayer = mPlayerAdapter.isMediaPlayer
        if (!isPlayer) {
            EqualizerUtils.notifyNoSessionId(context!!)
        }
        return isPlayer
    }

    fun onSongChange() {
        if (activity is MainActivity) {
            val activity = activity as MainActivity
            onSongSelected(activity.currentSong!!, SongProvider.getAllDeviceSongs(context!!))
        }

    }
}
