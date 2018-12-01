package com.example.linh.vietkitchen.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.GlideApp
import com.example.linh.vietkitchen.ui.GlideRequest

class GlideUtil {
    companion object {

        fun widthLoadingHolder(context: Context, imageView: ImageView, url: String,
                               listener: RequestListener<Drawable?>? = null): GlideRequest<Drawable> {
            return widthLoadingHolder(context, imageView, Uri.parse(url), listener)

        }

        fun widthLoadingHolder(context: Context, imageView: ImageView,uri: Uri, listener: RequestListener<Drawable?>? = null): GlideRequest<Drawable> {
            val scaleType = imageView.scaleType
            imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            return GlideApp.with(context)
                    .load(uri)
                    .thumbnail(GlideApp.with(context).load(R.drawable.ic_loading_gif)
                            .override(ScreenUtil.dp2px(72))
                            .centerInside()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .listener(object: RequestListener<Drawable?> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                            return listener?.onLoadFailed(e, model, target, isFirstResource)
                                    ?: false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            imageView.scaleType = scaleType
                            return listener?.onResourceReady(resource, model, target, dataSource, isFirstResource)
                                    ?: false
                        }
                    })
        }
    }
}