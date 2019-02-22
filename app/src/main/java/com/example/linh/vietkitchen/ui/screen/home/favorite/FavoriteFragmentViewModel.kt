package com.example.linh.vietkitchen.ui.screen.home.favorite

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.linh.vietkitchen.domain.command.PutLikeCommand
import com.example.linh.vietkitchen.domain.command.PutUnlikeCommand
import com.example.linh.vietkitchen.domain.command.RequestLikedRecipesCommand
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeViewModel
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.util.Constants
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class FavoriteFragmentViewModel @Inject constructor(application: Application,
        private val recipeMapper: RecipeMapper,
        private val likedRecipesCommand: RequestLikedRecipesCommand,
        putLikeCommand: PutLikeCommand,
        putUnlikeCommand: PutUnlikeCommand)
    : BaseHomeViewModel(application, putLikeCommand, putUnlikeCommand){

    private val userInfo by lazy { VietKitchenApp.getUserInfo() }
    private val _requestLikeRecipesStatus: MediatorLiveData<Resource<List<Recipe>>> = MediatorLiveData()
    internal val requestLikeRecipesStatus: LiveData<Resource<List<Recipe>>> = _requestLikeRecipesStatus
    private var listRecipes: MutableList<Recipe> = mutableListOf()
    private var isLoadMoreRecipe = false
    private var isFreshRecipe = false
    private var hasReachLastRecord = false

    override fun refreshRecipes() {
        isFreshRecipe = true
        isLoadMoreRecipe = false
        hasReachLastRecord = false
        listRecipes.clear()
//        requestLikeRecipesStatus.value = StatusBox(Status.REFRESH, data = listRecipes)
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
//        requestLikeRecipesStatus.value = StatusBox(Status.LOAD_MORE)
        requestLikedRecipes(getNextPageLikedRecipesIds(from))
    }

    private fun requestLikedRecipes(ids: List<String>?) {
        if (ids.isNullOrEmpty()){
//            requestLikeRecipesStatus.value = StatusBox(Status.ERROR_EMPTY)
            return
        }

        likedRecipesCommand.ids = ids
        val liveData = likedRecipesCommand.execute(getApplication())
        _requestLikeRecipesStatus.addSource(liveData){resource ->
            val res = when(resource.status){
                com.example.linh.vietkitchen.vo.Status.SUCCESS -> {
                    val list = recipeMapper.convertToUi(resource.data!!, true)
                    listRecipes.addAll(list)
                    Resource.success(listRecipes.toList())
                }
                com.example.linh.vietkitchen.vo.Status.LOADING -> {
                    Resource.loading()
                }
                com.example.linh.vietkitchen.vo.Status.ERROR -> {
                    Resource.error(resource.message)
                }
            }
            _requestLikeRecipesStatus.value = res
            _requestLikeRecipesStatus.removeSource(liveData)
            isLoadMoreRecipe = false
            isFreshRecipe = false
        }
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