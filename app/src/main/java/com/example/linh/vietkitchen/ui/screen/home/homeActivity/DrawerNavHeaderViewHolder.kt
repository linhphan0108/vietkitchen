package com.example.linh.vietkitchen.ui.home.homeActivity

import android.annotation.TargetApi
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.request.target.SimpleTarget
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.ctx
import com.example.linh.vietkitchen.ui.GlideApp
import android.os.Build
import com.bumptech.glide.request.transition.Transition


class DrawerNavHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    fun bindView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            GlideApp.with(ctx)
                    .load(R.drawable.img_drawer_nav_header)
                    .into(object : SimpleTarget<Drawable>() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            itemView.background = resource
                        }
                    })
        }else{
            itemView.setBackgroundResource(R.drawable.img_drawer_nav_header)
        }
    }
}