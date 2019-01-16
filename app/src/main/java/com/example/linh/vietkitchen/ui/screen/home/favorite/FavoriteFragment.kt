package com.example.linh.vietkitchen.ui.screen.home.favorite

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeFragment
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeViewModel

class FavoriteFragment : BaseHomeFragment() {
    companion object {
        @JvmStatic
        fun newInstance() = FavoriteFragment()
    }

    private lateinit var viewModel: FavoriteFragmentViewModel
    private val userInfo by lazy { VietKitchenApp.userInfo }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.requestLikedRecipes(userInfo.likedRecipesIds)
        }
    }



    //region MVP callbacks =========================================================================
    override fun getViewModel(): BaseHomeViewModel {
        val factory = FavoriteFragmentViewModelFactory(activity!!.application)
        viewModel = ViewModelProviders.of(this, factory).get(FavoriteFragmentViewModel::class.java)
        return viewModel
    }

    override fun onLikeEventObserve(recipe: Recipe) {
        recipeAdapter.onLike(recipe)
        setNoDataVisibility(recipeAdapter.itemCount > 0)
        if(recipeAdapter.itemCount > 0) {
            getRecyclerView().postDelayed({
                scrollToTop()
            }, 100)
        }
    }

    override fun onUnlikeEventObserve(recipe: Recipe) {
        recipeAdapter.onUnLike(recipe, true)
        setNoDataVisibility(recipeAdapter.itemCount > 0)
    }
    //endregion MVP callbacks

    //region inner methods =========================================================================
    override fun observeViewModel(){
        viewModel.requestLikeRecipesStatus.observe(this, Observer { box ->
            box?.let {
                when(box.code) {
                    Status.ERROR -> {onRequestRecipesFailed(box.message)}
                    Status.LOAD_MORE_ERROR -> { onLoadMoreFailed()}
                    Status.REFRESH -> {onStartRefresh()}
                    Status.LOAD_MORE -> { onStartLoadMore()}
                    Status.SUCCESS -> {
                        onRequestRecipesSuccess(box.data!!)
                        onStopRefresh()
                    }
                }
            }
        })
    }
    //endregion inner classes
}
