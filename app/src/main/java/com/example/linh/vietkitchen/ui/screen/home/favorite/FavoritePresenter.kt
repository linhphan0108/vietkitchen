package com.example.linh.vietkitchen.ui.screen.home.favorite

import com.example.linh.vietkitchen.domain.command.RequestLikedRecipesCommand
import com.example.linh.vietkitchen.ui.screen.home.BaseHomePresenter
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import timber.log.Timber

class FavoritePresenter(private val recipeMapper: RecipeMapper = RecipeMapper(),
                        private val likedRecipesCommand: RequestLikedRecipesCommand
                        = RequestLikedRecipesCommand())
    : BaseHomePresenter<FavoriteContractView>(), FavoriteContractPresenter {

    override fun requestLikedRecipes(ids: List<String>?) {
        launchDataLoad(ioBlock = {
            val likedRecipes = withIoContext {
                likedRecipesCommand.ids = ids
                val response = likedRecipesCommand.executeOnTheInternet(context!!)
                recipeMapper.convertToUi(response.data!!, true)
            }

            if (likedRecipes.isNullOrEmpty()){
                viewContract?.onRequestLikedRecipesNoData()
            }else{
                viewContract?.onRequestLikedRecipesSuccess(likedRecipes)
            }
        }, onError = {
            viewContract?.onRequestLikedRecipesFailed()
            Timber.e(it)
        })

    }

    override fun requestLikedRecipes(uid: String) {
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