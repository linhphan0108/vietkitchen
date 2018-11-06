package com.example.linh.vietkitchen.admin.ui.screen.admin

import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.util.VerticalSpaceItemDecoration
import kotlinx.android.synthetic.admin.dialog_category_checker.*

private const val BK_CATEGORIES =  "BK_CATEGORIES"

class CategoryChecker : BottomSheetDialogFragment(), CategoryCheckerChildHolder.OnItemClickListener {
    companion object {
        fun newInstance(categories: List<DrawerNavGroupItem>) : CategoryChecker{
            val dialog = CategoryChecker()
            val bundle = Bundle()
            bundle.putParcelableArrayList(BK_CATEGORIES, ArrayList(categories))
            dialog.arguments = bundle
            return dialog
        }
    }

    private lateinit var adapter: CategoryCheckerAdapter
    private lateinit var listCatsChecked: MutableList<DrawerNavChildItem>
    lateinit var callback: OnDismissCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_category_checker, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let { bundle ->
            val cats = bundle.getParcelableArrayList<DrawerNavGroupItem>(BK_CATEGORIES)
            val flattenCats = mutableListOf<Entity>()
            cats.forEachIndexed { index, groupItem ->
                if(index != 0 ) { //ignore the first item (all)
                    groupItem.isChildrenVisible = true
                    flattenCats.add(groupItem)
                    groupItem.itemsList?.let { flattenCats.addAll(it) }
                }
            }
            setupRecyclerView(flattenCats)
            listCatsChecked = mutableListOf()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        callback.onDismiss(listCatsChecked)
        super.onDismiss(dialog)
    }

    private fun setupRecyclerView(categories: MutableList<Entity>) {
        adapter = CategoryCheckerAdapter(categories, this)
        rcvCategoryChecker.adapter = adapter
        rcvCategoryChecker.layoutManager = LinearLayoutManager(context)
        rcvCategoryChecker.addItemDecoration(VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.rcv_item_decoration_1dp), 0,
                resources.getDimensionPixelSize(R.dimen.rcv_item_decoration)))

    }

    override fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: DrawerNavChildItem, checked: Boolean) {
        if (checked){
            if (!listCatsChecked.contains(data)) {
                listCatsChecked.add(data)
            }
        }else {
            if (listCatsChecked.contains(data)) {
                listCatsChecked.remove(data)
            }
        }
    }

    interface OnDismissCallback{
        fun onDismiss(listCatsChecked: MutableList<DrawerNavChildItem>)
    }
}