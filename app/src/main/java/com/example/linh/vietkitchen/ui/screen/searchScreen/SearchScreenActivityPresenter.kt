package com.example.linh.vietkitchen.ui.screen.searchScreen

import android.os.Looper
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.domain.command.RequestCategoryCommand
import com.example.linh.vietkitchen.domain.command.RequestRecipeCommand
import com.example.linh.vietkitchen.domain.command.RequestTagsCommand
import com.example.linh.vietkitchen.extension.removeAccent
import com.example.linh.vietkitchen.extension.toListOfStringOfKey
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.mapper.TagMapper
import com.example.linh.vietkitchen.ui.model.SearchItem
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import com.example.linh.vietkitchen.util.TimberUtils
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class SearchScreenActivityPresenter(private val requestRecipeCommand : RequestRecipeCommand = RequestRecipeCommand(),
                                    private val recipeMapper: RecipeMapper = RecipeMapper(),
                                    private val requestTagsCommand: RequestTagsCommand = RequestTagsCommand(),
                                    private val tagMapper: TagMapper = TagMapper(),
                                    private val requestCategoryCommand: RequestCategoryCommand = RequestCategoryCommand(),
                                    private val categoryMapper: CategoryMapper = CategoryMapper())
    : BasePresenter<SearchContractView>(), SearchScreenContractPresenter {
    private var searchItem: SearchItem? = null
    private var lastRecipeId: String? = null
    private var isLoadMoreRecipe = false
    private var isFreshRecipe = false
    private var hasReachLastRecord = false

    private var listTags: List<SearchItem>? = null
    private var listCategories: List<SearchItem>? = null

    override fun searchRecipesBy(item: SearchItem) {
        if (isLoadMoreRecipe){
            viewContract?.onStartLoadMore()
        }else {
            viewContract?.onRefreshRecipe()
        }
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
                requestRecipeCommand.executeOnTheInternet(context!!)
            }

            val listRecipes = withComputationContext{
                TimberUtils.checkNotMainThread()
                pagingResponse.data?.let {data ->
                    recipeMapper.convertToUi(data)
                }
            }
            if (listRecipes.isNullOrEmpty()) {
                viewContract?.onRecipesRequestFailed("Oops! something went wrong, no data found")
            }else{
                if(isLoadMoreRecipe){
                    viewContract?.onLoadMoreSuccess(listRecipes)

                }else {
                    viewContract?.onRecipesRequestSuccess(listRecipes)

                }
            }
            lastRecipeId = pagingResponse.lastId
            hasReachLastRecord = pagingResponse.isEnd
            isFreshRecipe = false
            isLoadMoreRecipe = false
            if(hasReachLastRecord) viewContract?.onLoadMoreReachEndRecord()

        }, { e ->
            Timber.e(e)
            when{
                isLoadMoreRecipe -> {
                    viewContract?.onLoadMoreFailed()
                    isLoadMoreRecipe = false
                }
                else -> {
                    viewContract?.onRecipesRequestFailed("Opps some things went wrong. ${e.message}")
                    isFreshRecipe = false
                }
            }
        }, false)
    }

    override fun requestTags() {
        launchDataLoad({
            val tags = withIoContext {
                val map = requestTagsCommand.executeOnTheInternet(context!!)
                map.data!!.toListOfStringOfKey()
            }
            listTags = tagMapper.toSearchItem(tags)
            viewContract?.onGetTagsSuccess(listTags!!)
        }, {
            viewContract?.onGetTagsFailed(it.message)
        }, false)
    }

    override fun getListTags(): List<SearchItem> {
        return listTags ?: emptyList()
    }

    override fun filterSearchSuggestions(query: String?){
        launchDataLoad({
            val filteredTags = withIoContext {
                if(query.isNullOrBlank()
                        || (getListTags().isNullOrEmpty() && getListCategories().isNullOrEmpty())) {
                    return@withIoContext emptyList<SearchItem>()
                }
                val filteredTags = getListTags().filter { item ->
                    item.query.toLowerCase().removeAccent().contains(query.toLowerCase().removeAccent())
                }

                val filteredCategories = getListCategories().filter { item ->
                    item.query.toLowerCase().removeAccent().contains(query.toLowerCase().removeAccent())
                }
                val result = mutableListOf<SearchItem>()
                result.addAll(filteredTags)
                result.addAll(filteredCategories)
                result.toList()
            }
            viewContract?.onFilterListTag(filteredTags)
        }, false)
    }

    override fun requestCategory() {
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
            viewContract?.onRequestCategoriesFailed(getStringRes(R.string.message_error))
        }, false)
    }

    private fun getListCategories(): List<SearchItem>{
        return listCategories ?: emptyList()
    }
}