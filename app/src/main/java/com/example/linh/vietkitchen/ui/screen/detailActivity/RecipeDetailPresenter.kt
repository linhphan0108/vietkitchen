package com.example.linh.vietkitchen.ui.screen.detailActivity

import com.example.linh.vietkitchen.domain.command.PutLikeCommand
import com.example.linh.vietkitchen.domain.command.PutUnlikeCommand
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import timber.log.Timber

class RecipeDetailPresenter(private val likeCommand: PutLikeCommand = PutLikeCommand(),
                            private val unlikeCommand: PutUnlikeCommand = PutUnlikeCommand())
    : BasePresenter<RecipeDetailViewContract>(), RecipeDetailPresenterContract {

    override fun onLikeChanged(uid: String, id: String, state: Boolean) {
        if (state){
            putLike(uid, id)
        }else{
            putUnlike(uid, id)
        }
    }

    private fun putLike(uid: String, id: String){
        launchDataLoad(ioBlock = {
            withIoContext {
                likeCommand.uid = uid
                likeCommand.recipeId = id
                likeCommand.executeOnTheInternet(context!!).data!!
            }
            viewContract?.onLikeChangedSuccess(true)
//          viewContract?.onLikeChangedFailed()
        }, onError = {e->
            Timber.d(e)
            viewContract?.onLikeChangedFailed()
        })
    }

    private fun putUnlike(uid: String, id: String){
        launchDataLoad(ioBlock = {
            val isSuccess = withIoContext {
                unlikeCommand.uid = uid
                unlikeCommand.recipeId = id
                unlikeCommand.executeOnTheInternet(context!!).data!!
            }
            if (isSuccess) {
                viewContract?.onLikeChangedSuccess(false)
            }else {
                viewContract?.onLikeChangedFailed()
            }
        }, onError = {e->
            Timber.e(e)
        })


    }
}