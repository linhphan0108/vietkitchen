package com.example.linh.vietkitchen.ui.screen.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.linh.vietkitchen.domain.command.PutLikeCommand
import com.example.linh.vietkitchen.domain.command.PutUnlikeCommand
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.vo.Status
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

abstract class BaseHomeViewModel(application: Application,
         private val putLikeCommand: PutLikeCommand,
         private val putUnlikeCommand: PutUnlikeCommand
): BaseViewModel(application) {

    private val _likeOrUnlikeAction: MediatorLiveData<Recipe> = MediatorLiveData()
    internal val likeOrUnlikeAction: LiveData<Recipe> = _likeOrUnlikeAction

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
        val recipeId = recipe.id!!
        putLikeCommand.uid = VietKitchenApp.getUserInfo().uid
        putLikeCommand.recipeId = recipeId
        val liveData = putLikeCommand.execute(getApplication())
        _likeOrUnlikeAction.addSource(liveData) {resource ->
            when(resource.status) {
                Status.SUCCESS -> {
                    recipe.hasLiked = true
                    VietKitchenApp.addLikedRecipeId(recipeId)
                    emitLikeOrUnlikeAction(recipe)
                    Timber.d("on likeRecipe success")
                }
                else ->{
                    Timber.d(resource.message)
                }
            }
            _likeOrUnlikeAction.removeSource(liveData)
        }

    }

    fun unLikeRecipe(recipe: Recipe) {
        val recipeId = recipe.id!!
        putUnlikeCommand.uid = VietKitchenApp.getUserInfo().uid
        putUnlikeCommand.recipeId = recipeId
        val liveData = putUnlikeCommand.execute(getApplication())
        _likeOrUnlikeAction.addSource(liveData){resource ->
            when(resource.status) {
                Status.SUCCESS -> {
                    recipe.hasLiked = false
                    VietKitchenApp.removeLikedRecipeId(recipeId)
                    emitLikeOrUnlikeAction(recipe)
                    Timber.d("on unLikeRecipe success")
                }
                else -> {
                    Timber.d(resource.message)
                }
            }
            _likeOrUnlikeAction.removeSource(liveData)
        }
    }

    internal fun emitLikeOrUnlikeAction(recipe: Recipe) {
        EventBus.getDefault().post(recipe)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    internal fun onMessageEventBus(event: Recipe) {
        _likeOrUnlikeAction.value = event
    }
}