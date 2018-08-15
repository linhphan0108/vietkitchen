package com.example.linh.vietkitchen.ui.home.homeActivity

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.adapter.HomePagerAdapter
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.mvpBase.BaseActivity
import com.example.linh.vietkitchen.ui.mvpBase.ToolbarActions
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home_app_bar.*
import kotlinx.android.synthetic.main.activity_home_content.*


class HomeActivity : BaseActivity<HomeActivityContractView, HomeActivityContractPresenter>(),
        NavigationView.OnNavigationItemSelectedListener, HomeActivityContractView,
        OnItemClickListener, ToolbarActions {

    companion object {
        fun createIntent(context: Context): Intent{
            return Intent(context, HomeActivity::class.java)
        }
    }

    private lateinit var homePagerAdapter: HomePagerAdapter
    private lateinit var drawerNavAdapter: DrawerNavRcAdapter
    var onDrawerNavItemChangedListener: OnDrawerNavItemChangedListener? = null
    internal var likeOrUnLikePublisher: Subject<Recipe> = PublishSubject.create()

    //region lifecycle =============================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupAppBarAndDrawerNav()
        setupBottomTabBar()
        setupViewPager()
        presenter.requestCategory()
        fab.setOnClickListener {
            presenter.putARecipe()
        }
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
    override fun getActivityLayoutRes() = R.layout.activity_home
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

    override fun changeToolbarTitle(title: String) {
        toolbar.title = title
    }

    //drawer navigation callback
    override fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: DrawerNavChildItem?) {
        val category = data?.itemTitle
        onDrawerNavItemChangedListener?.onDrawerNavChanged(category)
        drawerLayout.closeDrawer(GravityCompat.START)
    }
    //endregion callbacks

    //region inner methods =========================================================================
    private fun setupViewPager(){
        homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        viewPager.adapter = homePagerAdapter
        viewPager.offscreenPageLimit = 2
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                bottomNav.selectedItemId = when(position){
                    0 -> R.id.action_home
                    1 -> R.id.action_favorite
                    2 -> R.id.action_profile
                    else -> R.id.action_home
                }
            }

        })
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
//                R.id.action_calendar -> {
//                    viewPager.currentItem = 2
//                }
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
        drawerNavAdapter = DrawerNavRcAdapter(drawerNavExpandableRc, childItemClickListener= this)
        drawerNavExpandableRc.layoutManager = LinearLayoutManager(this)
        drawerNavExpandableRc.itemAnimator = DefaultItemAnimator()
        drawerNavExpandableRc.adapter = drawerNavAdapter
    }
    //endregion inner methods
}

//region inner classes ========================================================================
interface OnDrawerNavItemChangedListener{
    fun onDrawerNavChanged(category: String?)
}
//endregion inner classes
