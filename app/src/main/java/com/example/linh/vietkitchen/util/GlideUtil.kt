package com.example.linh.vietkitchen.util

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DecodeFormat
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.GlideApp
import com.example.linh.vietkitchen.ui.GlideRequest

class GlideUtil {
    companion object {
        fun widthLoadingHolder(context: Context, url: String): GlideRequest<Drawable> {
            return GlideApp.with(context)
                    .load(url)
                    .thumbnail(GlideApp.with(context).load(R.drawable.ic_loading_gif)
                            .override(ScreenUtil.dp2px(100))
                            .centerInside()
                            .format(DecodeFormat.PREFER_ARGB_8888))

        }
    }
}