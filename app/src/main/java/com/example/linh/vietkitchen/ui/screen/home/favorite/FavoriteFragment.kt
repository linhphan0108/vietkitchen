package com.example.linh.vietkitchen.ui.screen.home.favorite

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.adapter.RecipeAdapter
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeFragment
import com.example.linh.vietkitchen.util.VerticalSpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_home.*

class FavoriteFragment : BaseHomeFragment<FavoriteContractView, FavoriteContractPresenter>(), FavoriteContractView {
    companion object {
        @JvmStatic
        fun newInstance() = FavoriteFragment()
    }

    private val userInfo by lazy { VietKitchenApp.userInfo }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
        presenter.requestLikedRecipes(userInfo.likedRecipesIds)
    }

    override fun onDetach() {
        super.onDetach()
    }

    //region MVP callbacks =========================================================================
    override fun initPresenter() = FavoritePresenter()

    override fun getViewContract() = this

    override fun getFragmentLayoutRes() = R.layout.fragment_favorite

    override fun onNoInternetException() {
    }

    override fun onRequestLikedRecipesNoData() {
    }

    override fun onRequestLikedRecipesSuccess(recipes: List<Recipe>) {
        recipeAdapter.items = recipes.toMutableList()
        checkNoData()
    }

    override fun onRequestLikedRecipesFailed() {
    }

    override fun onLikeEventObserve(recipe: Recipe) {
        recipeAdapter.onLike(recipe)
        checkNoData()
    }

    override fun onUnlikeEventObserve(recipe: Recipe) {
        recipeAdapter.onUnLike(recipe, true)
        checkNoData()
    }

    override val viewContext: Context?
        get() = context

    override fun showProgress() {
    }

    override fun hideProgress() {
    }
    //endregion MVP callbacks

    //region inner methods =========================================================================
    private fun checkNoData(){
        if (recipeAdapter.itemCount <= 0){
            txtNoData.visibility = View.VISIBLE
        }else{
            txtNoData.visibility = View.GONE
        }
    }
    //endregion inner methods

    //region inner classes =========================================================================
    private fun setupRecyclerView() {
        rcvLikedRecipes.layoutManager = getRecyclerViewLayoutManager()
        rcvLikedRecipes.addItemDecoration(getRecyclerViewItemDecoration())
        rcvLikedRecipes.adapter = recipeAdapter
    }

    override fun requestRecyclerViewLayoutChange() {
        rcvRecipes.layoutManager = getRecyclerViewLayoutManager()
    }
    //endregion inner classes
}
