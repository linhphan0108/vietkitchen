package com.example.linh.vietkitchen.ui.screen.detailActivity

import com.example.linh.vietkitchen.domain.command.PutLikeCommand
import com.example.linh.vietkitchen.domain.command.PutUnlikeCommand
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
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
        likeCommand.uid = uid
        likeCommand.recipeId = id
        compositeDisposable.add(likeCommand.execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    viewContract?.onLikeChangedSuccess(true)
                }, {
                    viewContract?.onLikeChangedFailed()
                    Timber.e(it)
                }))
    }

    private fun putUnlike(uid: String, id: String){
        unlikeCommand.uid = uid
        unlikeCommand.recipeId = id
        compositeDisposable.add(unlikeCommand.execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    viewContract?.onLikeChangedSuccess(false)
                }, {
                    viewContract?.onLikeChangedFailed()
                    Timber.e(it)
                }))
    }
}