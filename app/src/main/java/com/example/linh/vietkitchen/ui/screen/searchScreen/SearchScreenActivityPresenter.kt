package com.example.linh.vietkitchen.ui.screen.searchScreen

import com.example.linh.vietkitchen.domain.command.RequestRecipeCommand
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.model.SearchItem
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import com.example.linh.vietkitchen.util.TimberUtils
import timber.log.Timber

class SearchScreenActivityPresenter(private val requestRecipeCommand : RequestRecipeCommand = RequestRecipeCommand(),
                                    private val recipeMapper: RecipeMapper = RecipeMapper())
    : BasePresenter<SearchContractView>(), SearchScreenContractPresenter {
    private var searchItem: SearchItem? = null
    private var lastRecipeId: String? = null
    private var isLoadMoreRecipe = false
    private var isFreshRecipe = false
    private var hasReachLastRecord = false

    override fun searchRecipesBy(item: SearchItem) {
        if (isLoadMoreRecipe){
            viewContract?.onStartLoadMore()
        }else {
            viewContract?.onRefreshRecipe()
        }
        this.searchItem = item
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

        }, { e ->
            Timber.e(e)
            when{
                isLoadMoreRecipe -> {
                    viewContract?.onLoadMoreFailed()
                    isLoadMoreRecipe = false
                }
                else -> {
                    viewContract?.onFoodsRequestFailed("Opps some things went wrong. ${e.message}")
                    isFreshRecipe = false
                }
            }
        }, false)
    }

    override fun requestRecipesByTag(tag: String){

    }
}