package com.example.linh.vietkitchen.ui.screen.searchScreen

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Looper
import com.example.linh.vietkitchen.domain.command.RequestCategoryCommand
import com.example.linh.vietkitchen.domain.command.RequestRecipeCommand
import com.example.linh.vietkitchen.domain.command.RequestTagsCommand
import com.example.linh.vietkitchen.extension.removeAccent
import com.example.linh.vietkitchen.extension.removeLast
import com.example.linh.vietkitchen.extension.toListOfStringOfKey
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.mapper.TagMapper
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.LoadMoreItem
import com.example.linh.vietkitchen.ui.model.SearchItem
import com.example.linh.vietkitchen.util.TimberUtils
import kotlinx.coroutines.Job
import timber.log.Timber

class SearchScreenViewModel(application: Application,
                            private val requestRecipeCommand : RequestRecipeCommand = RequestRecipeCommand(),
                            private val recipeMapper: RecipeMapper = RecipeMapper(),
                            private val requestTagsCommand: RequestTagsCommand = RequestTagsCommand(),
                            private val tagMapper: TagMapper = TagMapper(),
                            private val requestCategoryCommand: RequestCategoryCommand = RequestCategoryCommand(),
                            private val categoryMapper: CategoryMapper = CategoryMapper())
    : BaseViewModel(application) {
    private var searchItem: SearchItem? = null
    private var lastRecipeId: String? = null
    private var hasReachLastRecord = false
    private var filterJob: Job? = null

    internal var requestRecipesStatus: MutableLiveData<Int> = MutableLiveData()
    internal var requestTagsStatus: MutableLiveData<Int> = MutableLiveData()
    internal var requestCategoriesStatus: MutableLiveData<Int> = MutableLiveData()

    private var listTags: List<SearchItem>? = null
    private var listCategories: List<SearchItem>? = null
    internal var filteredSuggestion: MutableLiveData<List<SearchItem>> = MutableLiveData()
    internal var listRecipes: MutableLiveData<MutableList<Entity>> = MutableLiveData()

    init {
        requestRecipesStatus.value = Status.NORMAL
        requestTagsStatus.value = Status.NORMAL
        requestCategoriesStatus.value = Status.NORMAL
    }

    fun refreshRecipes(item: SearchItem){
        hasReachLastRecord = false
        lastRecipeId = null
        searchRecipesBy(item)
    }

    fun searchMoreRecipes(item: SearchItem){
        if (!hasReachLastRecord){
//            addLoadMoreItem()
            requestRecipesStatus.value = Status.LOAD_MORE
            searchRecipesBy(item)
        }
    }

    private fun searchRecipesBy(item: SearchItem) {
        if (!hasReachLastRecord) {
            requestRecipesStatus.value = Status.LOADING
            fetchRecipeBySearchItem(item)
        }
    }

    private fun fetchRecipeBySearchItem(item: SearchItem) {
        this.searchItem = item
        Timber.d("search recipe by ${item.type}")
        launchDataLoad({
            val pagingResponse = withIoContext {
                when(item.type){
                    SearchItem.SearchItemType.TITLE -> {
                        requestRecipeCommand.title = item.query
                    }
                    SearchItem.SearchItemType.CATEGORY -> {
                        requestRecipeCommand.category = item.query
                    }
                    SearchItem.SearchItemType.TAG -> {
                        requestRecipeCommand.tag = item.query
                    }
                }
//                requestRecipeCommand.startAtId = lastRecipeId
                requestRecipeCommand.execute(getApplication())
            }

            val recipes = withComputationContext{
                TimberUtils.checkNotMainThread()
                pagingResponse.data?.let {data ->
                    recipeMapper.convertToUi(data)
                }
            }

            lastRecipeId = pagingResponse.lastId
            hasReachLastRecord = pagingResponse.isEnd
            requestRecipesStatus.value = Status.NORMAL

            if (recipes.isNullOrEmpty()) {
                requestRecipesStatus.value = Status.ERROR_EMPTY
            }else{
                if(requestRecipesStatus.value == Status.LOAD_MORE){
//                    removeLoadMoreItem()
                    listRecipes.value?.addAll(recipes)
                    listRecipes.value = listRecipes.value
                }else {
                    listRecipes.value = recipes.toMutableList()
                }
            }
        }, { e ->
            Timber.e(e)
            when{
                requestRecipesStatus.value == Status.LOAD_MORE -> {
//                    removeLoadMoreItem()
                    requestRecipesStatus.value = Status.LOAD_MORE_ERROR
                    listRecipes.value = listRecipes.value
                }
                else -> {
                    requestRecipesStatus.value = Status.ERROR
                }
            }
        }, false)
    }

    fun requestTags() {
        launchDataLoad({
            val tags = withIoContext {
                val map = requestTagsCommand.execute(getApplication())
                map.data!!.toListOfStringOfKey()
            }
            listTags = tagMapper.toSearchItem(tags)
        }, {
            requestTagsStatus.value = Status.ERROR
        }, false)
    }

    fun getListTags(): List<SearchItem> {
        return listTags ?: emptyList()
    }

    fun filterSearchSuggestions(query: String?){
        filterJob?.let {
            if (!it.isCancelled) it.cancel()
        }
        filterJob = launchDataLoad({
            filteredSuggestion.value = withComputationContext {
                TimberUtils.checkThread("filterSearchSuggestions")
                if(query.isNullOrBlank()
                        || (getListTags().isNullOrEmpty() && getListCategories().isNullOrEmpty())) {
                    return@withComputationContext emptyList<SearchItem>()
                }
                val filteredTags = getListTags().filter { item ->
                    item.query.toLowerCase().removeAccent().contains(query.toLowerCase().removeAccent())
                }

                val filteredCategories = getListCategories().filter { item ->
                    item.query.toLowerCase().removeAccent().contains(query.toLowerCase().removeAccent())
                }
                val result = mutableListOf<SearchItem>()
                result.addAll(filteredCategories)
                result.addAll(filteredTags)
                result.sortedBy {
                    it.query.length
                }
            }
        }, false)
    }

    fun requestCategory() {
        launchDataLoad({
            listCategories = withIoContext {
                Timber.d("on launchDataLoad: ${Looper.myLooper() == Looper.getMainLooper()}")
                val response = requestCategoryCommand.execute()
                response.data?.let { listCategories->
                    return@withIoContext categoryMapper.toSearchItem(listCategories)
                }
            }
        },{e->
            Timber.e(e)
            requestCategoriesStatus.value = Status.ERROR
        }, false)
    }

    private fun getListCategories(): List<SearchItem>{
        return listCategories ?: emptyList()
    }
}