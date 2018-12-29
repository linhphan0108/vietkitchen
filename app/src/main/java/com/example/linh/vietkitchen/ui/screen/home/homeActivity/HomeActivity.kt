package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.support.v7.widget.SearchView
import android.view.inputmethod.EditorInfo
import com.example.linh.vietkitchen.BuildConfig
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.ui.adapter.HomePagerAdapter
import com.example.linh.vietkitchen.ui.adapter.SearchSuggestionAdapter
import com.example.linh.vietkitchen.ui.adapter.SearchSuggestionViewHolder
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.SearchItem
import com.example.linh.vietkitchen.ui.mvpBase.BaseActivity
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.ui.mvpBase.ToolbarActions
import com.example.linh.vietkitchen.ui.screen.searchScreen.SearchScreenActivity
import com.example.linh.vietkitchen.util.Constants
import com.example.linh.vietkitchen.util.ScreenUtil
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home_app_bar.*
import kotlinx.android.synthetic.main.activity_home_content.*
import kotlinx.android.synthetic.main.layout_search_view_suggestion.*
import timber.log.Timber


class HomeActivity : BaseActivity<HomeActivityContractView>(),
        NavigationView.OnNavigationItemSelectedListener, HomeActivityContractView,
        OnItemClickListener, ToolbarActions, SearchView.OnQueryTextListener, SearchSuggestionViewHolder.OnItemListeners {

    companion object {
        fun createIntent(context: Context): Intent{
            return Intent(context, HomeActivity::class.java)
        }
    }
    private val presenter: HomeActivityPresenter by lazy { HomeActivityPresenter() }
    private lateinit var homePagerAdapter: HomePagerAdapter
    private lateinit var drawerNavAdapter: DrawerNavRcAdapter
    private val searchSuggestionAdapter: SearchSuggestionAdapter by lazy { SearchSuggestionAdapter(mutableListOf(), this) }
    internal var onDrawerNavItemChangedListener: OnDrawerNavItemChangedListener? = null
    private lateinit var navItems: List<DrawerNavGroupItem>

    private var isSearchSuggestionAdapterAttached = false

    //region lifecycle =============================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAppbar()
        setupDrawerNav()
        setupBottomTabBar()
        setupViewPager()
        setupAdminFab()
        presenter.requestCategory()
        presenter.requestTags()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_options, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.action_search)
        (searchItem.actionView as SearchView).apply {
//            setSearchableInfo(searchManager.getSearchableInfo(ComponentName(this@HomeActivity, SearchScreenActivity::class.java)))
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            queryHint = getString(R.string.hint_search)
            setIconifiedByDefault(true) // Do not iconify the widget; expand it by default
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setOnQueryTextListener(this@HomeActivity)
            setOnSearchClickListener {
                Timber.d("SearchView onSearchClickListener")
            }
            searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    Timber.d("SearchView onMenuItemActionExpand")
                    setMenuItemsVisibility(menu, searchItem, false)
                    setSearchSuggestionVisibility(true)
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    Timber.d("SearchView onMenuItemActionCollapse")
                    setMenuItemsVisibility(menu, searchItem, true)
                    setSearchSuggestionVisibility(false)
                    return true
                }

            })
            Timber.d("SearchView created")
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return false
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

    override fun getPresenter(): BasePresenterContract<HomeActivityContractView> {
        return presenter
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun onNoInternetException() {

    }

    override fun onRequestCategoriesSuccess(items: List<DrawerNavGroupItem>) {
        drawerNavAdapter.updateItemThenNotify(items)
        navItems = items
    }

    override fun onRequestCategoriesFailed(message: String) {
    }

    override fun onGetTagsSuccess(tags: List<SearchItem>) {
    }

    override fun onGetTagsFailed(message: String?) {
        message?.let { toast(it)}
    }

    override fun onFilterListTag(filteredTags: List<SearchItem>) {
        searchSuggestionAdapter.items = filteredTags.toMutableList()
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
        toolbar.title = if(title.isBlank()){
            "tất cả"
        }else{
            title
        }
    }

    //drawer navigation callback
    override fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: DrawerNavChildItem?) {
        val category = data?.itemTitle ?: ""
        changeToolbarTitle(category)
        onDrawerNavItemChangedListener?.onDrawerNavChanged(category)
        drawerLayout.closeDrawer(GravityCompat.START)
        showToolbar()
    }

    //SearchView's callback
    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        presenter.filterListTags(p0)
        return true
    }

    //on search item suggestion clicked
    override fun onItemClick(item: SearchItem) {
        val intent = Intent(this, SearchScreenActivity::class.java)
        intent.action = Intent.ACTION_SEARCH
        intent.putExtra(SearchManager.USER_QUERY, item)
        startActivity(intent)
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
                showToolbar()
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
            showToolbar()
            true
        }
    }

    private fun setupAppbar(){
        setSupportActionBar(toolbar)
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

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
//        val params: CoordinatorLayout.LayoutParams  = appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
//        if (params.behavior == null) params.behavior = AppBarLayout.Behavior()
//        val behavior = params.behavior as AppBarLayout.Behavior
//        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
//            override fun canDrag(AppBarLayout: AppBarLayout): Boolean {
//                return true
//            }
//        })
    }

    private fun setupDrawerNav(){
//        drawerNavView.setNavigationItemSelectedListener(this)
//        val headerView = LayoutInflater.from(this).inflate(R.layout.activity_home_nav_header, null)
//        drawerNavView.addHeaderView(headerView)
//        drawerNavView.getHeaderView(0).visibility = View.GONE
        drawerNavAdapter = DrawerNavRcAdapter(drawerNavExpandableRc, childItemClickListener= this)
        drawerNavExpandableRc.layoutManager = LinearLayoutManager(this)
        drawerNavExpandableRc.itemAnimator = DefaultItemAnimator()
        drawerNavExpandableRc.adapter = drawerNavAdapter
    }

    private fun setupAdminFab(){
        if(BuildConfig.IS_ADMIN) fabAdmin.show() else fabAdmin.hide()
        fabAdmin.setOnClickListener {
//            presenter.putARecipe(
            val intent = Intent(this, Class.forName("com.example.linh.vietkitchen.admin.ui.screen.admin.AdminActivity"))
            val bundle = Bundle()
            bundle.putParcelableArrayList(Constants.BK_CATEGORIES, ArrayList(navItems))
            intent.putExtras(bundle)
            startActivityWithAnimation(intent)
        }
    }

    private fun showToolbar(){
        appBarLayout.postDelayed({
            appBarLayout.setExpanded(true, true)
        }, 500)
    }

    private fun setSearchSuggestionVisibility(isVisible: Boolean){
        if(!isSearchSuggestionAdapterAttached && isVisible){
            stub_search_suggestion.visibility = View.VISIBLE
            rcvSearchSuggestion.adapter = searchSuggestionAdapter
            rcvSearchSuggestion.layoutManager = LinearLayoutManager(this)
            isSearchSuggestionAdapterAttached = true
        }
        changeToolbarScrollable(!isVisible)
        rcvSearchSuggestion.visibility = if (isVisible) View.VISIBLE else View.GONE
        setBottomNavVisibility(!isVisible)
        setFabVisibility(!isVisible)
        Timber.d("setSearchSuggestionVisibility $isVisible")
    }

    private fun changeToolbarScrollable(scrollable: Boolean){
        // Show toolbar when we are in maps mode
        val params = toolbar.layoutParams as AppBarLayout.LayoutParams
        val appBarLayoutParams = appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
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

    private fun setMenuItemsVisibility(menu: Menu, exception: MenuItem, visible: Boolean) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            if (item !== exception) item.isVisible = visible
        }
        if (visible)invalidateOptionsMenu()
    }
    //endregion inner methods
}

//region inner classes ========================================================================
interface OnDrawerNavItemChangedListener{
    fun onDrawerNavChanged(category: String)
}
//endregion inner classes
