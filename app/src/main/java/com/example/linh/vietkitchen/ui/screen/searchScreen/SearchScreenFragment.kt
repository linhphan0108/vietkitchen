package com.example.linh.vietkitchen.ui.screen.searchScreen

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.app.SearchManager
import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import androidx.appcompat.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.adapter.viewholder.OnItemClickListener
import com.example.linh.vietkitchen.ui.adapter.RecipeAdapter
import com.example.linh.vietkitchen.ui.adapter.SearchSuggestionAdapter
import com.example.linh.vietkitchen.ui.adapter.viewholder.SearchSuggestionViewHolder
import com.example.linh.vietkitchen.ui.baseMVVM.AbsJustToolbarFragment
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.di.injector
import com.example.linh.vietkitchen.di.viewModel
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.model.SearchItem
import com.example.linh.vietkitchen.ui.screen.detailActivity.RecipeDetailFragment
import com.example.linh.vietkitchen.util.ScreenUtil
import com.example.linh.vietkitchen.util.VerticalStaggeredSpaceItemDecoration
import kotlinx.android.synthetic.main.activity_search_screen_content.*
import kotlinx.android.synthetic.main.layout_search_view_suggestion.*
import timber.log.Timber

const val ARG_IS_SEARCH_VIEW_FOCUSED = "ARG_IS_SEARCH_VIEW_FOCUSED"
const val ARG_CURRENT_QUERY = "ARG_CURRENT_QUERY"
class SearchScreenFragment : AbsJustToolbarFragment(), OnItemClickListener,
        SearchView.OnQueryTextListener, SearchSuggestionViewHolder.OnItemListeners, MenuItem.OnActionExpandListener {

    private lateinit var optionsMenu: Menu
    private lateinit var searchMenuItem: MenuItem
    private val viewModel: SearchScreenViewModel by viewModel(this){ injector.searchScreenFragmentViewModel }
    private val searchSuggestionAdapter: SearchSuggestionAdapter by lazy { SearchSuggestionAdapter(mutableListOf(), this) }
    private lateinit var recipeAdapter: RecipeAdapter
    private var isSearchViewFocused = true
    private var currentQuery: String? = null
    private var isActivityJustRestore = false

    //region activity cycle callbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        setHasOptionsMenu(true)
        isActivityJustRestore = savedInstanceState != null
        savedInstanceState?.let {
            isSearchViewFocused = savedInstanceState.getBoolean(ARG_IS_SEARCH_VIEW_FOCUSED)
            currentQuery = savedInstanceState.getString(ARG_CURRENT_QUERY)
        }
        viewModel.requestTags()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()
        setupAppbar()
        setupAdapter()
        setupRecyclerView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(ARG_IS_SEARCH_VIEW_FOCUSED, isSearchViewFocused)
        outState.putString(ARG_CURRENT_QUERY, currentQuery)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.search_screen_options, menu)
        optionsMenu = menu

        // Associate searchable configuration with the SearchView
//        val searchManager = context!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchMenuItem = menu.findItem(R.id.action_search)
        val searchView = (searchMenuItem.actionView as SearchView).apply {
            //setSearchableInfo(searchManager.getSearchableInfo(ComponentName(this@HomeActivity, SearchScreenFragment::class.java)))
//            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            queryHint = getString(R.string.hint_search)
            setIconifiedByDefault(true) // Do not iconify the widget; expand it by default
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setOnQueryTextListener(this@SearchScreenFragment)
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
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Have the NavigationUI look for an action or destination matching the menu
        // item id and navigate there if found.
        // Otherwise, bubble up to the parent.
//    }

    override fun getFragmentLayoutRes() = R.layout.activity_search_screen_content

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }
    //region end activity cycle callbacks

    //region MVP implements ======================================================================
    private fun onRecipesRequestSuccess(recipes: List<Entity>) {
        recipeAdapter.items = recipes
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
        val bundle = RecipeDetailFragment.createBundle(context, layoutPosition.toString(), data)
        findNavController().navigate(R.id.action_search_screen_dest_to_detail_dest, bundle)
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
            setTitle(query.query)
            viewModel.refreshRecipes(query)
            recipeAdapter.items = listOf()
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
        VietKitchenApp.category.observe(this, Observer {
            viewModel.onCategoryChanged(it)
        })
    }

    private fun setupToolbar(){
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeButtonEnabled(true)
        getToolbar().setOnClickListener {
            forceSearchViewExpand()
        }
        setTitle("")
    }

    private fun setupAppbar(){
        val appBarElevationMax = ScreenUtil.dp2px(1)
        getAppbar().addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appbar, verticalOffset ->
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
        rcvRecipesResult.layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        rcvRecipesResult.addItemDecoration(VerticalStaggeredSpaceItemDecoration(margin, margin, margin))
        rcvRecipesResult.adapter = recipeAdapter
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
        setTitle(item.query)
        onMenuItemActionCollapse(searchMenuItem)
    }

    private fun setSearchSuggestionVisibility(isVisible: Boolean){
        if(rcvSearchSuggestion == null){
            stub_search_suggestion.visibility = View.VISIBLE
            rcvSearchSuggestion.adapter = searchSuggestionAdapter
            rcvSearchSuggestion.layoutManager = LinearLayoutManager(context)
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
        if (visible)activity?.invalidateOptionsMenu()
    }
    //region end internal methods
}