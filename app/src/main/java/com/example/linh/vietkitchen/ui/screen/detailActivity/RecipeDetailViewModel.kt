package com.example.linh.vietkitchen.ui.screen.detailActivity

import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.content.Intent
import com.example.linh.vietkitchen.domain.command.PutLikeCommand
import com.example.linh.vietkitchen.domain.command.PutUnlikeCommand
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.model.UserInfo
import timber.log.Timber

class RecipeDetailViewModel(applicationContext: Application,
                            private val likeCommand: PutLikeCommand = PutLikeCommand(),
                            private val unlikeCommand: PutUnlikeCommand = PutUnlikeCommand())
    : BaseViewModel(applicationContext) {

    val recipe: MutableLiveData<Recipe> by lazy { MutableLiveData<Recipe>() }
    val likeState: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val userInfo: UserInfo by lazy { VietKitchenApp.userInfo }

    fun onCreate(intent: Intent){
        intent.getBundleExtra(EXTRA_BUNDLE).let {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                appBarLayout.transitionName = it.getString(BK_THUMB_IMAGE_TRANSITION_NAME)
//            }
            it.getParcelable<Recipe>(BK_RECIPE)?.let {re ->
                recipe.value = re
                likeState.value = re.hasLiked
            }
        }
    }

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