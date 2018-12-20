package com.example.linh.vietkitchen.ui.screen.home.homeFragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.color
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView.EndlessScrollListener
import com.example.linh.vietkitchen.ui.dialog.BottomSheetOptions
import com.example.linh.vietkitchen.ui.dialog.LoadingDialog
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.screen.home.homeActivity.HomeActivity
import com.example.linh.vietkitchen.ui.screen.home.homeActivity.OnDrawerNavItemChangedListener
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeFragment
import com.example.linh.vietkitchen.util.Constants
import com.example.linh.vietkitchen.util.VerticalSpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber




class HomeFragment : BaseHomeFragment<HomeFragmentContractView, HomeFragmentContractPresenter>(),
        HomeFragmentContractView, OnDrawerNavItemChangedListener {

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

    private var title: String = ""

    lateinit var rcvLoadMoreListener: EndlessScrollListener
    private val bottomSheetOptions: BottomSheetOptions by lazy {BottomSheetOptions()}
    private var loadingDialog: LoadingDialog? = null

    //region lifecycle =============================================================================
    override fun getFragmentLayoutRes() = R.layout.fragment_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        Timber.e("on create")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.e("on activity created")
        setupSwipeRefreshLayout()
        setupRecyclerView()
        presenter.refreshRecipes()
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
    override fun initPresenter(): HomeFragmentContractPresenter {
        return HomeFragmentPresenter()
    }

    override val viewContext: Context?
        get() = context

    override fun getViewContract(): HomeFragmentContractView {
        return this
    }

    override fun onNoInternetException() {
    }

    override fun onFoodsRequestSuccess(recipes: List<Recipe>) {
        recipeAdapter.updateItemThenNotify(recipes.toMutableList())
        swipeRefresh.isRefreshing = false
    }

    override fun onFoodsRequestFailed(msg: String) {
        toast(msg)
    }

    override fun onRefreshRecipe() {
//        onStartLoadMore()
    }

    override fun onLoadMoreSuccess(recipes: List<Recipe>) {
        toast("onLoadMoreSuccess ${recipes.size}")
        recipeAdapter.stopLoadMore()
        recipeAdapter.setMoreItems(recipes.toMutableList())
    }

    override fun onLoadMoreFailed() {
        recipeAdapter.stopLoadMore()
    }

    override fun onLoadMoreReachEndRecord() {
        recipeAdapter.stopLoadMore()
        toast("onLoadMoreReachEndRecord")
    }

    override fun onLikeEventObserve(recipe: Recipe) {
        recipeAdapter.onLike(recipe)
    }

    override fun onUnlikeEventObserve(recipe: Recipe) {
        recipeAdapter.onUnLike(recipe)
    }

    override fun onDeleteRecipeSuccess(adapterPosition: Int) {
        recipeAdapter.removeItem(adapterPosition)
        toast("recipe deleted successfully")
    }

    override fun onDeleteRecipeFailed(msg: String) {
        toast(msg)
    }

    override fun onStartLoadMore() {
        recipeAdapter.startLoadMore()
    }

    override fun showProgress() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.newInstance("delete")
        }
        loadingDialog!!.show(childFragmentManager, LoadingDialog::class.java.name)
    }

    override fun hideProgress() {
        loadingDialog?.let { loadingDialog ->
            if(loadingDialog.isVisible) loadingDialog.dismiss()
        }
    }
    //endregion MVP callbacks

    //region callbacks =============================================================================
    override fun onDrawerNavChanged(category: String) {
        if (title == category) return
        title = category
        presenter.refreshRecipes(category)
    }

    override fun onItemLongClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe): Boolean {
        bottomSheetOptions.listeners = object: BottomSheetOptions.BottomSheetOptionsListeners {
            override fun onDelete() {
                presenter.deleteRecipe(data, adapterPosition)
            }
        }
        bottomSheetOptions.show(childFragmentManager, BottomSheetOptions::class.java.name)
        return true
    }
    //endregion callbacks

    //region inner methods =========================================================================
    private fun setupSwipeRefreshLayout(){
        val progressColor1 = context?.color(R.color.color_wave_refresh_progress_1) ?: 0
        val progressColor2 = context?.color(R.color.color_wave_refresh_progress_2) ?: 0
        val progressColor3 = context?.color(R.color.color_wave_refresh_progress_3) ?: 0
        swipeRefresh.setColorSchemeColors(progressColor1, progressColor2, progressColor3)
        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
            rcvLoadMoreListener.onRefresh()
            presenter.refreshRecipes()
        }
    }
    //endregion inner methods

    //region inner classes =========================================================================
    private fun setupRecyclerView() {
//        recyclerView.itemAnimator = DefaultItemAnimator()
        rcvRecipes.layoutManager = LinearLayoutManager(context)
        rcvRecipes.addItemDecoration(VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.rcv_item_decoration), resources.getDimensionPixelSize(R.dimen.padding_16), resources.getDimensionPixelSize(R.dimen.padding_16)))
        rcvRecipes.adapter = recipeAdapter
        rcvLoadMoreListener = object : EndlessScrollListener(3) {
            override fun onLoadMore(page: Int, totalItemsCount: Int): Boolean {
                rcvRecipes.post { presenter.loadMoreRecipe() }
                Timber.d("rcvLoadMoreListener")
                return true
            }
        }
        rcvRecipes.addOnScrollListener(rcvLoadMoreListener)
    }
    //endregion inner classes
}
