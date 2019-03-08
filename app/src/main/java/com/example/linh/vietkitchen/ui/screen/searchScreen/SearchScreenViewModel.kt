package com.example.linh.vietkitchen.ui.screen.searchScreen

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.domain.command.RequestRecipeCommand
import com.example.linh.vietkitchen.domain.command.RequestTagsCommand
import com.example.linh.vietkitchen.extension.removeAccent
import com.example.linh.vietkitchen.extension.toListOfStringOfKey
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.mapper.TagMapper
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.model.SearchItem
import com.example.linh.vietkitchen.util.TimberUtils
import com.example.linh.vietkitchen.util.transform
import com.example.linh.vietkitchen.vo.Resource
import com.example.linh.vietkitchen.vo.Status
import kotlinx.coroutines.Job
import timber.log.Timber
import javax.inject.Inject

class SearchScreenViewModel @Inject constructor(application: Application,
                            private val requestRecipeCommand : RequestRecipeCommand,
                            private val recipeMapper: RecipeMapper,
                            private val requestTagsCommand: RequestTagsCommand,
                            private val tagMapper: TagMapper,
                            private val categoryMapper: CategoryMapper)
    : BaseViewModel(application) {
    var searchItem: SearchItem? = null
    private var lastRecipeId: String? = null
    private var hasReachLastRecord = false
    private var filterJob: Job? = null

    private var _listTagsLiveData: MediatorLiveData<Resource<List<SearchItem>>> = MediatorLiveData()
    val listTagsLiveData: LiveData<Resource<List<SearchItem>>> = _listTagsLiveData
    var listTagLiveDataResponse: LiveData<Resource<List<SearchItem>>>? = null
    private var listTags: List<SearchItem>? = null
    private var listCategories: List<SearchItem>? = null

    private val _filteredSuggestion: MutableLiveData<List<SearchItem>> = MutableLiveData()
    val filteredSuggestion: LiveData<List<SearchItem>> = _filteredSuggestion

    private val _listRecipes: MediatorLiveData<Resource<List<Entity>>> = MediatorLiveData()
    private var liveDataResponse: LiveData<Resource<List<Recipe>>>? = null
    val listRecipes: LiveData<Resource<List<Entity>>> = _listRecipes

    init {
        requestTags()
    }

    fun refreshRecipes(item: SearchItem){
        hasReachLastRecord = false
        lastRecipeId = null
        searchRecipesBy(item)
    }

    fun searchMoreRecipes(item: SearchItem){
        if (!hasReachLastRecord){
//            addLoadMoreItem()
            searchRecipesBy(item)
        }
    }

    private fun searchRecipesBy(item: SearchItem) {
        if (!hasReachLastRecord) {
            fetchRecipeBySearchItem(item)
        }
    }

    private fun fetchRecipeBySearchItem(item: SearchItem) {
        if (item.query == searchItem?.query) return
        this.searchItem = item
        requestRecipeCommand.reset()
        Timber.d("search recipe by ${item.type}")
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
//        liveDataResponse?.let {
//            _listRecipes.removeSource(it)
//            liveDataResponse = null
//        }
        liveDataResponse = requestRecipeCommand.execute(getApplication())
                .transform { resource ->
                    when(resource.status){
                        com.example.linh.vietkitchen.vo.Status.SUCCESS -> {
                            val list = resource.data?.data?.let {
                                recipeMapper.convertToUi(it)
                            }
                            Resource.success(list)
                        }
                        com.example.linh.vietkitchen.vo.Status.LOADING -> {
                            Resource.loading(null)
                        }
                        com.example.linh.vietkitchen.vo.Status.ERROR -> {
                            Resource.error(resource.message, null)
                        }
                    }
                }
        _listRecipes.addSource(liveDataResponse!!) { resource ->
            _listRecipes.value = resource
        }

    }

    private fun requestTags() {
        listTagLiveDataResponse?.let { _listTagsLiveData.removeSource(it) }
        listTagLiveDataResponse = requestTagsCommand.execute(getApplication())
                .transform {resource ->
                    when(resource.status){
                        com.example.linh.vietkitchen.vo.Status.SUCCESS -> {
                            val tags = resource.data?.toListOfStringOfKey()
                            listTags = tags?.let { tagMapper.toSearchItem(it) }
                            Resource.success(listTags)
                        }
                        com.example.linh.vietkitchen.vo.Status.LOADING -> {
                            Resource.loading(null)
                        }
                        com.example.linh.vietkitchen.vo.Status.ERROR -> {
                            Resource.error(resource.message)
                        }
                    }
                }
        _listTagsLiveData.addSource(listTagLiveDataResponse!!){ resource ->
            _listTagsLiveData.value = resource
        }
    }

    private fun getListTags(): List<SearchItem> {
        return listTags ?: emptyList()
    }

    fun filterSearchSuggestions(query: String?){
        filterJob?.let {
            if (!it.isCancelled) it.cancel()
        }
        filterJob = launchDataLoad({
            _filteredSuggestion.value = withComputationContext {
                TimberUtils.checkThread("filterSearchSuggestions")
                if(query.isNullOrBlank()
                        || (getListTags().isNullOrEmpty() && getListCategories().isNullOrEmpty())) {
                    return@withComputationContext emptyList<SearchItem>()
                }
                val trimmedQuery = query.trim().toLowerCase().removeAccent()
                val filteredTags = getListTags().filter { item ->
                    item.query.toLowerCase().removeAccent().contains(trimmedQuery)
                }

                val filteredCategories = getListCategories().filter { item ->
                    item.query.toLowerCase().removeAccent().contains(trimmedQuery)
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

    fun onCategoryChanged(listCat: List<DrawerNavGroupItem>) {
        listCategories = categoryMapper.toSearchItemFromDrawerNav(listCat)
    }

    private fun getListCategories(): List<SearchItem>{
        return listCategories ?: emptyList()
    }
}