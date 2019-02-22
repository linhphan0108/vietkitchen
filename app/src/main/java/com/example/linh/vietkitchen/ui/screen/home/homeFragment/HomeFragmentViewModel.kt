package com.example.linh.vietkitchen.ui.screen.home.homeFragment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.example.linh.vietkitchen.domain.command.*
import com.example.linh.vietkitchen.extension.removeLast
import com.example.linh.vietkitchen.util.TimberUtils
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeViewModel
import com.example.linh.vietkitchen.util.RecipeUtil
import com.example.linh.vietkitchen.vo.Resource
import timber.log.Timber
import javax.inject.Inject


class HomeFragmentViewModel @Inject constructor(application: Application,
        private val recipeMapper: RecipeMapper,
        private val categoryMapper: CategoryMapper,
        private val requestRecipeCommand : RequestRecipeCommand,
        private val deleteRecipeCommand: DeleteRecipeCommand,
        private val deleteImagesCommand: DeleteImagesCommand,
        private val updateCategoriesCommand: UpdateCategoriesCommand,
        putLikeCommand: PutLikeCommand,
        putUnlikeCommand: PutUnlikeCommand)
    : BaseHomeViewModel(application, putLikeCommand, putUnlikeCommand), Observer<Resource<List<Recipe>>> {

    private var category: String? = null
    private var lastRecipeId: String? = null
    private var isLoadMoreRecipe = false
    private var isFreshRecipe = false
    private var hasReachLastRecord = false

    private val _deleteRecipeStatus: MutableLiveData<Resource<Int>> = MutableLiveData()
    val deleteRecipeStatus: LiveData<Resource<Int>> = _deleteRecipeStatus
    private val _listRecipeLiveData: MutableLiveData<Resource<List<Recipe>>> = MutableLiveData()
    private var liveDataPagingResponse: LiveData<Resource<List<Recipe>>>? = null
    val listRecipeLiveData: LiveData<Resource<List<Recipe>>> = _listRecipeLiveData
    private var listRecipes: MutableList<Recipe> = mutableListOf()

    private fun fetchRecipes() {
        requestRecipeCommand.category = category
        requestRecipeCommand.startAtId = lastRecipeId
        val liveData = requestRecipeCommand.execute(getApplication())
        liveDataPagingResponse?.removeObserver(this)
        liveDataPagingResponse = Transformations.map(liveData) { resource ->
            val result = when (resource.status) {
                com.example.linh.vietkitchen.vo.Status.SUCCESS -> {
                    val list = resource.data?.also { pagingResponse ->
                        lastRecipeId = pagingResponse.lastId
                        hasReachLastRecord = pagingResponse.isEnd
                    }?.data?.let {
                        listRecipes.addAll(recipeMapper.convertToUi(it))
                        listRecipes.toList()
                    } ?: listRecipes.toList()
                    Resource.success(list)
                }
                com.example.linh.vietkitchen.vo.Status.LOADING -> {
                    Resource.loading(null)
                }
                com.example.linh.vietkitchen.vo.Status.ERROR -> {
                    Resource.error(resource.message, null)
                }
            }
            isFreshRecipe = false
            isLoadMoreRecipe = false
            result
        }
        liveDataPagingResponse!!.observeForever(this)
//            if(isLoadMoreRecipe) {
//                removeLoadMoreItem()
//            }
    }

    override fun loadMoreRecipe() {
        if(isLoadMoreRecipe || isFreshRecipe || hasReachLastRecord) return
        isLoadMoreRecipe = true
//        requestRecipesStatus.value = StatusBox(Status.LOAD_MORE)
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
//        requestRecipesStatus.value = StatusBox(Status.REFRESH, data = listRecipes)
        fetchRecipes()
    }

    fun deleteRecipe(recipe: Recipe, adapterPosition: Int) {
        launchDataLoad({
            val wasDeleteImagesSuccess =  withIoContext {
                TimberUtils.checkNotMainThread()
                Transformations.map(deleteImages(recipe)){resource ->
                    resource.status == com.example.linh.vietkitchen.vo.Status.SUCCESS
                }}

            val wasUpdateCategorySuccess = withIoContext {
                withIoContext {
                    val updatedCat = updateCategory(recipe)
                    Transformations.map(updateCategoryToServer(updatedCat)){resource ->
                        if (resource.status == com.example.linh.vietkitchen.vo.Status.SUCCESS){
                            VietKitchenApp.setCategory(updatedCat)
                            true
                        }else{
                            false
                        }
                    }
                }
            }
            val wasDeleteRecipeSuccess = withIoContext {
                withIoContext{
                    Transformations.map(deleteRecipeInDb(recipe)){resource ->
                        resource.status == com.example.linh.vietkitchen.vo.Status.SUCCESS
                    }
                }
            }


            if (wasDeleteImagesSuccess.value!!) {Timber.d("deleted images successfully")}
            if(wasUpdateCategorySuccess.value!!) {Timber.d("updated category successfully")}
            if(wasDeleteRecipeSuccess.value!!) {Timber.d("deleted Recipe successfully")}
//            if (wasDeleteImagesSuccess && wasUpdateCategorySuccess && wasDeleteRecipeSuccess) {
            _deleteRecipeStatus.value = Resource.success(adapterPosition)
//            }
        },{e ->
            Timber.e(e)
            _deleteRecipeStatus.value = Resource.error(e.message)
        })
    }

    private fun deleteImages(recipe: Recipe): LiveData<Resource<Boolean>> {
        deleteImagesCommand.fileUrls = RecipeUtil.extractAllImagePaths(recipe)
        return deleteImagesCommand.execute(getApplication())
    }

    private fun updateCategoryToServer(cat: List<DrawerNavGroupItem>): LiveData<Resource<Boolean>> {
        updateCategoriesCommand.listCatGroup = categoryMapper.toDomain(cat)
        return updateCategoriesCommand.execute(getApplication())
    }

    private fun updateCategory(recipe: Recipe): List<DrawerNavGroupItem> {
        return VietKitchenApp.category.value?.let {cat ->
            val clonedCat = cat.map { it.clone() }
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
            clonedCat
        } ?: listOf()
    }

    private fun deleteRecipeInDb(recipe: Recipe): LiveData<Resource<Boolean>> {
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

    override fun onChanged(t: Resource<List<Recipe>>?) {
        _listRecipeLiveData.value = t
    }
}