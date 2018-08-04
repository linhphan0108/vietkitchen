package com.example.linh.vietkitchen.ui.home.homeFragment

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.extension.color
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.ui.adapter.OnItemClickListener
import com.example.linh.vietkitchen.ui.adapter.RecipeAdapter
import com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView.EndlessScrollListener
import com.example.linh.vietkitchen.ui.screen.detailActivity.RecipeDetailActivity
import com.example.linh.vietkitchen.ui.home.homeActivity.HomeActivity
import com.example.linh.vietkitchen.ui.home.homeActivity.OnDrawerNavItemChangedListener
import com.example.linh.vietkitchen.ui.home.homeFragmentonRefresh.HomeFragmentContractPresenter
import com.example.linh.vietkitchen.ui.home.homeFragmentonRefresh.HomeFragmentContractView
import com.example.linh.vietkitchen.ui.mvpBase.ToolbarFragment
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : ToolbarFragment<HomeFragmentContractView, HomeFragmentContractPresenter>(),
        HomeFragmentContractView, OnDrawerNavItemChangedListener, OnItemClickListener {

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }

        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    private var param1: String? = null
    private var param2: String? = null

    lateinit var recipeAdapter: RecipeAdapter

    //region lifecycle =============================================================================
    override fun getFragmentLayoutRes() = R.layout.fragment_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
        presenter.refreshFoods()
    }

    override fun onStart() {
        super.onStart()
        Timber.e("on start")
    }

    override fun onResume() {
        super.onResume()
        toolbarActions?.changeToolbarTitle("home fragment")
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
    override fun initPresenter() = HomeFragmentPresenter()

    override val viewContext: Context?
        get() = context

    override fun getViewContract(): HomeFragmentContractView {
        return this
    }

    override fun onFoodsRequestSuccess(recipes: List<Recipe>) {
        recipeAdapter.updateItemThenNotify(recipes.toMutableList())
        swipeRefresh.isRefreshing = false
    }

    override fun onFoodsRequestFailed(msg: String) {
        toast(msg)
        recipeAdapter.stopShimmerAnimation()
    }

    override fun onRefreshRecipe() {
        recipeAdapter.startShimmerAnimation()
    }

    override fun onLoadingMore() {
//        recipeAdapter.startLoadMoreAnimation()
    }

    override fun onLoadMoreSuccess(recipes: List<Recipe>) {
        toast("onLoadMoreSuccess ${recipes.size}")
        recipeAdapter.stopLoadMoreAnimation()
        recipeAdapter.setMoreItems(recipes.toMutableList())
    }

    override fun onLoadMoreFailed() {
        recipeAdapter.stopLoadMoreAnimation()
    }

    override fun onLoadMoreReachEndRecord() {
        recipeAdapter.stopLoadMoreAnimation()
        toast("onLoadMoreReachEndRecord")
    }

    override fun showProgress() {
        recipeAdapter.startShimmerAnimation()
    }

    override fun hideProgress() {
    }
    //endregion MVP callbacks

    //region callbacks =============================================================================
    override fun onDrawerNavChanged(category: String?) {
        presenter.refreshFoods(category)
    }

    //recycler view callback
    override fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe) {
        val intent = RecipeDetailActivity.createIntent(context, layoutPosition.toString(), data)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.transitionName = layoutPosition.toString()
            val activityOptions = ActivityOptions.makeSceneTransitionAnimation(context as Activity, itemView, itemView.transitionName)
            context?.startActivity(intent, activityOptions.toBundle())
        }else{
            context?.startActivity(intent)
        }
    }
    //endregion callbacks

    //region inner methods =========================================================================
    private fun setupRecyclerView(){
        recipeAdapter = RecipeAdapter(listener = this)
        rcvFood.layoutManager = LinearLayoutManager(context)
        rcvFood.itemAnimator = DefaultItemAnimator()
        rcvFood.addItemDecoration(VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.rcv_item_decoration)))
        rcvFood.adapter = recipeAdapter
        rcvFood.addOnScrollListener(object : EndlessScrollListener(3){
            override fun onLoadMore(page: Int, totalItemsCount: Int): Boolean {
                presenter.loadMoreRecipe()
                return true
            }
        })
    }

    private fun setupSwipeRefreshLayout(){
        val waveColor = context?.color(R.color.color_wave_refresh) ?: 0
        val progressColor1 = context?.color(R.color.color_wave_refresh_progress_1) ?: 0
        val progressColor2 = context?.color(R.color.color_wave_refresh_progress_2) ?: 0
        val progressColor3 = context?.color(R.color.color_wave_refresh_progress_3) ?: 0
        swipeRefresh.setColorSchemeColors(progressColor1, progressColor2, progressColor3)
        swipeRefresh.setWaveColor(waveColor)
        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
            presenter.refreshFoods()
        }
    }
    //endregion inner methods

    //region inner classes =========================================================================
    inner class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                    state: RecyclerView.State) {
            if (parent.getChildAdapterPosition(view) == 0){
                outRect.top = verticalSpaceHeight
            }
            outRect.bottom = verticalSpaceHeight
        }
    }
    //endregion inner classes
}
