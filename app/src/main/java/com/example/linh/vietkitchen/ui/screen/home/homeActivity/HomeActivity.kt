package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.linh.vietkitchen.BuildConfig
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.baseMVVM.*
import com.example.linh.vietkitchen.di.injector
import com.example.linh.vietkitchen.di.viewModel
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.util.ScreenUtil
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home_app_bar.*
import timber.log.Timber


class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
        OnItemClickListener, BaseFragment.FragmentScreenChangeCallbacks {
    private lateinit var appBarConfiguration : AppBarConfiguration

    private val viewModel: HomeActivityViewModel by viewModel(this) {injector.homeActivityViewModel}
    private lateinit var drawerNavAdapter: DrawerNavRcAdapter
    internal var onDrawerNavItemChangedListener: OnDrawerNavItemChangedListener? = null

//    private var halfDoubleTabOnHomeBottomNav = false


    //region lifecycle =============================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val host = supportFragmentManager.findFragmentById(R.id.navHostFragment)
                as NavHostFragment? ?: return
        val navController = host.navController
        appBarConfiguration = AppBarConfiguration( setOf(R.id.home_dest), drawerLayout)
        setupAppbar()
        setupActionBar(navController, appBarConfiguration)
        setupBottomNavMenu(navController)
        setupDrawerNav(navController)
        setupAdminFab()
        observeViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Have the NavigationUI look for an action or destination matching the menu
        // item id and navigate there if found.
        // Otherwise, bubble up to the parent.
        return item.onNavDestinationSelected(findNavController(R.id.navHostFragment))
                || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Allows NavigationUI to support proper up navigation or the drawer layout
        // drawer menu, depending on the situation
        return findNavController(R.id.navHostFragment).navigateUp(appBarConfiguration)
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
    override fun getViewModel(): BaseViewModel = viewModel
    private fun onRequestCategoriesSuccess(items: List<DrawerNavGroupItem>) {
        drawerNavAdapter.updateItemThenNotify(items)
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

    //drawer navigation callback
    override fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: DrawerNavChildItem?) {
        val category = data?.itemTitle ?: ""
        onDrawerNavItemChangedListener?.onDrawerNavChanged(category)
        drawerLayout.closeDrawer(GravityCompat.START)
        showToolbar()
    }
    //endregion callbacks

    override fun onRequireFullScreen() {
        appBarLayout.visibility = View.GONE
        bottomNav.visibility = View.GONE
        fabAdmin.visibility = View.GONE
//        val params = (navHostFragment.view as ViewGroup).layoutParams as CoordinatorLayout.LayoutParams
//        params.behavior = null
//        (navHostFragment.view as ViewGroup).requestLayout()
    }

    override fun onRequireNormalScreen() {
        appBarLayout.visibility = View.VISIBLE
        bottomNav.visibility = View.VISIBLE
        fabAdmin.visibility = View.VISIBLE
//        val params = (navHostFragment.view as ViewGroup).layoutParams as CoordinatorLayout.LayoutParams
//        params.behavior = ScrollingViewBehavior()
    }

    override fun onRequireJustToolbarScreen() {
        appBarLayout.visibility = View.VISIBLE
        bottomNav.visibility = View.GONE
        fabAdmin.visibility = View.GONE
    }

    //region inner methods =========================================================================
    override fun observeViewModel(){
        VietKitchenApp.category.observe(this, Observer {
            onRequestCategoriesSuccess(it)
        })
    }

    private fun setupBottomNavMenu(navController: NavController) {
        bottomNav?.setupWithNavController(navController)
    }

    private fun setupActionBar(navController: NavController,
                               appBarConfig : AppBarConfiguration) {
        // This allows NavigationUI to decide what label to show in the action bar
        // By using appBarConfig, it will also determine whether to
        // show the up arrow or drawer menu icon
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfig)
    }

    private fun setupAppbar(){
        val appBarElevationMax = ScreenUtil.dp2px(1)
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appbar, verticalOffset ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Timber.d("addOnOffsetChangedListener $verticalOffset")
                val currentPercent = Math.abs(verticalOffset) * 100 / Math.abs(appbar.totalScrollRange + 1).toFloat()
                val appBarElevationCurrent = (currentPercent * appBarElevationMax) / 100
                val stateListAnimator = StateListAnimator()
                stateListAnimator.addState(IntArray(0), ObjectAnimator.ofFloat(appbar, "elevation", appBarElevationCurrent))
                appbar.stateListAnimator = stateListAnimator
            } else {
            }

        })
    }

    private fun setupDrawerNav(navController: NavController) {
        drawerNavAdapter = DrawerNavRcAdapter(drawerNavExpandableRc, childItemClickListener= this)
        drawerNavExpandableRc.layoutManager = LinearLayoutManager(this)
        drawerNavExpandableRc.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        drawerNavExpandableRc.adapter = drawerNavAdapter
        drawerNavView.setupWithNavController(navController)
    }

    private fun setupAdminFab(){
        if(BuildConfig.IS_ADMIN) fabAdmin.show() else fabAdmin.hide()
        fabAdmin.setOnClickListener {
//            presenter.putARecipe(
            val intent = Intent(this, Class.forName("com.example.linh.vietkitchen.admin.ui.screen.admin.AdminActivity"))
            val bundle = Bundle()
            intent.putExtras(bundle)
            startActivityWithAnimation(intent)
        }
    }

    internal fun showToolbar(){
        appBarLayout.postDelayed({
            appBarLayout.setExpanded(true, true)
        }, 500)
    }

    internal fun getToolbar() = toolbar

    internal fun getAppbar() = appBarLayout

    private fun changeToolbarScrollable(scrollable: Boolean){
        // Show toolbar when we are in maps mode
        val params = toolbar.layoutParams as AppBarLayout.LayoutParams
        val appBarLayoutParams = appBarLayout.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
        if (scrollable) {
            params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
            appBarLayoutParams.behavior = AppBarLayout.Behavior()
            appBarLayout.layoutParams = appBarLayoutParams
        } else {
            params.scrollFlags = 0
            appBarLayoutParams.behavior = null
            appBarLayout.layoutParams = appBarLayoutParams
        }
    }

    private fun setBottomNavVisibility(isVisible: Boolean){
        bottomNav.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    private fun setFabVisibility(isVisible: Boolean){
        if (isVisible) fabAdmin.show() else fabAdmin.hide()
    }

    //endregion inner methods
}

//region inner classes ========================================================================
interface OnDrawerNavItemChangedListener{
    fun onDrawerNavChanged(category: String)
}
//endregion inner classes
