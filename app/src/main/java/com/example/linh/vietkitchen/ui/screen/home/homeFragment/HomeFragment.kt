package com.example.linh.vietkitchen.ui.screen.home.homeFragment

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.dialog.BottomSheetOptions
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.screen.home.homeActivity.HomeActivity
import com.example.linh.vietkitchen.ui.screen.home.homeActivity.OnDrawerNavItemChangedListener
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeFragment
import com.example.linh.vietkitchen.util.Constants
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

    private var viewModel: HomeFragmentViewModel? = null

    private var title: String = ""

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
        if (savedInstanceState == null) {
            getViewModel().refreshRecipes()
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
    override fun getViewModel(): HomeFragmentViewModel {
        if(viewModel == null) {
            val factory = HomeFragmentViewModelFactory(activity!!.application)
            viewModel = ViewModelProviders.of(this, factory).get(HomeFragmentViewModel::class.java)
        }
        return viewModel!!
    }

    override fun onLikeEventObserve(recipe: Recipe) {
        recipeAdapter.onLike(recipe)
    }

    override fun onUnlikeEventObserve(recipe: Recipe) {
        recipeAdapter.onUnLike(recipe)
    }

    private fun onDeleteRecipeSuccess(adapterPosition: Int) {
//        recipeAdapter.removeItem(adapterPosition)
        toast("recipe deleted successfully")
    }

    private fun onDeleteRecipeFailed(msg: String) {
        toast(msg)
    }

    //endregion MVP callbacks

    //region callbacks =============================================================================
    override fun onDrawerNavChanged(category: String) {
        if (title == category) return
        title = category
        getViewModel().refreshRecipesByCat(category)
    }

    override fun onItemLongClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe): Boolean {
        bottomSheetOptions.listeners = object: BottomSheetOptions.BottomSheetOptionsListeners {
            override fun onDelete() {
                getViewModel().deleteRecipe(data, adapterPosition)
            }
        }
        bottomSheetOptions.show(childFragmentManager, BottomSheetOptions::class.java.name)
        return true
    }
    //endregion callbacks

    //region inner methods =========================================================================
    override fun observeViewModel(){
        getViewModel().requestRecipesStatus.observe(this, Observer { box ->
            box?.let {
                when(box.code){
                    Status.ERROR -> {onRequestRecipesFailed(box.message)}
                    Status.LOAD_MORE_ERROR -> {onLoadMoreFailed()}
                    Status.REFRESH -> {onStartRefresh()}
                    Status.LOAD_MORE -> {onStartLoadMore()}
                    Status.SUCCESS -> {
                        onRequestRecipesSuccess(box.data!!)
                        onStopRefresh()
                    }
                }
            }
        })
        getViewModel().deleteRecipeStatus.observe(this, Observer {statusBox ->
            statusBox?.let {
                when(it.code){
                    Status.SUCCESS -> {onDeleteRecipeSuccess(it.data!!)}
                    Status.ERROR -> {onDeleteRecipeFailed(it.message!!)}
                }
            }
        })
    }
    //endregion inner classes
}
