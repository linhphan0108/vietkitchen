package com.example.linh.vietkitchen.ui.home.homeActivity

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.DrawerNavHeader
import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import timber.log.Timber

class DrawerNavRcAdapter(private val recyclerView: RecyclerView, items: List<Entity> = listOf())
    : ListDelegationAdapter<MutableList<Entity>>(), DrawerNavGroupViewHolder.OnItemClickListener {
    init {
        delegatesManager.addDelegate(DrawerNavHeaderLayoutDelegate())
        delegatesManager.addDelegate(DrawerNavGroupItemDelegate(this))
        delegatesManager.addDelegate(DrawerNavChildItemDelegate())
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
        val childItemsSize = groupItem.itemsList.size
        if (childItemsSize > 0){
            //collapse list
            if (groupItem.isChildrenVisible){
                groupItem.isChildrenVisible = false
                val startIndex = layoutPosition + 1
                val lastIndex = startIndex + childItemsSize
                for (i in startIndex until lastIndex) {
                    items.removeAt(startIndex)
                }
                notifyItemRangeRemoved(startIndex, childItemsSize)

            //expand list
            }else {
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
    }
}