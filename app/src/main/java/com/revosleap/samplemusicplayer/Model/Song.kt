package com.revosleap.samplemusicplayer.Model

class Song(val title: String, val trackNumber: Int, private val year: Int, val duration: Int,
           val path: String, val albumName: String, val artistId: Int, val artistName: String) {
    companion object {
        internal val EMPTY_SONG = Song("", -1, -1, -1,
                "", "", -1, "")
    }

}
