package com.revosleap.samplemusicplayer.utils

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.revosleap.samplemusicplayer.MainFragment

import com.revosleap.samplemusicplayer.R
import com.revosleap.samplemusicplayer.Model.Song

import java.util.ArrayList

class RecyclerAdapter(private val songClicked: MainFragment.ItemClickHandler) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var songsList: ArrayList<*> = ArrayList<Song>()
    var song : Song? = Song.EMPTY_SONG

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.track_item, viewGroup, false))
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val song = songsList[i] as Song
        viewHolder.bind(song)
        viewHolder.itemView.setOnClickListener { songClicked.setOnItemClick(song) }
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    fun addSongs(songs: ArrayList<*>) {
        songsList.clear()
        songsList = songs
        notifyDataSetChanged()
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.textViewSongTitle)
        private val artist: TextView = itemView.findViewById(R.id.textViewArtistName)

        fun bind(song: Song) {
            title.text = song.title
            artist.text = song.artistName
        }
    }
}
