package com.example.linh.vietkitchen.ui.home.homeActivity

import android.annotation.TargetApi
import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.adapter.HomePagerAdapter
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.mvpBase.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home_app_bar.*
import kotlinx.android.synthetic.main.activity_home_content.*
import com.example.linh.vietkitchen.util.TouchEventUtil
import timber.log.Timber


class HomeActivity : BaseActivity<HomeActivityContractView, HomeActivityContractPresenter>(),
        NavigationView.OnNavigationItemSelectedListener, HomeActivityContractView {
    private lateinit var homePagerAdapter: HomePagerAdapter
    private lateinit var drawerNavAdapter: DrawerNavRcAdapter

    //region lifecycle =============================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupAppBarAndDrawerNav()
        setupBottomTabBar()
        setupViewPager()
        presenter.requestCategory()
//        createAnPutDumpDataToFirebaseDb()
//        fetchData()
//        Timber.d("message has logged by timber")
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    //endregion lifecycle

    //region MVP callbacks==========================================================================
    override fun getFragmentLayoutRes() = R.layout.activity_home
    override fun getViewContract(): HomeActivityContractView = this

    override val viewContext: Context?
        get() = this

    override fun initPresenter() = HomeActivityPresenter()

    override fun showProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRequestCategoriesSuccess(items: List<DrawerNavGroupItem>) {
        drawerNavAdapter.updateItemThenNotify(items)
    }

    override fun onRequestCategoriesFailed(message: String) {
    }
    //endregion MVP callbacks


    //region callbacks==============================================================================
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    //region drawer navigation callbacks
//    override fun onChildClick(parent: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, id: Long): Boolean {
//        return false
//    }
//
//    override fun onGroupClick(parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long): Boolean {
//    }
    //endregion drawer navigation callbacks

    //endregion callbacks

    //region inner methods =========================================================================
    private fun setupViewPager(){
        homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        viewPager.adapter = homePagerAdapter
        viewPager.offscreenPageLimit = 3
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

    @TargetApi(Build.VERSION_CODES.M)
    private fun setupAppBarAndDrawerNav(){
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
//        drawerNavView.setNavigationItemSelectedListener(this)
//        val headerView = LayoutInflater.from(this).inflate(R.layout.activity_home_nav_header, null)
//        drawerNavView.addHeaderView(headerView)
//        drawerNavView.getHeaderView(0).visibility = View.GONE
        drawerNavAdapter = DrawerNavRcAdapter(drawerNavExpandableRc)
        drawerNavExpandableRc.layoutManager = LinearLayoutManager(this)
        drawerNavExpandableRc.itemAnimator = DefaultItemAnimator()
        drawerNavExpandableRc.adapter = drawerNavAdapter
    }

    private fun showSnakeBar(){
        Snackbar.make(drawerLayout, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }
    //endregion inner methods
}
