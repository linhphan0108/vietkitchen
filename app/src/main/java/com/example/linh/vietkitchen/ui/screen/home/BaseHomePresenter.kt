package com.example.linh.vietkitchen.ui.screen.home

import com.example.linh.vietkitchen.domain.command.PutLikeCommand
import com.example.linh.vietkitchen.domain.command.PutUnlikeCommand
import com.example.linh.vietkitchen.ui.VietKitchenApp.Companion.userInfo
import com.example.linh.vietkitchen.ui.screen.home.homeActivity.HomeActivity
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.Subject
import timber.log.Timber

abstract class BaseHomePresenter<T: BaseHomeContractView>(
        private val putLikeCommand: PutLikeCommand = PutLikeCommand(),
        private val putUnlikeCommand: PutUnlikeCommand = PutUnlikeCommand()
): BasePresenter<T>(), BaseHomeContractPresenter<T> {
    lateinit var likeOrUnLikePublisher: Subject<Recipe>

    override fun attachView(view: T) {
        super.attachView(view)
        attachSubscriberToHomeActivity()
    }

    private fun attachSubscriberToHomeActivity(){
        if (activity is HomeActivity){
            likeOrUnLikePublisher = (activity as HomeActivity).likeOrUnLikePublisher
            compositeDisposable.add(likeOrUnLikePublisher.subscribe({
                if (it.hasLiked){
                    viewContract?.onLikeEventObserve(it)
                }else{
                    viewContract?.onUnlikeEventObserve(it)
                }
            }, {
                Timber.e(it)
            }))
        }
    }

    override fun likeRecipe(recipe: Recipe) {
        val recipeId = recipe.id!!
        putLikeCommand.uid = userInfo.uid
        putLikeCommand.recipeId = recipeId
        compositeDisposable.add(putLikeCommand.execute()
                .doOnComplete{
                    recipe.hasLiked = true
                    userInfo.likedRecipesIds!!.add(recipeId)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    emitLike(recipe)
                    Timber.d("on likeRecipe success")
                }, {
                    Timber.e(it)
                }))
    }

    override fun unLikeRecipe(recipe: Recipe) {
        val recipeId = recipe.id!!
        putUnlikeCommand.uid = userInfo.uid
        putUnlikeCommand.recipeId = recipeId
        compositeDisposable.add(putUnlikeCommand.execute()
                .doOnComplete {
                    recipe.hasLiked = false
                    userInfo.likedRecipesIds!!.remove(recipeId)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    emitUnLike(recipe)
                    Timber.d("on unLikeRecipe success")
                }, {
                    Timber.e(it)
                }))
    }

    override fun emitLike(recipe: Recipe) {
        likeOrUnLikePublisher.onNext(recipe)
    }

    override fun emitUnLike(recipe: Recipe) {
        likeOrUnLikePublisher.onNext(recipe)
    }
}