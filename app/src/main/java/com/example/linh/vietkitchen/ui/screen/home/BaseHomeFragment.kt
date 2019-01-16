package com.example.linh.vietkitchen.ui.screen.home

import android.app.Activity
import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.adapter.viewholder.OnItemClickListener
import com.example.linh.vietkitchen.ui.adapter.RecipeAdapter
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.detailActivity.BK_LIKE_STATE_JUST_CHANGED
import com.example.linh.vietkitchen.ui.screen.detailActivity.RecipeDetailActivity
import com.example.linh.vietkitchen.util.RecyclerViewLayoutMode
import com.example.linh.vietkitchen.util.VerticalSpaceItemDecoration
import com.example.linh.vietkitchen.util.VerticalStaggeredSpaceItemDecoration
import android.view.MenuItem
import com.example.linh.vietkitchen.extension.color
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.ui.baseMVVM.BaseToolbarFragment
import com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView.EndlessScrollListener
import com.example.linh.vietkitchen.util.Constants
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber


private const val REQUEST_DETAIL_RECIPE = 2
abstract class BaseHomeFragment : BaseToolbarFragment(),
        OnItemClickListener {

    private var recyclerViewLayoutMode = RecyclerViewLayoutMode.MODE_STAGGERED_VERTICAL
    private var lastItemClicked = -1
    lateinit var recipeAdapter: RecipeAdapter
    private lateinit var rcvLoadMoreListener: EndlessScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSwipeRefreshLayout()
        setupRecyclerView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_DETAIL_RECIPE){
            if (resultCode == Activity.RESULT_OK){
                val recipe = recipeAdapter.getItemAt(lastItemClicked) as Recipe
                val likeState: Boolean = data?.getBooleanExtra(BK_LIKE_STATE_JUST_CHANGED, recipe.hasLiked) ?: recipe.hasLiked
                if (likeState != recipe.hasLiked){
                    recipe.hasLiked = likeState
                    getViewModel().emitLikeOrUnlikeAction(recipe)
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Timber.d("onOptionsItemSelected in fragment")
        return when (item.itemId) {
            R.id.action_change_rc_layout ->{
                onChangeRecyclerViewLayoutClicked()
                item.setIcon(getActionIconForChangeRecyclerViewLayout())
                true
            }else -> {
                false
            }
        }
    }

    override fun getFragmentLayoutRes() = R.layout.fragment_home
    abstract override fun getViewModel(): BaseHomeViewModel
    internal abstract fun onLikeEventObserve(recipe: Recipe)
    internal abstract fun onUnlikeEventObserve(recipe: Recipe)

    //================== view model callbacks ======================================================
    protected fun onStartRefresh(){
        swipeRefresh.isRefreshing = true
        recipeAdapter.refresh()
    }

    protected fun onStopRefresh(){
        swipeRefresh.isRefreshing = false
    }

    protected fun onStartLoadMore() {
        recipeAdapter.startLoadMore()
    }

    protected fun onRequestRecipesSuccess(recipes: List<Recipe>) {
        recipeAdapter.items = recipes
        swipeRefresh.isRefreshing = false
        setNoDataVisibility(recipes.isNullOrEmpty())
    }

    protected fun onRequestRecipesNoData() {
    }

    protected fun onRequestRecipesFailed(msg: String?) {
        toast(msg ?: getString(R.string.error_msg))
    }

    protected fun onLoadMoreFailed() {
//        recipeAdapter.stopLoadMore()
    }

    //recycler view callback
    override fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe) {
        val intent = RecipeDetailActivity.createIntent(context, layoutPosition.toString(), data)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            itemView.transitionName = layoutPosition.toString()
//            val activityOptions = ActivityOptions.makeSceneTransitionAnimation(context as Activity, itemView, itemView.transitionName)
//            startActivityForResult(intent, REQUEST_DETAIL_RECIPE, activityOptions.toBundle())
//        }else{
            startActivityForResult(intent, REQUEST_DETAIL_RECIPE)
//        }
        lastItemClicked = adapterPosition
    }

    override fun onLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe){
        getViewModel().likeRecipe(data)
    }

    override fun onUnLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe){
        getViewModel().unLikeRecipe(data)
    }

    override fun onItemLongClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe): Boolean {
        return false
    }
    //region inner methods =========================================================================
    protected open fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(listener = this)
        val recyclerView = getRecyclerView()
        recyclerView.layoutManager = getRecyclerViewLayoutManager()
        recyclerView.addItemDecoration(getRecyclerViewItemDecoration())
        recyclerView.adapter = recipeAdapter
        rcvLoadMoreListener = object : EndlessScrollListener(Constants.VISIBLE_THRESHOLD_TO_LOAD_MORE) {
            override fun onLoadMore(page: Int, totalItemsCount: Int): Boolean {
                rcvRecipes.post { getViewModel().loadMoreRecipe() }
                Timber.d("rcvLoadMoreListener")
                return true
            }
        }
        rcvRecipes.addOnScrollListener(rcvLoadMoreListener)
    }

    private fun setupSwipeRefreshLayout() {
        val progressColor1 = context?.color(R.color.color_wave_refresh_progress_1) ?: 0
        val progressColor2 = context?.color(R.color.color_wave_refresh_progress_2) ?: 0
        val progressColor3 = context?.color(R.color.color_wave_refresh_progress_3) ?: 0
        swipeRefresh.setColorSchemeColors(progressColor1, progressColor2, progressColor3)
        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
            rcvLoadMoreListener.onRefresh()
            getViewModel().refreshRecipes()
        }
    }

    protected fun getRecyclerView() = rcvRecipes

    private fun getRecyclerViewLayoutManager(): androidx.recyclerview.widget.RecyclerView.LayoutManager{
        return when(recyclerViewLayoutMode){
            RecyclerViewLayoutMode.MODE_STAGGERED_VERTICAL -> {
                getStaggeredGridLayoutManager()
            }
            RecyclerViewLayoutMode.MODE_LINENEAR_VERTICAL -> {
                getLinearLayoutManager()
            }
        }
    }

    private fun getLinearLayoutManager(): androidx.recyclerview.widget.LinearLayoutManager {
        return androidx.recyclerview.widget.LinearLayoutManager(context)
    }

    private fun getStaggeredGridLayoutManager(): androidx.recyclerview.widget.StaggeredGridLayoutManager {
        return androidx.recyclerview.widget.StaggeredGridLayoutManager(2, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL)
    }

    private fun getRecyclerViewItemDecoration(): androidx.recyclerview.widget.RecyclerView.ItemDecoration{
        return when(recyclerViewLayoutMode){
            RecyclerViewLayoutMode.MODE_STAGGERED_VERTICAL -> {
                val margin = resources.getDimensionPixelSize(R.dimen.padding_16)
                VerticalStaggeredSpaceItemDecoration(margin, margin, margin)
            }
            RecyclerViewLayoutMode.MODE_LINENEAR_VERTICAL -> {
                val margin = resources.getDimensionPixelSize(R.dimen.padding_16)
                val verticalMargin = resources.getDimensionPixelSize(R.dimen.rcv_item_decoration)
                VerticalSpaceItemDecoration(verticalMargin, margin, margin)
            }
        }
    }

    private fun onChangeRecyclerViewLayoutClicked(){
        recyclerViewLayoutMode = when(recyclerViewLayoutMode){
            RecyclerViewLayoutMode.MODE_STAGGERED_VERTICAL -> RecyclerViewLayoutMode.MODE_LINENEAR_VERTICAL
            RecyclerViewLayoutMode.MODE_LINENEAR_VERTICAL -> RecyclerViewLayoutMode.MODE_STAGGERED_VERTICAL
        }
        requestRecyclerViewLayoutChange()
    }

    private fun getActionIconForChangeRecyclerViewLayout(): Int{
        return when(recyclerViewLayoutMode){
            RecyclerViewLayoutMode.MODE_STAGGERED_VERTICAL -> R.drawable.outline_view_module_24
            RecyclerViewLayoutMode.MODE_LINENEAR_VERTICAL -> R.drawable.outline_view_agenda_24
        }
    }

    fun scrollToTop(){
        getRecyclerView().stopScroll()
        getRecyclerView().smoothScrollToPosition(0)
    }

    protected fun setNoDataVisibility(isVisible: Boolean){
        if (isVisible){
            txtNoData.visibility = View.GONE
        }else{
            txtNoData.visibility = View.GONE
        }
    }

    private fun requestRecyclerViewLayoutChange() {
        rcvRecipes.layoutManager = getRecyclerViewLayoutManager()
    }

    override fun observeViewModel() {
        getViewModel().likeOrUnlikeAction.observe(this, Observer {recipe ->
            recipe?.let {
                if (it.hasLiked){
                    onLikeEventObserve(it)
                }else{
                    onUnlikeEventObserve(it)
                }
            }
        })
    }

    //endregion inner methods
}