package com.example.linh.vietkitchen.ui.screen.home.favorite

import androidx.lifecycle.Observer
import android.os.Bundle
import com.example.linh.vietkitchen.di.injector
import com.example.linh.vietkitchen.di.viewModel
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.AbsHomeFragment
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeViewModel
import com.example.linh.vietkitchen.vo.Status.*

class FavoriteFragment : AbsHomeFragment() {

    private val viewModel: FavoriteFragmentViewModel by viewModel(this){ injector.favoriteFragmentViewModel }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.refreshRecipes()
        }
    }

    //region MVP callbacks =========================================================================
    override fun getViewModel(): BaseHomeViewModel {return viewModel
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
        super.observeViewModel()
        viewModel.requestLikeRecipesStatus.observe(this, Observer { resource ->
            when(resource.status) {
                LOADING -> {}
                ERROR -> {onRequestRecipesFailed(resource.message)}
//                Status.LOAD_MORE_ERROR -> { onLoadMoreFailed()}
//                Status.REFRESH -> {onStartRefresh()}
//                Status.LOAD_MORE -> { onStartLoadMore()}
                SUCCESS -> {
                    onRequestRecipesSuccess(resource.data!!)
                    onStopRefresh()
                }
            }
        })
    }
    //endregion inner classes
}
