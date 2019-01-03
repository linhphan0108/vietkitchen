package com.example.linh.vietkitchen.ui.screen.home

import com.example.linh.vietkitchen.domain.command.PutLikeCommand
import com.example.linh.vietkitchen.domain.command.PutUnlikeCommand
import com.example.linh.vietkitchen.ui.VietKitchenApp.Companion.userInfo
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

abstract class BaseHomePresenter<T: BaseHomeContractView>(
        private val putLikeCommand: PutLikeCommand = PutLikeCommand(),
        private val putUnlikeCommand: PutUnlikeCommand = PutUnlikeCommand()
): BasePresenter<T>(), BaseHomeContractPresenter<T> {

    override fun attachView(view: T) {
        super.attachView(view)
        EventBus.getDefault().register(this)
    }

    override fun detachView() {
        EventBus.getDefault().unregister(this)
        super.detachView()
    }

    override fun likeRecipe(recipe: Recipe) {
        launchDataLoad(ioBlock = {
            withIoContext {
                val recipeId = recipe.id!!
                putLikeCommand.uid = userInfo.uid
                putLikeCommand.recipeId = recipeId
                putLikeCommand.executeOnTheInternet(context!!)
                recipe.hasLiked = true
                userInfo.likedRecipesIds!!.add(recipeId)
                null
            }
            emitLike(recipe)
            Timber.d("on likeRecipe success")
        }, onError = {e->
            Timber.e(e)
        }, shouldShowProgress = false)

    }

    override fun unLikeRecipe(recipe: Recipe) {
        launchDataLoad({
            withIoContext {
                val recipeId = recipe.id!!
                putUnlikeCommand.uid = userInfo.uid
                putUnlikeCommand.recipeId = recipeId
                putUnlikeCommand.executeOnTheInternet(context!!)
                recipe.hasLiked = false
                userInfo.likedRecipesIds!!.remove(recipeId)
                null
            }
            emitUnLike(recipe)
            Timber.d("on unLikeRecipe success")
        }, {
            Timber.e(it)
        }, shouldShowProgress = false)
    }

    override fun emitLike(recipe: Recipe) {
        EventBus.getDefault().post(recipe)
    }

    override fun emitUnLike(recipe: Recipe) {
        EventBus.getDefault().post(recipe)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEventBus(event: Recipe) {
        if (event.hasLiked){
            viewContract?.onLikeEventObserve(event)
        }else{
            viewContract?.onUnlikeEventObserve(event)
        }
    }
}