package com.example.linh.vietkitchen.ui.screen.home.favorite

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.domain.command.RequestLikedRecipesCommand
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.baseMVVM.StatusBox
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeViewModel
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.model.Recipe
import timber.log.Timber

class FavoriteFragmentViewModel(application: Application,
        private val recipeMapper: RecipeMapper = RecipeMapper(),
        private val likedRecipesCommand: RequestLikedRecipesCommand = RequestLikedRecipesCommand())
    : BaseHomeViewModel(application){

    val requestLikeRecipesStatus: MutableLiveData<StatusBox<List<Recipe>>> = MutableLiveData()
    private var listRecipes: MutableList<Recipe> = mutableListOf()


    fun requestLikedRecipes(ids: List<String>?) {
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

            if (likedRecipes.isNullOrEmpty()){
                requestLikeRecipesStatus.value = StatusBox(Status.ERROR_EMPTY)
            }else{
                listRecipes.clear()
                listRecipes.addAll(likedRecipes)
                requestLikeRecipesStatus.value = StatusBox(Status.SUCCESS, data = listRecipes)
            }
        }, onError = {
            requestLikeRecipesStatus.value = StatusBox(Status.ERROR, it.message)
            Timber.e(it)
        })

    }

    fun requestLikedRecipes(uid: String) {
//        likedRecipesCommand.uid = uid
//        compositeDisposable.add(likedRecipesCommand.execute()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe ({
//                    viewContract?.onRequestLikedRecipesSuccess(it)
//                }, {
//                    viewContract?.onRequestLikedRecipesFailed()
//                    Timber.e(it)
//                }))
    }
}