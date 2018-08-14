package com.example.linh.vietkitchen.ui.screen.home.favorite

import com.example.linh.vietkitchen.domain.command.RequestLikedRecipesCommand
import com.example.linh.vietkitchen.exception.FirebaseNoDataException
import com.example.linh.vietkitchen.ui.screen.home.BaseHomePresenter
import com.example.linh.vietkitchen.ui.screen.home.mapper.RecipeMapper
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

class FavoritePresenter(private val recipeMapper: RecipeMapper = RecipeMapper(),
                        private val likedRecipesCommand: RequestLikedRecipesCommand
                        = RequestLikedRecipesCommand())
    : BaseHomePresenter<FavoriteContractView>(), FavoriteContractPresenter {

    override fun requestLikedRecipes(ids: List<String>?) {
        if (ids == null || ids.isEmpty()){
            viewContract?.onRequestLikedRecipesSuccess(listOf())
            return
        }
        likedRecipesCommand.ids = ids
        compositeDisposable.add(likedRecipesCommand.execute()
                .map {
                    recipeMapper.convertToUi(it, true)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    viewContract?.onRequestLikedRecipesSuccess(it)
                }, {
                    if (it is FirebaseNoDataException){
                        viewContract?.onRequestLikedRecipesNoData()
                    }else {
                        viewContract?.onRequestLikedRecipesFailed()
                    }
                    Timber.e(it)
                }))
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