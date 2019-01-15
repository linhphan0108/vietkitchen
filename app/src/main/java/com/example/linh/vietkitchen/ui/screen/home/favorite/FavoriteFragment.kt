package com.example.linh.vietkitchen.ui.screen.home.favorite

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeFragment
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeViewModel
import kotlinx.android.synthetic.main.fragment_favorite.*

class FavoriteFragment : BaseHomeFragment() {
    companion object {
        @JvmStatic
        fun newInstance() = FavoriteFragment()
    }

    private lateinit var viewModel: FavoriteFragmentViewModel
    private val userInfo by lazy { VietKitchenApp.userInfo }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
        if (savedInstanceState == null) {
            viewModel.requestLikedRecipes(userInfo.likedRecipesIds)
        }
    }



    //region MVP callbacks =========================================================================
    override fun getFragmentLayoutRes() = R.layout.fragment_favorite
    override fun getViewModel(): BaseHomeViewModel {
        val factory = FavoriteFragmentViewModelFactory(activity!!.application)
        viewModel = ViewModelProviders.of(this, factory).get(FavoriteFragmentViewModel::class.java)
        return viewModel
    }

    private fun onRequestLikedRecipesNoData() {
    }

    private fun onRequestLikedRecipesSuccess(recipes: List<Recipe>) {
        recipeAdapter.items = recipes
        setNoDataVisibility(recipes.isNullOrEmpty())
    }

    private fun onRequestLikedRecipesFailed() {
    }

    override fun onLikeEventObserve(recipe: Recipe) {
        recipeAdapter.onLike(recipe)
        setNoDataVisibility(recipeAdapter.itemCount > 0)
        if(recipeAdapter.itemCount > 0) {
            rcvLikedRecipes.postDelayed({
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
                when(it.code){
                    Status.SUCCESS -> {onRequestLikedRecipesSuccess(it.data!!)}
                    Status.ERROR_EMPTY -> {onRequestLikedRecipesNoData()}
                    Status.ERROR -> {onRequestLikedRecipesFailed()}
                }
            }
        })
    }

    private fun setNoDataVisibility(isVisible: Boolean){
        if (isVisible){
            txtNoData.visibility = View.GONE
        }else{
            txtNoData.visibility = View.GONE
        }
    }

    override fun getRecyclerView(): RecyclerView = rcvLikedRecipes

    override fun requestRecyclerViewLayoutChange() {
        rcvLikedRecipes.layoutManager = getRecyclerViewLayoutManager()
    }
    //endregion inner classes
}
