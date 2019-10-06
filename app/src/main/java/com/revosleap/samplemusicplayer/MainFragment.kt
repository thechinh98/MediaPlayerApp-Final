package com.revosleap.samplemusicplayer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.revosleap.samplemusicplayer.Model.Song
import com.revosleap.samplemusicplayer.utils.RecyclerAdapter
import com.revosleap.samplemusicplayer.utils.SongProvider
import kotlinx.android.synthetic.main.fragment_fragment_main.*
import kotlinx.android.synthetic.main.fragment_play_music.*
import java.util.ArrayList


class MainFragment : Fragment() {
    private var recyclerAdapter: RecyclerAdapter? = null
    var mSelectedArtistSongs: List<Song>? = null
    private var callBack = object : ItemClickHandler {
        override fun setOnItemClick(song: Song) {
            if (activity is MainActivity) {
                var activity = activity as MainActivity
                activity.currentSong = song
                activity.musicPageChange()
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    interface ItemClickHandler {
        fun setOnItemClick(song: Song)
    }
    private fun setViews() {
        //set adapter
        recyclerAdapter = RecyclerAdapter(callBack)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        //get songs
        mSelectedArtistSongs = SongProvider.getAllDeviceSongs(context!!)
        recyclerAdapter!!.addSongs(mSelectedArtistSongs as ArrayList<*>)


    }
}
