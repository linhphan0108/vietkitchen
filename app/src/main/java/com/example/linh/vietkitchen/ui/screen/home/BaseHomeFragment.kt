package com.example.linh.vietkitchen.ui.screen.home

import android.app.Activity
import android.app.ActivityOptions
import android.os.Build
import android.view.View
import com.example.linh.vietkitchen.ui.adapter.OnItemClickListener
import com.example.linh.vietkitchen.ui.adapter.RecipeAdapter
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.mvpBase.ToolbarFragment
import com.example.linh.vietkitchen.ui.screen.detailActivity.RecipeDetailActivity

abstract class BaseHomeFragment<V: BaseHomeContractView, P: BaseHomeContractPresenter<V>> : ToolbarFragment<V, P>(),
        OnItemClickListener {

    lateinit var recipeAdapter: RecipeAdapter

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

    override fun onLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe){
        presenter.likeRecipe(data)
    }

    override fun onUnLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe){
        presenter.unLikeRecipe(data)
    }

    //region inner methods =========================================================================
    protected open fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(listener = this)
    }
    //endregion inner methods

}