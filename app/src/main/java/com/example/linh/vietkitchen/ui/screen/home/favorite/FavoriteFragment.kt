package com.example.linh.vietkitchen.ui.home

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeFragment
import com.example.linh.vietkitchen.ui.screen.home.favorite.FavoriteContractPresenter
import com.example.linh.vietkitchen.ui.screen.home.favorite.FavoriteContractView
import com.example.linh.vietkitchen.ui.screen.home.favorite.FavoritePresenter
import com.example.linh.vietkitchen.util.VerticalSpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_favorite.*

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

    override fun onRequestLikedRecipesSuccess(recipes: List<Recipe>) {
        recipeAdapter.updateItemThenNotify(recipes.toMutableList())
    }

    override fun onRequestLikedRecipesFailed() {
    }

    override fun onLikeEventObserve(recipe: Recipe) {
        recipeAdapter.onLike(recipe)
    }

    override fun onUnlikeEventObserve(recipe: Recipe) {
        recipeAdapter.onUnLike(recipe, true)
    }

    override val viewContext: Context?
        get() = context

    override fun showProgress() {
    }

    override fun hideProgress() {
    }
    //endregion MVP callbacks

    //region inner methods =========================================================================
    //endregion inner methods

    //region inner classes =========================================================================
    override fun setupRecyclerView(){
        super.setupRecyclerView()
        rcvLikedRecipes.layoutManager = LinearLayoutManager(context)
        rcvLikedRecipes.itemAnimator = DefaultItemAnimator()
        rcvLikedRecipes.addItemDecoration(VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.rcv_item_decoration)))
        rcvLikedRecipes.adapter = recipeAdapter
    }
    //endregion inner classes
}
