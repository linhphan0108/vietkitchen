package com.example.linh.vietkitchen.ui.screen.home.homeFragment

import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.command.DeleteImagesCommand
import com.example.linh.vietkitchen.domain.command.DeleteRecipeCommand
import com.example.linh.vietkitchen.domain.command.RequestRecipeCommand
import com.example.linh.vietkitchen.domain.command.UpdateCategoriesCommand
import com.example.linh.vietkitchen.util.TimberUtils
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.model.UserInfo
import com.example.linh.vietkitchen.ui.screen.home.BaseHomePresenter
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.util.RecipeUtil
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
    private var hasReachLastRecord = false

    lateinit var categories: List<DrawerNavGroupItem>

    override fun requestFoods(category: String?){
        if (isLoadMoreRecipe){
            viewContract?.onStartLoadMore()
        }else {
            viewContract?.onRefreshRecipe()
        }

        launchDataLoad({
            val pagingResponse = withIoContext {
                requestRecipeCommand.category = category
                requestRecipeCommand.startAtId = lastRecipeId
                requestRecipeCommand.executeOnTheInternet(context!!)
            }

            val listRecipes = withComputationContext{
                TimberUtils.checkNotMainThread()
                pagingResponse.data?.let {data ->
                    recipeMapper.convertToUi(data)
                }
            }
            if (listRecipes.isNullOrEmpty()) {
                viewContract?.onFoodsRequestFailed("Oops! something went wrong, no data found")
            }else{
                if(isLoadMoreRecipe){
                    viewContract?.onLoadMoreSuccess(listRecipes)

                }else {
                    viewContract?.onFoodsRequestSuccess(listRecipes)

                }
            }
            lastRecipeId = pagingResponse.lastId
            hasReachLastRecord = pagingResponse.isEnd
            isFreshRecipe = false
            isLoadMoreRecipe = false
            if(hasReachLastRecord) viewContract?.onLoadMoreReachEndRecord()

        }, {
            when{
                isLoadMoreRecipe -> {
                    viewContract?.onLoadMoreFailed()
                    isLoadMoreRecipe = false
                }
                else -> {
                    viewContract?.onFoodsRequestFailed("Opps some things went wrong. ${it.message}")
                    isFreshRecipe = false
                }
            }
        }, false)
    }

    override fun loadMoreRecipe() {
        if(isLoadMoreRecipe || isFreshRecipe || hasReachLastRecord) return
        isLoadMoreRecipe = true
        requestFoods()
    }

    override fun refreshRecipes(category: String?) {
        isFreshRecipe = true
        isLoadMoreRecipe = false
        lastRecipeId = null
        hasReachLastRecord = false
        requestFoods(category)
    }

    override fun deleteRecipe(recipe: Recipe, adapterPosition: Int) {
        launchDataLoad({
            val wasDeleteImagesSuccess =  withIoContext {
                TimberUtils.checkNotMainThread()
                deleteImages(recipe).data!!}

            val wasUpdateCategorySuccess = withIoContext {
                withIoContext {
                    val updatedCat = updateCategory(recipe)
                    val isSuccess = updateCategoryToServer(updatedCat).data!!
                    if (isSuccess) EventBus.getDefault().post(updatedCat)
                    isSuccess
                }
            }
            val wasDeleteRecipeSuccess = withIoContext {
                withIoContext{deleteRecipeInDb(recipe).data!!} }


            if (wasDeleteImagesSuccess) {Timber.d("deleted images successfully")}
            if(wasUpdateCategorySuccess) {Timber.d("updated category successfully")}
            if(wasDeleteRecipeSuccess) {Timber.d("deleted Recipe successfully")}
//            if (wasDeleteImagesSuccess && wasUpdateCategorySuccess && wasDeleteRecipeSuccess) {
            viewContract?.onDeleteRecipeSuccess(adapterPosition)
//            }
        },{e ->
            Timber.e(e)
            viewContract?.onDeleteRecipeFailed(e.message ?: "")
        })
    }

    private suspend fun deleteImages(recipe: Recipe): Response<Boolean> {
        deleteImagesCommand.fileUrls = RecipeUtil.extractAllImagePaths(recipe)
        return deleteImagesCommand.executeOnTheInternet(context!!)
    }

    private suspend fun updateCategoryToServer(cat: List<DrawerNavGroupItem>): Response<Boolean> {
        updateCategoriesCommand.listCatGroup = categoryMapper.toDomain(cat)
        return updateCategoriesCommand.executeOnTheInternet(context!!)
    }

    private suspend fun updateCategory(recipe: Recipe): List<DrawerNavGroupItem> {
        val clonedCat = categories.map { it.clone() }
        recipe.categories.forEach {checkedCat ->
            clonedCat.forEach { groupCat ->
                var isContained = false
                groupCat.itemsList?.forEach { childCat ->
                    if (checkedCat == childCat.itemTitle) {
                        isContained = true
                        childCat.numberItems--
                        if (childCat.numberItems < 0) childCat.numberItems = 0
                    }
                }
                if(isContained) groupCat.numberItems--
            }
        }
        //decrease the item all
        clonedCat.first().numberItems--
        return clonedCat
    }

    private suspend fun deleteRecipeInDb(recipe: Recipe): Response<Boolean> {
        deleteRecipeCommand.recipe = recipeMapper.toDomain(recipe)
        return deleteRecipeCommand.executeOnTheInternet(context!!)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEventBus(event: List<DrawerNavGroupItem>) {
        this.categories = event
        Timber.d("onMessageEventBus(): DrawerNavGroupItem received")
    }
}