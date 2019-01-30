package com.example.linh.vietkitchen.ui.screen.home.favorite

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.domain.command.RequestLikedRecipesCommand
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.baseMVVM.StatusBox
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeViewModel
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.util.Constants
import timber.log.Timber

class FavoriteFragmentViewModel(application: Application,
        private val recipeMapper: RecipeMapper = RecipeMapper(),
        private val likedRecipesCommand: RequestLikedRecipesCommand = RequestLikedRecipesCommand())
    : BaseHomeViewModel(application){

    private val userInfo by lazy { VietKitchenApp.getUserInfo() }
    internal val requestLikeRecipesStatus: MutableLiveData<StatusBox<List<Recipe>>> = MutableLiveData()
    private var listRecipes: MutableList<Recipe> = mutableListOf()
    private var isLoadMoreRecipe = false
    private var isFreshRecipe = false
    private var hasReachLastRecord = false

    override fun refreshRecipes() {
        isFreshRecipe = true
        isLoadMoreRecipe = false
        hasReachLastRecord = false
        listRecipes.clear()
        requestLikeRecipesStatus.value = StatusBox(Status.REFRESH, data = listRecipes)
        requestLikedRecipes(getNextPageLikedRecipesIds(0))
    }

    override fun loadMoreRecipe() {
        if (hasReachLastRecord || isLoadMoreRecipe || isFreshRecipe) return
        val from = listRecipes.size
        val count = userInfo.likedRecipesIds?.size ?: 0
        if(from >= count){
            hasReachLastRecord = true
            return
        }
        isLoadMoreRecipe = true
        requestLikeRecipesStatus.value = StatusBox(Status.LOAD_MORE)
        requestLikedRecipes(getNextPageLikedRecipesIds(from))
    }

    private fun requestLikedRecipes(ids: List<String>?) {
        if (ids.isNullOrEmpty()){
            requestLikeRecipesStatus.value = StatusBox(Status.ERROR_EMPTY)
            return
        }
        launchDataLoad(ioBlock = {
            val likedRecipes = withIoContext {
                likedRecipesCommand.ids = ids
                val response = likedRecipesCommand.execute(getApplication())
                recipeMapper.convertToUi(response.data!!, true)
            }

            listRecipes.addAll(likedRecipes)
            requestLikeRecipesStatus.value = StatusBox(Status.SUCCESS, data = listRecipes.toList())
            isLoadMoreRecipe = false
            isFreshRecipe = false
        }, onError = {
            when{
                isLoadMoreRecipe -> {
                    requestLikeRecipesStatus.value = StatusBox(Status.LOAD_MORE_ERROR)
                    isLoadMoreRecipe = false
                }
                else -> {
                    requestLikeRecipesStatus.value = StatusBox(Status.ERROR)
                    isFreshRecipe = false
                }
            }
            Timber.e(it)
        })

    }

    private fun getNextPageLikedRecipesIds(from: Int): List<String>?{
        return userInfo.likedRecipesIds?.let { listLikedRecipesIds ->
            val count = listLikedRecipesIds.size
            val to = if (from + Constants.PAGINATION_LENGTH < count){
                from + Constants.PAGINATION_LENGTH
            }else if (from < count && from + Constants.PAGINATION_LENGTH > count){
                count
            }else{
                -1
            }
            return if(to > -1) {
                userInfo.likedRecipesIds?.subList(from, to)
            }else{
                null
            }
        }
    }
}