package com.example.linh.vietkitchen.ui.screen.home.homeFragment

import androidx.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.di.injector
import com.example.linh.vietkitchen.di.viewModel
import com.example.linh.vietkitchen.ui.dialog.BottomSheetOptions
import com.example.linh.vietkitchen.ui.screen.home.homeActivity.HomeActivity
import com.example.linh.vietkitchen.ui.screen.home.homeActivity.OnDrawerNavItemChangedListener
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.AbsHomeFragment
import timber.log.Timber

class HomeFragment : AbsHomeFragment(), OnDrawerNavItemChangedListener {

    private val viewModel: HomeFragmentViewModel by viewModel(this){injector.homeFragmentViewModel}

    private var category: String = "Tất Cả"

    private val bottomSheetOptions: BottomSheetOptions by lazy {BottomSheetOptions()}

    //region lifecycle =============================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        getViewModel().refreshRecipes()
        Timber.e("on create")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.e("on activity created")
    }

    override fun onStart() {
        super.onStart()
        Timber.e("on start")
    }

    override fun onResume() {
        super.onResume()
        setTitle(category)
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
    override fun getViewModel() =  viewModel

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
        if (this.category == category) return
        this.category = category
        setTitle(category)
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
        super.observeViewModel()
        getViewModel().listRecipeLiveData.observe(this, Observer { resource ->
            when(resource.status){
                com.example.linh.vietkitchen.vo.Status.SUCCESS -> {
                    onRequestRecipesSuccess(resource.data!!)
                    onStopRefresh()
                }
                com.example.linh.vietkitchen.vo.Status.LOADING -> {

                }
                com.example.linh.vietkitchen.vo.Status.ERROR -> {
                    onRequestRecipesFailed(resource.message)
                }
//                    Status.LOAD_MORE_ERROR -> {onLoadMoreFailed()}
//                    Status.REFRESH -> {onStartRefresh()}
//                    Status.LOAD_MORE -> {onStartLoadMore()}
            }
        })
        getViewModel().deleteRecipeStatus.observe(this, Observer {resource ->
                when(resource.status){
                    com.example.linh.vietkitchen.vo.Status.SUCCESS -> {onDeleteRecipeSuccess(resource.data!!)}
                    com.example.linh.vietkitchen.vo.Status.ERROR -> {onDeleteRecipeFailed(resource.message!!)}
                }
        })
    }
    //endregion inner classes
}
