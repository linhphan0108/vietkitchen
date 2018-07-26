package com.example.linh.vietkitchen.extension

import android.content.Context
import android.support.v7.widget.RecyclerView

val RecyclerView.ViewHolder.ctx: Context
    get() = itemView.context
