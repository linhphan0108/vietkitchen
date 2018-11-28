package com.example.linh.vietkitchen.ui.screen.home.homeFragment

import com.example.linh.vietkitchen.domain.command.DeleteImagesCommand
import com.example.linh.vietkitchen.domain.command.DeleteRecipeCommand
import com.example.linh.vietkitchen.domain.command.RequestRecipeCommand
import com.example.linh.vietkitchen.domain.command.UpdateCategoriesCommand
import com.example.linh.vietkitchen.exception.FirebaseNoDataException
import com.example.linh.vietkitchen.extension.TimberUtils
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.model.UserInfo
import com.example.linh.vietkitchen.ui.screen.home.BaseHomePresenter
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.util.RecipeUtil
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe



class HomeFragmentPresenter(private val userInfo: UserInfo = VietKitchenApp.userInfo,
                            private val recipeMapper: RecipeMapper = RecipeMapper(),
                            private val categoryMapper: CategoryMapper = CategoryMapper(),
                            private val requestRecipeCommand : RequestRecipeCommand = RequestRecipeCommand(),
                            private val deleteRecipeCommand: DeleteRecipeCommand = DeleteRecipeCommand(),
                            private val deleteImagesCommand: DeleteImagesCommand = DeleteImagesCommand(),
                            private val updateCategoriesCommand: UpdateCategoriesCommand = UpdateCategoriesCommand())
    : BaseHomePresenter<HomeFragmentContractView>(), HomeFragmentContractPresenter {

    private var lastRecipeId: String? = null
    private var isLoadMoreRecipe = false
    private var isFreshRecipe = false
    private var isReachLastRecord = false

    lateinit var categories: List<DrawerNavGroupItem>

    private var requestRecipeDisposable: Disposable? = null

    override fun attachView(view: HomeFragmentContractView) {
        super.attachView(view)
        EventBus.getDefault().register(this)
    }

    override fun detachView() {
        super.detachView()
        EventBus.getDefault().unregister(this)
        requestRecipeDisposable?.dispose()
    }

    override fun requestFoods(category: String?){
        if (isLoadMoreRecipe){
            viewContract?.onLoadingMore()
        }else {
            viewContract?.onRefreshRecipe()
        }
        requestRecipeCommand.category = category
        requestRecipeCommand.startAtId = lastRecipeId
        requestRecipeDisposable = requestRecipeCommand.execute()
                .map {
                    recipeMapper.convertToUi(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    viewContract?.hideShimmer()
                    if (it != null && it.isEmpty()) {
                        viewContract?.onFoodsRequestFailed("Oops! something went wrong, no data found")
                    }else{
                        lastRecipeId = it.last().id
                        if(isLoadMoreRecipe){
                            viewContract?.onLoadMoreSuccess(it)
                            isLoadMoreRecipe = false
                        }else {
                            viewContract?.onFoodsRequestSuccess(it)
                            isFreshRecipe = false
                        }
                    }

                }, {
                    viewContract?.hideShimmer()
                    Timber.e(it)
                    when {
                        it is FirebaseNoDataException -> {
                            isReachLastRecord = true
                            isLoadMoreRecipe = false
                            isFreshRecipe = false
                            viewContract?.onLoadMoreReachEndRecord()
                        }
                        isLoadMoreRecipe -> {
                            viewContract?.onLoadMoreFailed()
                            isLoadMoreRecipe = false
                        }
                        else -> {
                            viewContract?.onFoodsRequestFailed("Opps some things went wrong. ${it.message}")
                            isFreshRecipe = false
                        }
                    }
                }, {
                    viewContract?.hideProgress()
                    Timber.d("on fetching recipes completed")
                })

    }

    override fun loadMoreRecipe() {
        if(isLoadMoreRecipe || isFreshRecipe || isReachLastRecord) return
        isLoadMoreRecipe = true
        requestFoods()
    }

    override fun refreshFoods(category: String?) {
        requestRecipeDisposable?.dispose()
        isFreshRecipe = true
        isLoadMoreRecipe = false
        lastRecipeId = null
        isReachLastRecord = false
        requestFoods(category)
    }

    override fun deleteRecipe(recipe: Recipe, adapterPosition: Int) {
        viewContract?.showProgress()
        val deleteImagesFlowable = Flowable.fromCallable {
            RecipeUtil.extractImagePaths(recipe)
        }.flatMap {
            deleteImagesCommand.fileUrls = it
            deleteImagesCommand.execute()
        }.subscribeOn(Schedulers.computation())

        val updateCategoryCompletable = Flowable.fromCallable {
            recipe.categories.forEach {checkedCat ->
                categories.forEach { groupCat ->
                    groupCat.itemsList?.forEach { childCat ->
                        if (checkedCat == childCat.itemTitle) childCat.numberItems--
                        if(childCat.numberItems < 0) childCat.numberItems = 0
                    }
                }
            }
            //decrease the all item
            categories.first().numberItems--
            return@fromCallable categoryMapper.toDomain(categories)
        }.flatMapCompletable {
            updateCategoriesCommand.listCatGroup = it
            updateCategoriesCommand.execute()
        }.subscribeOn(Schedulers.computation())

        val deleteRecipeFlowable = Flowable.fromCallable {
            recipeMapper.toDomain(recipe)
        }.flatMapCompletable {
            deleteRecipeCommand.recipe = it
            deleteRecipeCommand.execute()
        }.subscribeOn(Schedulers.computation())

        compositeDisposable.add(deleteImagesFlowable.toList().flatMapCompletable {
            TimberUtils.checkNotMainThread()
            Completable.concatArray(updateCategoryCompletable, deleteRecipeFlowable)
        }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewContract?.hideProgress()
                    viewContract?.onDeleteRecipeSuccess(adapterPosition)
                }, {e ->
                    viewContract?.hideProgress()
                    viewContract?.onDeleteRecipeFailed(e.message ?: "")
                    Timber.e(e)
                }))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEventBus(event: List<DrawerNavGroupItem>) {
        this.categories = event
        Timber.d("onMessageEventBus(): DrawerNavGroupItem received")
    }
}