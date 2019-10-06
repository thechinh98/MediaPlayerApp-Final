package com.revosleap.samplemusicplayer

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager, private var mainFragmentMain: MainFragment, private var playMusicFragment: PlayMusicFragment)  : FragmentPagerAdapter(fragmentManager){
    override fun getItem(p0: Int): Fragment {
        when(p0) {
            0 -> {
                return mainFragmentMain
            }
            1 -> {
                return playMusicFragment
            }
        }
        return BlankFragment()
    }

    override fun getCount() = 2
}