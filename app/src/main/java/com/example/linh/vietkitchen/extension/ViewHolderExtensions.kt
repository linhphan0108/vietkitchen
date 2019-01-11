package com.example.linh.vietkitchen.extension

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

val androidx.recyclerview.widget.RecyclerView.ViewHolder.ctx: Context
    get() = itemView.context
