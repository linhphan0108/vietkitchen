package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.annotation.TargetApi
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.ctx
import com.example.linh.vietkitchen.ui.GlideApp
import com.example.linh.vietkitchen.ui.model.UserInfo
import kotlinx.android.synthetic.main.activity_home_nav_header.view.*


class DrawerNavHeaderViewHolder(itemView: View, private val userInfo: UserInfo) : RecyclerView.ViewHolder(itemView){
    fun bindView(){
        with(itemView){
            GlideApp.with(ctx)
                    .load(userInfo.avatarUrl)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .apply(RequestOptions.circleCropTransform())
                    .into(itemView.imgAvatar)
            itemView.txtDisplayName.text = userInfo.displayName
            itemView.txtEmail.text = userInfo.email
        }

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