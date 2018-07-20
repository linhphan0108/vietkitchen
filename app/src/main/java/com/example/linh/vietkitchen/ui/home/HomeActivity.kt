package com.example.linh.vietkitchen.ui.home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.data.local.*
import com.example.linh.vietkitchen.ui.adapter.HomePagerAdapter
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber


class HomeActivity : AppCompatActivity() {

    lateinit var homePagerAdapter: HomePagerAdapter

    //region lifecycle =============================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupBottomTabBar()
        setupViewPager()
//        createAnPutDumpDataToFirebaseDb()
//        fetchData()
//        Timber.d("message has logged by timber")
    }
    //endregion lifecycle


    //region inner methods =========================================================================
    private fun setupViewPager(){
        homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        viewPager.adapter = homePagerAdapter
        viewPager.offscreenPageLimit = 2
    }

    private fun setupBottomTabBar(){
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_home ->{
                    viewPager.currentItem = 0
                }
                R.id.action_favorite -> {
                    viewPager.currentItem = 1
                }
                R.id.action_calendar -> {
                    viewPager.currentItem = 2
                }
                R.id.action_profile -> {
                    viewPager.currentItem = 3
                }
                else -> {
                }
            }
            true
        }
    }
    //endregion inner methods

}
