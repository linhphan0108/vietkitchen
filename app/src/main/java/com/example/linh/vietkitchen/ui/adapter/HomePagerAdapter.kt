package com.example.linh.vietkitchen.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.linh.vietkitchen.ui.screen.home.homeFragment.HomeFragment
import com.example.linh.vietkitchen.ui.screen.home.favorite.FavoriteFragment
import com.example.linh.vietkitchen.ui.screen.home.profile.ProfileFragment
import android.util.SparseArray



class HomePagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
    companion object {
        const val NUMBER_TABS = 3
        const val HOME = 0
        const val FAVORITE = 1
        const val PROFILE = 2
    }

    private val sparseArrayFragments = SparseArray<Fragment>()
    override fun getItem(position: Int): Fragment {
        return when(position){
            HOME -> {
                return if (sparseArrayFragments.get(position) != null){
                    sparseArrayFragments.get(position)
                }else{
                    val newInstance = HomeFragment.newInstance()
                    sparseArrayFragments.put(position, newInstance)
                    newInstance
                }
            }
            FAVORITE -> FavoriteFragment.newInstance()
//            2 -> CalendarFragment()
            PROFILE -> ProfileFragment.newInstance()
            else -> HomeFragment.newInstance()
        }
    }

    override fun getCount() = NUMBER_TABS
}