package com.ardrawing.sketch.anime.drawing.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ardrawing.sketch.anime.drawing.ui.favorite.FavoriteFragment
import com.ardrawing.sketch.anime.drawing.ui.home.HomeFragment
import com.ardrawing.sketch.anime.drawing.ui.setting.SettingFragment

class MainViewPageAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_SET_USER_VISIBLE_HINT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                HomeFragment()
            }

            1 -> {
                FavoriteFragment()
            }

            2 -> {
                SettingFragment()
            }

            else -> HomeFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}