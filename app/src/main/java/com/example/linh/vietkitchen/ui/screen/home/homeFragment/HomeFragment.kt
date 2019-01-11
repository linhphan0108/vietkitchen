package com.example.linh.vietkitchen.ui.screen.home.homeFragment

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.color
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView.EndlessScrollListener
import com.example.linh.vietkitchen.ui.dialog.BottomSheetOptions
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.screen.home.homeActivity.HomeActivity
import com.example.linh.vietkitchen.ui.screen.home.homeActivity.OnDrawerNavItemChangedListener
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeFragment
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeViewModel
import com.example.linh.vietkitchen.util.Constants
import com.example.linh.vietkitchen.util.Constants.VISIBLE_THRESHOLD_TO_LOAD_MORE
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber

class HomeFragment : BaseHomeFragment(), OnDrawerNavItemChangedListener {
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance(navItems: List<DrawerNavGroupItem>) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(Constants.BK_CATEGORIES, ArrayList(navItems))
                    }
                }

        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeFragmentViewModel

    private var title: String = ""

    private lateinit var rcvLoadMoreListener: EndlessScrollListener
    private val bottomSheetOptions: BottomSheetOptions by lazy {BottomSheetOptions()}

    //region lifecycle =============================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        Timber.e("on create")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.e("on activity created")
        setupSwipeRefreshLayout()
        setupRecyclerView()
        observeViewModel()
        if (savedInstanceState == null) {
            viewModel.refreshRecipes()
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.e("on start")
    }

    override fun onResume() {
        super.onResume()
        toolbarActions?.changeToolbarTitle(title)
        Timber.e("on resume")
    }

    override fun onPause() {
        super.onPause()
        Timber.e("on pause")
    }

    override fun onStop() {
        super.onStop()
        Timber.e("on stop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.e("on destroy view")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.e("on destroy")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity){
            context.onDrawerNavItemChangedListener = this
        }
        Timber.e("on attach")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.e("on Detach")
    }
    //endregion lifecycle

    //region MVP callbacks =========================================================================
    override fun getFragmentLayoutRes() = R.layout.fragment_home
    override fun getViewModel(): BaseHomeViewModel {
        val factory = HomeFragmentViewModelFactory(activity!!.application)
        viewModel = ViewModelProviders.of(this, factory).get(HomeFragmentViewModel::class.java)
        return viewModel
    }

    private fun onFoodsRequestSuccess(recipes: List<Entity>) {
        recipeAdapter.items = recipes.toMutableList()
        swipeRefresh.isRefreshing = false
    }

    private fun onRequestRecipesFailed(msg: String) {
        toast(msg)
    }


    private fun onLoadMoreFailed() {
        recipeAdapter.stopLoadMore()
    }

    private fun onStartRefresh(){
        swipeRefresh.isRefreshing = true
        recipeAdapter.refresh()
    }

    private fun onStopRefresh(){
        swipeRefresh.isRefreshing = false
    }

    override fun onLikeEventObserve(recipe: Recipe) {
        recipeAdapter.onLike(recipe)
    }

    override fun onUnlikeEventObserve(recipe: Recipe) {
        recipeAdapter.onUnLike(recipe)
    }

    private fun onDeleteRecipeSuccess(adapterPosition: Int) {
        recipeAdapter.removeItem(adapterPosition)
        toast("recipe deleted successfully")
    }

    private fun onDeleteRecipeFailed(msg: String) {
        toast(msg)
    }

    fun onStartLoadMore() {
        recipeAdapter.startLoadMore()
    }
    //endregion MVP callbacks

    //region callbacks =============================================================================
    override fun onDrawerNavChanged(category: String) {
        if (title == category) return
        title = category
        viewModel.refreshRecipes(category)
    }

    override fun onItemLongClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe): Boolean {
        bottomSheetOptions.listeners = object: BottomSheetOptions.BottomSheetOptionsListeners {
            override fun onDelete() {
                viewModel.deleteRecipe(data, adapterPosition)
            }
        }
        bottomSheetOptions.show(childFragmentManager, BottomSheetOptions::class.java.name)
        return true
    }
    //endregion callbacks

    //region inner methods =========================================================================
    private fun setupSwipeRefreshLayout() {
        val progressColor1 = context?.color(R.color.color_wave_refresh_progress_1) ?: 0
        val progressColor2 = context?.color(R.color.color_wave_refresh_progress_2) ?: 0
        val progressColor3 = context?.color(R.color.color_wave_refresh_progress_3) ?: 0
        swipeRefresh.setColorSchemeColors(progressColor1, progressColor2, progressColor3)
        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
            rcvLoadMoreListener.onRefresh()
            viewModel.refreshRecipes()
        }
    }

    override fun observeViewModel(){
        viewModel.requestRecipesStatus.observe(this, Observer { box ->
            box?.let {
                when(box.code){
                    Status.ERROR -> {onRequestRecipesFailed(box.message ?: getString(R.string.error_msg))}
                    Status.LOAD_MORE_ERROR -> {onLoadMoreFailed()}
                    Status.REFRESH -> {onStartRefresh()}
                    Status.LOAD_MORE,
                    Status.SUCCESS -> {
                        onFoodsRequestSuccess(box.data!!)
                        onStopRefresh()
                    }
                }
            }
        })
        viewModel.deleteRecipeStatus.observe(this, Observer {statusBox ->
            statusBox?.let {
                when(it.code){
                    Status.SUCCESS -> {onDeleteRecipeSuccess(it.data!!)}
                    Status.ERROR -> {onDeleteRecipeFailed(it.message!!)}
                }
            }
        })
    }

    override fun setupRecyclerView() {
        super.setupRecyclerView()
        rcvLoadMoreListener = object : EndlessScrollListener(VISIBLE_THRESHOLD_TO_LOAD_MORE) {
            override fun onLoadMore(page: Int, totalItemsCount: Int): Boolean {
                rcvRecipes.post { viewModel.loadMoreRecipe() }
                Timber.d("rcvLoadMoreListener")
                return true
            }
        }
        rcvRecipes.addOnScrollListener(rcvLoadMoreListener)
    }

    override fun getRecyclerView(): androidx.recyclerview.widget.RecyclerView = rcvRecipes

    override fun requestRecyclerViewLayoutChange() {
        rcvRecipes.layoutManager = getRecyclerViewLayoutManager()
    }

    fun scrollToTop(){
        rcvRecipes.stopScroll()
        rcvRecipes.smoothScrollToPosition(0)
    }

    //endregion inner classes
}
