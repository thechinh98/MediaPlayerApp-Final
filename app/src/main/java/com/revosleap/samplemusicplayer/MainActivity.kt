package com.revosleap.samplemusicplayer

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.revosleap.samplemusicplayer.Model.Song
import com.revosleap.samplemusicplayer.PlayMusicFragment.PlaybackListener
import com.revosleap.samplemusicplayer.ViewModel.MusicNotificationManager
import com.revosleap.samplemusicplayer.ViewModel.MusicService
import com.revosleap.samplemusicplayer.ViewModel.PlayerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_play_music.*

class MainActivity : AppCompatActivity() {
    var mMusicService: MusicService? = null
    var mIsBound: Boolean? = null
    var mPlayerAdapter: PlayerAdapter? = null
    var mMusicNotificationManager: MusicNotificationManager? = null
    var playMusicFragment = PlayMusicFragment()
    var mainFragmentMain = MainFragment()
    var currentSong : Song? = Song.EMPTY_SONG
    var mPlaybackListener: PlaybackListener? = null
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {

            mMusicService = (iBinder as MusicService.LocalBinder).instance
            mPlayerAdapter = mMusicService!!.mediaPlayerHolder
            mMusicNotificationManager = mMusicService!!.musicNotificationManager

            if (mPlaybackListener == null) {
                mPlaybackListener = playMusicFragment.PlaybackListener()
                mPlayerAdapter!!.setPlaybackInfoListener(mPlaybackListener!!)
            }
            if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying) {

                playMusicFragment.restorePlayerStatus()
            }
            checkReadStoragePermissions()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mMusicService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        var pagerAdapter = ViewPagerAdapter(supportFragmentManager, mainFragmentMain, playMusicFragment)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view_pager.apply{
            adapter = pagerAdapter
        }
        doBindService()

    }

    override fun onPause() {
        super.onPause()
        doUnbindService()
        if (mPlayerAdapter != null && mPlayerAdapter!!.isMediaPlayer) {
            mPlayerAdapter!!.onPauseActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        doBindService()
        if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying) {
            playMusicFragment.restorePlayerStatus()
        }
        img_my_music.setOnClickListener {
            view_pager.currentItem = 0
        }
        img_now_playing.setOnClickListener {
            view_pager.currentItem = 1
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun checkReadStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }


    private fun doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(Intent(this,
                MusicService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        mIsBound = true

        val startNotStickyIntent = Intent(this, MusicService::class.java)
        startService(startNotStickyIntent)
    }

    private fun doUnbindService() {
        if (mIsBound!!) {
            unbindService(mConnection)
            mIsBound = false
        }
    }

    fun musicPageChange() {
        view_pager.currentItem = 1
        playMusicFragment.onSongChange()
    }



}
