package com.example.linh.vietkitchen.ui.screen.detailActivity

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.domain.command.PutLikeCommand
import com.example.linh.vietkitchen.domain.command.PutUnlikeCommand
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.model.UserInfo
import timber.log.Timber
import javax.inject.Inject

class RecipeDetailViewModel @Inject constructor(applicationContext: Application,
        private val likeCommand: PutLikeCommand,
        private val unlikeCommand: PutUnlikeCommand)
    : BaseViewModel(applicationContext) {

    val recipe: MutableLiveData<Recipe> by lazy { MutableLiveData<Recipe>() }
    val likeState: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val userInfo: UserInfo by lazy { VietKitchenApp.getUserInfo() }

    internal fun stateHasChanged(): Boolean{
        return recipe.value?.hasLiked != likeState.value
    }

    fun onLikeChanged() {
        val newState = !likeState.value!!
        val userId = userInfo.uid
        val recipeId = recipe.value!!.id!!
        if (newState){
            putLike(userId, recipeId)
        }else{
            putUnlike(userId, recipeId)
        }
    }

    private fun putLike(uid: String, id: String){
        launchDataLoad(ioBlock = {
            withIoContext {
                likeCommand.uid = uid
                likeCommand.recipeId = id
                likeCommand.execute(getApplication()).data!!
            }
            likeState.value = true
//            viewContract?.onLikeChangedSuccess(true)
//          viewContract?.onLikeChangedFailed()
        }, onError = {e->
            Timber.d(e)
//            viewContract?.onLikeChangedFailed()
        })
    }

    private fun putUnlike(uid: String, id: String){
        launchDataLoad(ioBlock = {
            val isSuccess = withIoContext {
                unlikeCommand.uid = uid
                unlikeCommand.recipeId = id
                unlikeCommand.execute(getApplication()).data!!
            }
            if (isSuccess) {
                likeState.value = false
//                viewContract?.onLikeChangedSuccess(false)
            }else {
//                viewContract?.onLikeChangedFailed()
            }
        }, onError = {e->
            Timber.e(e)
        })
    }
}