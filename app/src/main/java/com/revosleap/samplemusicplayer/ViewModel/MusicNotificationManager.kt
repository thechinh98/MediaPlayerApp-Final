package com.revosleap.samplemusicplayer.ViewModel

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.session.MediaSessionManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat

import com.revosleap.samplemusicplayer.MainActivity
import com.revosleap.samplemusicplayer.R
import com.revosleap.samplemusicplayer.Model.Song
import com.revosleap.samplemusicplayer.utils.Utils

class MusicNotificationManager internal constructor(private val mMusicService: MusicService) {
    private val CHANNEL_ID = "action.CHANNEL_ID"
    private val REQUEST_CODE = 100
    val notificationManager: NotificationManager = mMusicService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    var notificationBuilder: NotificationCompat.Builder? = null
        private set
    private var mediaSession: MediaSessionCompat? = null
    private var mediaSessionManager: MediaSessionManager? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null
    private val context: Context = mMusicService.baseContext

    private fun playerAction(action: String): PendingIntent {

        val pauseIntent = Intent()
        pauseIntent.action = action

        return PendingIntent.getBroadcast(mMusicService, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun createNotification(): Notification {

        val song = mMusicService.mediaPlayerHolder?.mSelectedSong

        notificationBuilder = NotificationCompat.Builder(mMusicService, CHANNEL_ID)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val openPlayerIntent = Intent(mMusicService, MainActivity::class.java)
        openPlayerIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val contentIntent = PendingIntent.getActivity(mMusicService, REQUEST_CODE,
                openPlayerIntent, 0)

        val artist = song!!.artistName
        val songTitle = song.title

        initMediaSession(song)

        notificationBuilder!!
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_play_button)
                .setLargeIcon(Utils.songArt(song.path, mMusicService.baseContext))
                .setColor(context.resources.getColor(R.color.colorAccent))
                .setContentTitle(songTitle)
                .setContentText(artist)
                .setContentIntent(contentIntent)
                .addAction(notificationAction(PREV_ACTION))
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(NEXT_ACTION))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        notificationBuilder!!.setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession!!.sessionToken)
                .setShowActionsInCompactView(0, 1, 2))
        return notificationBuilder!!.build()
    }

    private fun notificationAction(action: String): NotificationCompat.Action {

        var icon: Int = when (action) {
            PREV_ACTION -> R.drawable.ic_left_arrow
            PLAY_PAUSE_ACTION ->

                if (mMusicService.mediaPlayerHolder?.state != PlaybackInfoListener.State.PAUSED)
                    R.drawable.ic_pause_button
                else
                    R.drawable.ic_play_button
            NEXT_ACTION -> R.drawable.ic_right_arrow
            else -> R.drawable.ic_left_arrow
        }

        return NotificationCompat.Action.Builder(icon, action, playerAction(action)).build()
    }

    @RequiresApi(26)
    private fun createNotificationChannel() {

        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(CHANNEL_ID,
                    mMusicService.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW)

            notificationChannel.description = mMusicService.getString(R.string.app_name)

            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            notificationChannel.setShowBadge(false)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun initMediaSession(song: Song) {
        mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSession = MediaSessionCompat(context, "AudioPlayer")
        transportControls = mediaSession!!.controller.transportControls
        mediaSession!!.isActive = true
        mediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        updateMetaData(song)
    }

    private fun updateMetaData(song: Song) {
        mediaSession!!.setMetadata(MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, Utils.songArt(song.path, context))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artistName)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.albumName)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                .build())
    }

    companion object {
        const val NOTIFICATION_ID = 101
        internal const val PLAY_PAUSE_ACTION = "action.PLAYPAUSE"
        internal const val NEXT_ACTION = "action.NEXT"
        internal const val PREV_ACTION = "action.PREV"
    }

}
