package com.example.linh.vietkitchen.ui.screen.searchScreen

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.adapter.OnItemClickListener
import com.example.linh.vietkitchen.ui.adapter.RecipeAdapter
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.model.SearchItem
import com.example.linh.vietkitchen.ui.mvpBase.BaseActivity
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.util.ScreenUtil
import com.example.linh.vietkitchen.util.VerticalStaggeredSpaceItemDecoration
import kotlinx.android.synthetic.main.activity_home_app_bar.*
import kotlinx.android.synthetic.main.activity_search_screen_content.*
import timber.log.Timber

class SearchScreenActivity : BaseActivity<SearchContractView>(), SearchContractView, OnItemClickListener {

    private val presenter: SearchScreenContractPresenter by lazy {SearchScreenActivityPresenter()}
    private lateinit var recipeAdapter: RecipeAdapter
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, SearchScreenActivity::class.java)
        }
    }

    //region activity cycle callbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        setupToolbar()
        setupAppbar()
        setupAdapter()
        setupRecyclerView()
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_screen_options, menu)
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

    //region end activity cycle callbacks

    //region MVP implements ======================================================================
    override fun getPresenter(): BasePresenterContract<SearchContractView> {
        return presenter
    }

    override fun getViewContract(): SearchContractView = this

    override fun getActivityLayoutRes(): Int = R.layout.activity_search_screen_app_bar

    override val viewContext: Context?
        get() = this

    override fun onNoInternetException() {
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun onStartLoadMore() {

    }

    override fun onFoodsRequestSuccess(recipes: List<Recipe>) {
        recipeAdapter.items = recipes.toMutableList()
        checkNoData()
    }

    override fun onFoodsRequestFailed(msg: String) {
    }

    override fun onRefreshRecipe() {
    }

    override fun onLoadMoreSuccess(recipes: List<Recipe>) {
    }

    override fun onLoadMoreFailed() {
    }

    override fun onLoadMoreReachEndRecord() {
    }

    //region end MVP implements

    //region recipe adapter callbacks ==============================================================
    override fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe) {
    }

    override fun onItemLongClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe): Boolean {
        return true
    }

    override fun onLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe) {
    }

    override fun onUnLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe) {
    }//region end recipe adapter callbacks =========================================================

    //region internal methods ======================================================================
    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getParcelableExtra<SearchItem>(SearchManager.USER_QUERY)
            //use the query to search your data somehow
            Timber.d("handle intent -> query = $query")
            title = query.query
            presenter.searchRecipesBy(query)
            recipeAdapter.items = mutableListOf()
        }
    }

    private fun setupToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
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
    //region end internal methods
}