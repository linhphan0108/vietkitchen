package com.example.linh.vietkitchen.ui.screen.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.domain.command.PutLikeCommand
import com.example.linh.vietkitchen.domain.command.PutUnlikeCommand
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.model.Recipe
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

abstract class BaseHomeViewModel(application: Application,
     private val putLikeCommand: PutLikeCommand = PutLikeCommand(),
     private val putUnlikeCommand: PutUnlikeCommand = PutUnlikeCommand()
): BaseViewModel(application) {

    internal val likeOrUnlikeAction: MutableLiveData<Recipe> = MutableLiveData()

    init {
        EventBus.getDefault().register(this)
    }

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }

    abstract fun refreshRecipes()
    abstract fun loadMoreRecipe()

    fun likeRecipe(recipe: Recipe) {
        launchDataLoad(ioBlock = {
            withIoContext {
                val recipeId = recipe.id!!
                putLikeCommand.uid = VietKitchenApp.getUserInfo().uid
                putLikeCommand.recipeId = recipeId
                putLikeCommand.execute(getApplication())
                recipe.hasLiked = true
                VietKitchenApp.addLikedRecipeId(recipeId)
                null
            }
            emitLikeOrUnlikeAction(recipe)
            Timber.d("on likeRecipe success")
        }, onError = {e->
            Timber.e(e)
        }, shouldShowProgress = false)

    }

    fun unLikeRecipe(recipe: Recipe) {
        launchDataLoad({
            withIoContext {
                val recipeId = recipe.id!!
                putUnlikeCommand.uid = VietKitchenApp.getUserInfo().uid
                putUnlikeCommand.recipeId = recipeId
                putUnlikeCommand.execute(getApplication())
                recipe.hasLiked = false
                VietKitchenApp.removeLikedRecipeId(recipeId)
                null
            }
            emitLikeOrUnlikeAction(recipe)
            Timber.d("on unLikeRecipe success")
        }, {
            Timber.e(it)
        }, shouldShowProgress = false)
    }

    internal fun emitLikeOrUnlikeAction(recipe: Recipe) {
        EventBus.getDefault().post(recipe)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    internal fun onMessageEventBus(event: Recipe) {
        likeOrUnlikeAction.value = event
    }
}