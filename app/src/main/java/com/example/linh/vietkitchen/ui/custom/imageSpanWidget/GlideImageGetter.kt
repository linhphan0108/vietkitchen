package com.example.linh.vietkitchen.ui.custom.imageSpanWidget

import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.linh.vietkitchen.extension.ctx
import com.example.linh.vietkitchen.ui.GlideApp
import timber.log.Timber

class GlideImageGetter(private val textView: TextView,
                       private val w: Int, private val h: Int,
                       private val cornerRadius: Int = 0): Html.ImageGetter{
    private var holder: BitmapDrawablePlaceHolder = BitmapDrawablePlaceHolder(textView, w, h)

    override fun getDrawable(source: String?): Drawable {
        Timber.d("ImageGetter $source")
        GlideApp.with(textView.ctx)
                .asBitmap()
                .load(source)
                .fitCenter()
                .override(w, h)
                .transforms(RoundedCorners(cornerRadius))
                .into(holder.target)
        return holder
    }
}