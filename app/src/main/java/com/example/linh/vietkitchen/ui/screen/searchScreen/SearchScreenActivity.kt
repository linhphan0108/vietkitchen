package com.example.linh.vietkitchen.ui.screen.searchScreen

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.ui.adapter.OnItemClickListener
import com.example.linh.vietkitchen.ui.adapter.RecipeAdapter
import com.example.linh.vietkitchen.ui.adapter.SearchSuggestionAdapter
import com.example.linh.vietkitchen.ui.adapter.SearchSuggestionViewHolder
import com.example.linh.vietkitchen.ui.baseMVVM.BaseActivity
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.model.SearchItem
import com.example.linh.vietkitchen.ui.screen.detailActivity.RecipeDetailActivity
import com.example.linh.vietkitchen.util.ScreenUtil
import com.example.linh.vietkitchen.util.VerticalStaggeredSpaceItemDecoration
import kotlinx.android.synthetic.main.activity_search_screen_app_bar.*
import kotlinx.android.synthetic.main.activity_search_screen_content.*
import kotlinx.android.synthetic.main.layout_search_view_suggestion.*
import timber.log.Timber

const val ARG_IS_SEARCH_VIEW_FOCUSED = "ARG_IS_SEARCH_VIEW_FOCUSED"
const val ARG_CURRENT_QUERY = "ARG_CURRENT_QUERY"
class SearchScreenActivity : BaseActivity(), OnItemClickListener,
        SearchView.OnQueryTextListener, SearchSuggestionViewHolder.OnItemListeners, MenuItem.OnActionExpandListener {
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, SearchScreenActivity::class.java)
        }
    }

    private lateinit var optionsMenu: Menu
    private lateinit var searchMenuItem: MenuItem
    private lateinit var viewModel: SearchScreenViewModel
    private val searchSuggestionAdapter: SearchSuggestionAdapter by lazy { SearchSuggestionAdapter(mutableListOf(), this) }
    private lateinit var recipeAdapter: RecipeAdapter
    private var isSearchViewFocused = true
    private var currentQuery: String? = null
    private var isActivityJustRestore = false

    //region activity cycle callbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        isActivityJustRestore = savedInstanceState != null
        savedInstanceState?.let {
            isSearchViewFocused = savedInstanceState.getBoolean(ARG_IS_SEARCH_VIEW_FOCUSED)
            currentQuery = savedInstanceState.getString(ARG_CURRENT_QUERY)
        }

        setupToolbar()
        setupAppbar()
        setupAdapter()
        setupRecyclerView()
        handleIntent(intent)

        observeViewModel()
        viewModel.requestTags()
        viewModel.requestCategory()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(ARG_IS_SEARCH_VIEW_FOCUSED, isSearchViewFocused)
        outState.putString(ARG_CURRENT_QUERY, currentQuery)
        super.onSaveInstanceState(outState)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_screen_options, menu)
        optionsMenu = menu

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchMenuItem = menu.findItem(R.id.action_search)
        val searchView = (searchMenuItem.actionView as SearchView).apply {
            //setSearchableInfo(searchManager.getSearchableInfo(ComponentName(this@HomeActivity, SearchScreenActivity::class.java)))
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            queryHint = getString(R.string.hint_search)
            setIconifiedByDefault(true) // Do not iconify the widget; expand it by default
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setOnQueryTextListener(this@SearchScreenActivity)
            Timber.d("SearchView created")
            setOnQueryTextFocusChangeListener {_, hasFocus ->
                isSearchViewFocused = hasFocus
            }

        }
        searchMenuItem.setOnActionExpandListener(this)
        if (isSearchViewFocused) {
            forceSearchViewExpand()
            currentQuery?.let { searchView.setQuery(it, false) }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home->{
                onBackPressed()
                true
            }
            else ->{
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun getActivityLayoutRes(): Int = R.layout.activity_search_screen_app_bar

    override fun getViewModel(): BaseViewModel {
        val factory = SearchScreenViewModelFactory(application)
        viewModel = ViewModelProviders.of(this, factory).get(SearchScreenViewModel::class.java)
        return viewModel
    }
    //region end activity cycle callbacks

    //region MVP implements ======================================================================
    private fun onRecipesRequestSuccess(recipes: MutableList<Entity>) {
        recipeAdapter.items = recipes.toMutableList()
        checkNoData()
        hideProgress()
    }

    private fun onRecipesRequestFailed(msg: String) {
        hideProgress()
    }

    fun onRefreshRecipe() {
        showProgress()
    }

    fun onLoadMoreSuccess(recipes: List<Recipe>) {
    }

    private fun onLoadMoreFailed() {
    }

    fun onLoadMoreReachEndRecord() {
    }
    fun onGetTagsSuccess(tags: List<SearchItem>) {
    }

    private fun onGetTagsFailed(message: String?) {
        message?.let { toast(it)}
    }

    private fun onFilteredSuggestion(filteredList: List<SearchItem>) {
        searchSuggestionAdapter.items = filteredList.toMutableList()
    }

    fun onRequestCategoriesSuccess(items: List<DrawerNavGroupItem>) {
    }

    private fun onRequestCategoriesFailed(message: String) {
    }
    //region end MVP implements

    //region recipe adapter callbacks ==============================================================
    override fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe) {
        val intent = RecipeDetailActivity.createIntent(this, "", data)
        startActivity(intent)
    }

    override fun onItemLongClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe): Boolean {
        return true
    }

    override fun onLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe) {
    }

    override fun onUnLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe) {
    }//region end recipe adapter callbacks =========================================================

    //SearchView's callback
    override fun onQueryTextSubmit(query: String?): Boolean {
        Timber.d("onQueryTextSubmit = $query")
        return query?.let {
            val searchItem = SearchItem(query, SearchItem.SearchItemType.TAG)
            onSearchViewSubmit(searchItem)
            true
        } ?: false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        //since after activity restored due to configuration changed
        //the SearchView will set the search box to be blank
        if(isActivityJustRestore && query.isNullOrBlank()){
            isActivityJustRestore = false
            return true
        }
        if (query == currentQuery){
            return true
        }
        currentQuery = query
        viewModel.filterSearchSuggestions(query)
        return true
    }

    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
        Timber.d("SearchView onMenuItemActionExpand")
        setMenuItemsVisibility(item, false)
        setSearchSuggestionVisibility(true)
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
        Timber.d("SearchView onMenuItemActionCollapse")
        setMenuItemsVisibility(item, true)
        setSearchSuggestionVisibility(false)
        return true
    }

    //on search item suggestion clicked
    override fun onItemClick(item: SearchItem) {
        onSearchViewSubmit(item)
    }

    //region internal methods ======================================================================
    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getParcelableExtra<SearchItem>(SearchManager.USER_QUERY)
            //use the query to search your data somehow
            Timber.d("handle intent -> query = $query")
            title = query.query
            viewModel.refreshRecipes(query)
            recipeAdapter.items = mutableListOf()
        }
    }

    override fun observeViewModel(){
        viewModel.filteredSuggestion.observe(this, Observer { listSearchItem ->
            listSearchItem?.let { onFilteredSuggestion(it) }
        })
        viewModel.listRecipes.observe(this, Observer {listRecipes ->
            listRecipes?.let { onRecipesRequestSuccess(it) }
        })
        viewModel.requestRecipesStatus.observe(this, Observer {
            when(it){
                Status.LOADING -> {}
                Status.LOAD_MORE -> {}
                Status.ERROR -> onRecipesRequestFailed("")
                Status.LOAD_MORE_ERROR -> onLoadMoreFailed()
            }
        })
        viewModel.requestCategoriesStatus.observe(this, Observer {
            when(it){
                Status.LOADING -> {}
                Status.LOAD_MORE -> {}
                Status.ERROR -> onRequestCategoriesFailed("")
                Status.LOAD_MORE_ERROR -> {}
            }
        })
        viewModel.requestTagsStatus.observe(this, Observer {
            when(it){
                Status.LOADING -> {}
                Status.LOAD_MORE -> {}
                Status.ERROR -> onGetTagsFailed("")
                Status.LOAD_MORE_ERROR -> {}
            }
        })
    }

    private fun setupToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        toolbar.setOnClickListener {
            forceSearchViewExpand()
        }
        title = ""
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

    private fun setupAdapter() {
        recipeAdapter = RecipeAdapter(listener = this)
    }

    private fun setupRecyclerView() {
        val margin = resources.getDimensionPixelSize(R.dimen.padding_16)
        rcvLikedRecipes.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        rcvLikedRecipes.addItemDecoration(VerticalStaggeredSpaceItemDecoration(margin, margin, margin))
        rcvLikedRecipes.adapter = recipeAdapter
    }

    private fun checkNoData(){
        if (recipeAdapter.itemCount <= 0){
            txtNoData.visibility = View.VISIBLE
        }else{
            txtNoData.visibility = View.GONE
        }
    }

    private fun onSearchViewSubmit(item: SearchItem){
        viewModel.refreshRecipes(item)
        searchMenuItem.collapseActionView()
        title = item.query
        onMenuItemActionCollapse(searchMenuItem)
    }

    private fun setSearchSuggestionVisibility(isVisible: Boolean){
        if(rcvSearchSuggestion == null){
            stub_search_suggestion.visibility = View.VISIBLE
            rcvSearchSuggestion.adapter = searchSuggestionAdapter
            rcvSearchSuggestion.layoutManager = LinearLayoutManager(this)
        }
        rcvSearchSuggestion.visibility =  if (isVisible) View.VISIBLE else View.GONE
        Timber.d("setSearchSuggestionVisibility $isVisible")
    }

    private fun forceSearchViewExpand(){
        searchMenuItem.expandActionView()
        onMenuItemActionExpand(searchMenuItem)
    }

    private fun setMenuItemsVisibility(exception: MenuItem, visible: Boolean) {
        for (i in 0 until optionsMenu.size()) {
            val item = optionsMenu.getItem(i)
            if (item !== exception) item.isVisible = visible
        }
        if (visible)invalidateOptionsMenu()
    }
    //region end internal methods
}