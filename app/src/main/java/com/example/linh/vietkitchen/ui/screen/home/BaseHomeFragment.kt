package com.example.linh.vietkitchen.ui.screen.home

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.adapter.OnItemClickListener
import com.example.linh.vietkitchen.ui.adapter.RecipeAdapter
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.mvpBase.ToolbarFragment
import com.example.linh.vietkitchen.ui.screen.detailActivity.BK_LIKE_STATE_JUST_CHANGED
import com.example.linh.vietkitchen.ui.screen.detailActivity.RecipeDetailActivity
import com.example.linh.vietkitchen.util.VerticalSpaceItemDecoration

private const val REQUEST_DETAIL_RECIPE = 2
abstract class BaseHomeFragment<V: BaseHomeContractView, P: BaseHomeContractPresenter<V>> : ToolbarFragment<V, P>(),
        OnItemClickListener {

    private var lastItemClicked = -1
    lateinit var recipeAdapter: RecipeAdapter
    lateinit var txtNoDataSuper: TextView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupAdapter()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_DETAIL_RECIPE){
            if (resultCode == Activity.RESULT_OK){
                val recipe = recipeAdapter.getItemAt(lastItemClicked) as Recipe
                val likeState: Boolean = data?.getBooleanExtra(BK_LIKE_STATE_JUST_CHANGED, recipe.hasLiked) ?: recipe.hasLiked
                if (likeState != recipe.hasLiked){
                    recipe.hasLiked = likeState
                    if (likeState){
                        presenter.emitLike(recipe)
                    }else{
                        presenter.emitUnLike(recipe)
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    //recycler view callback
    override fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe) {
        val intent = RecipeDetailActivity.createIntent(context, layoutPosition.toString(), data)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.transitionName = layoutPosition.toString()
            val activityOptions = ActivityOptions.makeSceneTransitionAnimation(context as Activity, itemView, itemView.transitionName)
            startActivityForResult(intent, REQUEST_DETAIL_RECIPE, activityOptions.toBundle())
        }else{
            startActivityForResult(intent, REQUEST_DETAIL_RECIPE)
        }
        lastItemClicked = adapterPosition
    }

    override fun onLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe){
        presenter.likeRecipe(data)
    }

    override fun onUnLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe){
        presenter.unLikeRecipe(data)
    }

    override fun onItemLongClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe): Boolean {
        return false
    }
    //region inner methods =========================================================================
    protected open fun setupAdapter() {
        recipeAdapter = RecipeAdapter(listener = this)
    }
    //endregion inner methods
}