package com.example.linh.vietkitchen.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.linh.vietkitchen.ui.home.CalendarFragment
import com.example.linh.vietkitchen.ui.home.FavoriteFragment
import com.example.linh.vietkitchen.ui.home.HomeFragment
import com.example.linh.vietkitchen.ui.home.ProfileFragment

class HomePagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
    companion object {
        const val NUMBER_TABS = 4
    }
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> HomeFragment()
            1 -> FavoriteFragment()
            2 -> CalendarFragment()
            3 -> ProfileFragment()
            else -> HomeFragment()
        }
    }

    override fun getCount() = NUMBER_TABS

}