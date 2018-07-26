package com.example.linh.vietkitchen.ui.home

import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.graphics.Typeface
import android.widget.TextView
import android.view.LayoutInflater
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.ctx
import java.util.*


class DrawerNavAnimatedExpandableListViewAdapter : AnimatedExpandableListAdapter() {

    override fun getGroup(groupPosition: Int) = "header title"

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun hasStableIds() = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val headerTitle = getGroup(groupPosition)
        val viewGroup = convertView ?: LayoutInflater.from(parent!!.ctx).inflate(R.layout.item_header_drawer_nav, parent, false)
        val txtListHeader = viewGroup.findViewById(R.id.txtListHeader) as TextView
        txtListHeader.setTypeface(null, Typeface.BOLD)
        txtListHeader.text = headerTitle

        return viewGroup
    }

//    override fun getChildrenCount(groupPosition: Int) = Random().nextInt(8) + 2

    override fun getChild(groupPosition: Int, childPosition: Int) = "item child"

    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

//    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
//        val childText = getChild(groupPosition, childPosition)
//        val viewChild = convertView ?: LayoutInflater.from(parent?.ctx).inflate(R.layout.item_child_drawer_nav, parent, false)
//        val txtListChild = viewChild as TextView
//        txtListChild.text = childText
//        return viewChild
//    }

    override fun getChildId(groupPosition: Int, childPosition: Int) = Random().nextLong()
    override fun getGroupCount() = 10

    override fun getRealChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val childText = getChild(groupPosition, childPosition)
        val viewChild = convertView ?: LayoutInflater.from(parent?.ctx).inflate(R.layout.item_child_drawer_nav, parent, false)
        val txtListChild = viewChild as TextView
        txtListChild.text = childText
        return viewChild
    }

    override fun getRealChildrenCount(groupPosition: Int) = 8
}