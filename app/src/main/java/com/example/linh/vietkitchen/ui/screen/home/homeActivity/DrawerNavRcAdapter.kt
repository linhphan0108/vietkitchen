package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.linh.vietkitchen.extension.findIndex
import com.example.linh.vietkitchen.ui.VietKitchenApp.Companion.userInfo
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.DrawerNavHeader
import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import timber.log.Timber

const val ITEM_ALL_POSITION = 1//since the index-0 is the header layout
const val DEFAULT_SELECTED_POSITION = -1

class DrawerNavRcAdapter(private val recyclerView: RecyclerView,
                         private val childItemClickListener: OnItemClickListener? = null,
                         items: List<Entity> = listOf())
    : ListDelegationAdapter<MutableList<Entity>>(), OnGroupItemClickListener, OnItemClickListener {

    private var currentSelectedPosition = ITEM_ALL_POSITION
    /**
     * used to cache the selected child item which it's group was collapsed
     * the selected child item's state will be restored after it's group open
     */
    private var collapsedSelectedChildItem = DEFAULT_SELECTED_POSITION

    init {
        delegatesManager.addDelegate(DrawerNavHeaderLayoutDelegate(userInfo))
        delegatesManager.addDelegate(DrawerNavGroupItemDelegate(this))
        delegatesManager.addDelegate(DrawerNavChildItemDelegate(this))
        setItems(items.toMutableList())
        addHeaderLayoutItem()
    }

    fun updateItemThenNotify(items: List<Entity>){
        this.items = items.toMutableList()
        addHeaderLayoutItem()
        currentSelectedPosition = ITEM_ALL_POSITION
        if(this.items.isNotEmpty()) this.items[currentSelectedPosition].isSelected = true
        notifyDataSetChanged()
    }

    private fun addHeaderLayoutItem(){
        this.items.add(0, DrawerNavHeader())
    }

    override fun onGroupItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int) {
        Timber.d("positions $layoutPosition - $adapterPosition")
        val groupItem = items[layoutPosition] as DrawerNavGroupItem
        if(groupItem.itemsList != null) {
            //update current selected position
            val childItemsSize: Int = groupItem.itemsList.size
            if (childItemsSize > 0) {
                //collapse list
                if (groupItem.isChildrenVisible) {
                    onGroupCollapsed(adapterPosition, groupItem)
                    //expand list
                } else {
                    onGroupExpended(adapterPosition, groupItem)
                }

                //check if the clicked group item is half or fully invisible
                //if yes then scroll the list up
                val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val lastAddedChildPos = layoutPosition + childItemsSize
                if (lastAddedChildPos < itemCount) {
                    if (lastAddedChildPos > lastVisibleItemPosition) {
                        recyclerView.smoothScrollToPosition(lastAddedChildPos)
                    }
                }
            }
        }else{
            //the group item for request all recipes
            onItemClick(itemView, layoutPosition, adapterPosition)
        }
    }

    override fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: DrawerNavChildItem?) {
        Timber.d("onItemClick current position $currentSelectedPosition adapter position $adapterPosition")
        onSelectedItemChanged(currentSelectedPosition, adapterPosition)
        childItemClickListener?.onItemClick(itemView, layoutPosition, adapterPosition, data)
        collapsedSelectedChildItem = DEFAULT_SELECTED_POSITION
    }

    private fun onSelectedItemChanged(current: Int, newPosition: Int){
        if(current != newPosition) {
            onItemStateChanged(current, false)
            onItemStateChanged(newPosition, true)
            currentSelectedPosition = newPosition
        }
    }

    private fun onItemStateChanged(pos: Int, isSelected: Boolean){
        if (items[pos].isSelected == isSelected) return
        items[pos].isSelected = isSelected
        notifyItemChanged(pos, PayLoads.SELECTED_CHANGED)
    }

    private fun onGroupExpended(adapterPosition: Int, groupItem: DrawerNavGroupItem) {
        if(groupItem.itemsList.isNullOrEmpty()) return
        groupItem.isChildrenVisible = true
        val childItemsSize: Int = groupItem.itemsList.size
        val childItems = groupItem.itemsList
        val startIndex = adapterPosition + 1
        val lastIndex = startIndex + childItemsSize
        for ((index, i) in (startIndex until lastIndex).withIndex()) {
            items.add(i, childItems[index])
        }
        onGroupStatesChanged(groupItem.isChildrenVisible, adapterPosition,  groupItem.itemsList)
        notifyItemRangeInserted(startIndex, childItemsSize)
    }

    private fun onGroupCollapsed(adapterPosition: Int, groupItem: DrawerNavGroupItem) {
        if(groupItem.itemsList.isNullOrEmpty()) return
        groupItem.isChildrenVisible = false
        onGroupStatesChanged(groupItem.isChildrenVisible, adapterPosition,  groupItem.itemsList)
        val childItemsSize: Int = groupItem.itemsList.size
        val startIndex = adapterPosition + 1
        val lastIndex = startIndex + childItemsSize
        for (i in startIndex until lastIndex) {
            items.removeAt(startIndex)
        }
        notifyItemRangeRemoved(startIndex, childItemsSize)
    }

    private fun onGroupStatesChanged(isExpanded: Boolean, groupPos: Int, childItems: List<DrawerNavChildItem>){
        val span = getSpan(isExpanded, groupPos, childItems)
        if (isSelectedInsideGroup(isExpanded, groupPos, childItems.count())){
            collapsedSelectedChildItem = if (isExpanded) {
                DEFAULT_SELECTED_POSITION
            }else { -span }
            val newPosition = currentSelectedPosition + span
            onSelectedItemChanged(currentSelectedPosition, newPosition)

        }else if (isSelectedBellowGroup(isExpanded, groupPos, childItems.count())){
            currentSelectedPosition += span
        }
        Timber.d("current position $currentSelectedPosition")
    }

    private fun isSelectedInsideGroup(isExpanded: Boolean, groupPos: Int, childCount: Int): Boolean {
        return if (isExpanded){
            currentSelectedPosition == groupPos && collapsedSelectedChildItem != DEFAULT_SELECTED_POSITION
        }else {//collapsed
            currentSelectedPosition > groupPos
                    && currentSelectedPosition < (groupPos + childCount)
        }
    }

    private fun isSelectedBellowGroup(isExpanded: Boolean, groupPos: Int, childCount: Int): Boolean{
        return if (isExpanded){
            currentSelectedPosition > groupPos
        }else{
            currentSelectedPosition > groupPos + childCount
        }
    }

    /**
     * when a group is collapsed the selected item will be moved from it's child onto itself
     * the way will be reversed if that group is expanded
     * so we need to calculating the last selected child item was expanded or collapsed or not
     */
    private fun getSpan(isExpanded: Boolean, groupPos: Int, childItems: List<DrawerNavChildItem>): Int{
        var span = 0
        if (isSelectedInsideGroup(isExpanded, groupPos, childItems.count())){
            span = if (isExpanded){
                collapsedSelectedChildItem
            }else{
                -(childItems.findIndex { it.isSelected } + 1)
            }
        }else if (isSelectedBellowGroup(isExpanded, groupPos, childItems.count())){
            span = if (isExpanded) {
                childItems.count()
            }else{
                -childItems.count()
            }
        }
        Timber.d(" ${if(isExpanded) "expanded" else "collapsed"} - span $span")
        return span
    }
}

enum class PayLoads{
    SELECTED_CHANGED
}