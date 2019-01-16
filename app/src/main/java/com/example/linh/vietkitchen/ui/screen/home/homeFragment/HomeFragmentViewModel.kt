package com.example.linh.vietkitchen.ui.screen.home.homeFragment

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.command.DeleteImagesCommand
import com.example.linh.vietkitchen.domain.command.DeleteRecipeCommand
import com.example.linh.vietkitchen.domain.command.RequestRecipeCommand
import com.example.linh.vietkitchen.domain.command.UpdateCategoriesCommand
import com.example.linh.vietkitchen.extension.removeLast
import com.example.linh.vietkitchen.util.TimberUtils
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.baseMVVM.StatusBox
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.model.UserInfo
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeViewModel
import com.example.linh.vietkitchen.util.RecipeUtil
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe


class HomeFragmentViewModel(application: Application,
                            private val userInfo: UserInfo = VietKitchenApp.userInfo,
                            private val recipeMapper: RecipeMapper = RecipeMapper(),
                            private val categoryMapper: CategoryMapper = CategoryMapper(),
                            private val requestRecipeCommand : RequestRecipeCommand = RequestRecipeCommand(),
                            private val deleteRecipeCommand: DeleteRecipeCommand = DeleteRecipeCommand(),
                            private val deleteImagesCommand: DeleteImagesCommand = DeleteImagesCommand(),
                            private val updateCategoriesCommand: UpdateCategoriesCommand = UpdateCategoriesCommand())
    : BaseHomeViewModel(application) {

    private var category: String? = null
    private var lastRecipeId: String? = null
    private var isLoadMoreRecipe = false
    private var isFreshRecipe = false
    private var hasReachLastRecord = false

    lateinit var categories: List<DrawerNavGroupItem>
    internal var deleteRecipeStatus: MutableLiveData<StatusBox<Int>> = MutableLiveData()
    internal var requestRecipesStatus: MutableLiveData<StatusBox<List<Recipe>>> = MutableLiveData()
    private var listRecipes: MutableList<Recipe> = mutableListOf()

    private fun fetchRecipes(){
        launchDataLoad({
            val pagingResponse = withIoContext {
                requestRecipeCommand.category = category
                requestRecipeCommand.startAtId = lastRecipeId
                requestRecipeCommand.execute(getApplication())
            }

            val recipes = withComputationContext{
                TimberUtils.checkNotMainThread()
                pagingResponse.data?.let {data ->
                    recipeMapper.convertToUi(data)
                }
            } ?: listOf()

            if(isLoadMoreRecipe) {
                removeLoadMoreItem()
            }
            listRecipes.addAll(recipes)

            lastRecipeId = pagingResponse.lastId
            hasReachLastRecord = pagingResponse.isEnd
            isFreshRecipe = false
            isLoadMoreRecipe = false

            requestRecipesStatus.value = StatusBox(Status.SUCCESS, data = listRecipes.toList())
        }, { e ->
            Timber.e(e)
            when{
                isLoadMoreRecipe -> {
                    requestRecipesStatus.value = StatusBox(Status.LOAD_MORE_ERROR)
                    isLoadMoreRecipe = false
                }
                else -> {
                    requestRecipesStatus.value = StatusBox(Status.ERROR)
                    isFreshRecipe = false
                }
            }
        }, false)
    }

    override fun loadMoreRecipe() {
        if(isLoadMoreRecipe || isFreshRecipe || hasReachLastRecord) return
        isLoadMoreRecipe = true
        requestRecipesStatus.value = StatusBox(Status.LOAD_MORE)
        fetchRecipes()
    }

    override fun refreshRecipes() {
        this.refreshRecipesByCat()
    }

    fun refreshRecipesByCat(category: String? = null) {
        isFreshRecipe = true
        isLoadMoreRecipe = false
        hasReachLastRecord = false
        lastRecipeId = null
        listRecipes.clear()
        category?.let { this.category = it }
        requestRecipesStatus.value = StatusBox(Status.REFRESH, data = listRecipes)
        fetchRecipes()
    }

    fun deleteRecipe(recipe: Recipe, adapterPosition: Int) {
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
            deleteRecipeStatus.value = StatusBox(Status.SUCCESS, data = adapterPosition)
//            }
        },{e ->
            Timber.e(e)
            deleteRecipeStatus.value = StatusBox(Status.ERROR, e.message)
        })
    }

    private suspend fun deleteImages(recipe: Recipe): Response<Boolean> {
        deleteImagesCommand.fileUrls = RecipeUtil.extractAllImagePaths(recipe)
        return deleteImagesCommand.execute(getApplication())
    }

    private suspend fun updateCategoryToServer(cat: List<DrawerNavGroupItem>): Response<Boolean> {
        updateCategoriesCommand.listCatGroup = categoryMapper.toDomain(cat)
        return updateCategoriesCommand.execute(getApplication())
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
        return deleteRecipeCommand.execute(getApplication())
    }

    private fun removeLoadMoreItem(){
        if (listRecipes.size > 1) {
            listRecipes.apply {
                this.removeLast()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEventBus(event: List<DrawerNavGroupItem>) {
        this.categories = event
        Timber.d("onMessageEventBus(): DrawerNavGroupItem received")
    }
}