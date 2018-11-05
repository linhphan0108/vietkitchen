package com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

abstract class EndlessScrollListener(
        // The minimum number of items to have below your current scroll position
        // before loading more.
        private val visibleThreshold: Int = 5,
        // Sets the starting page index
        private val startingPageIndex: Int = 0,
        // The current offset index of data you have loaded
        private var currentPage: Int = startingPageIndex
        ) : RecyclerView.OnScrollListener() {

    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0
    // True if we are still waiting for the last set of data to load.
    private var loading = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        val totalItemCount = recyclerView?.adapter?.itemCount ?: 0
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) { this.loading = true; }
        }
        // If it's still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false
            previousTotalItemCount = totalItemCount
            currentPage++
        }

        // If it isn't currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        var lastVisibleItem = 0
        val layoutManager = recyclerView?.layoutManager
        if (layoutManager is LinearLayoutManager){
            lastVisibleItem = layoutManager.findLastVisibleItemPosition()
        }
        if (!loading && (lastVisibleItem + visibleThreshold) >= totalItemCount ) {
            loading = onLoadMore(currentPage + 1, totalItemCount)
        }
    }

    fun onRefresh(){
        currentPage = 0
        previousTotalItemCount = 0
    }

    // Defines the process for actually loading more data based on page
    // Returns true if more data is being loaded; returns false if there is no more data to load.
    abstract fun onLoadMore(page: Int, totalItemsCount: Int): Boolean

}