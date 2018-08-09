package com.example.linh.vietkitchen.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.linh.vietkitchen.ui.home.FavoriteFragment
import com.example.linh.vietkitchen.ui.home.homeFragment.HomeFragment
import com.example.linh.vietkitchen.ui.screen.home.profile.ProfileFragment

class HomePagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
    companion object {
        const val NUMBER_TABS = 3
    }
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> HomeFragment.newInstance()
            1 -> FavoriteFragment.newInstance()
//            2 -> CalendarFragment()
            2 -> ProfileFragment.newInstance()
            else -> HomeFragment.newInstance()
        }
    }

    override fun getCount() = NUMBER_TABS

}