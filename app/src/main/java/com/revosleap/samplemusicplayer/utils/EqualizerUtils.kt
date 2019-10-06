package com.revosleap.samplemusicplayer.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.widget.Toast

object EqualizerUtils {

    fun notifyNoSessionId(context: Context) {
        Toast.makeText(context, "Play a Song first", Toast.LENGTH_SHORT).show()
    }
}
