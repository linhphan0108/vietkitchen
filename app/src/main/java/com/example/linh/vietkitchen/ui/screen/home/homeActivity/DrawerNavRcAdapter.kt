package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.linh.vietkitchen.ui.VietKitchenApp.Companion.userInfo
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.DrawerNavHeader
import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import timber.log.Timber

class DrawerNavRcAdapter(private val recyclerView: RecyclerView,
                         private val childItemClickListener: OnItemClickListener? = null,
                         items: List<Entity> = listOf())
    : ListDelegationAdapter<MutableList<Entity>>(), OnGroupItemClickListener {
    init {
        delegatesManager.addDelegate(DrawerNavHeaderLayoutDelegate(userInfo))
        delegatesManager.addDelegate(DrawerNavGroupItemDelegate(this))
        delegatesManager.addDelegate(DrawerNavChildItemDelegate(childItemClickListener))
        setItems(items.toMutableList())
        addHeaderLayoutItem()
    }

    fun updateItemThenNotify(items: List<Entity>){
        this.items = items.toMutableList()
        addHeaderLayoutItem()
        notifyDataSetChanged()
    }

    private fun addHeaderLayoutItem(){
        this.items.add(0, DrawerNavHeader())
    }

    override fun onGroupItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int) {
        Timber.d("positions $layoutPosition - $adapterPosition")
        val groupItem = items[layoutPosition] as DrawerNavGroupItem
        if(groupItem.itemsList != null) {
            val childItemsSize: Int = groupItem.itemsList.size
            if (childItemsSize > 0) {
                //collapse list
                if (groupItem.isChildrenVisible) {
                    groupItem.isChildrenVisible = false
                    val startIndex = layoutPosition + 1
                    val lastIndex = startIndex + childItemsSize
                    for (i in startIndex until lastIndex) {
                        items.removeAt(startIndex)
                    }
                    notifyItemRangeRemoved(startIndex, childItemsSize)

                    //expand list
                } else {
                    groupItem.isChildrenVisible = true
                    val childItems = groupItem.itemsList
                    var index = 0
                    val startIndex = layoutPosition + 1
                    val lastIndex = startIndex + childItemsSize
                    for (i in startIndex until lastIndex) {
                        items.add(i, childItems[index])
                        index++
                    }
                    notifyItemRangeInserted(startIndex, childItemsSize)
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
            childItemClickListener?.onItemClick(itemView, layoutPosition, adapterPosition)
        }
    }
}