package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.model.UserInfo


class DrawerNavHeaderViewHolder(itemView: View, private val userInfo: UserInfo) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){
    constructor(parent: ViewGroup, userInfo: UserInfo):
            this(LayoutInflater.from(parent.context).inflate(R.layout.activity_home_nav_header, parent, false)
                    , userInfo)

    fun bindView(){
    }
}