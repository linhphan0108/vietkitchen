package com.example.linh.vietkitchen.ui.custom.imageSpanWidget

import android.text.Annotation
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.widget.TextView
import com.example.linh.vietkitchen.extension.ctx
import com.example.linh.vietkitchen.util.ScreenUtil

private val SCREEN_WIDTH = ScreenUtil.screenWidth()
object ImageSpanUtil{
    fun replaceAnnotationByImageSpan(textView: TextView, ssb: SpannableStringBuilder, annotation: Annotation) {
        val ctx = textView.ctx
        val url = annotation.value
        val id = ctx.resources.getIdentifier(url, null, ctx.packageName)
        val imageSpan = if (id != 0){//local drawable resource
            ImageSpan(ctx, id, ImageSpan.ALIGN_BASELINE)
        }else{//internet resource
            val w = SCREEN_WIDTH - ScreenUtil.dp2px(ctx, 64)//minus the parent's padding
            val h = (w * 3) / 4
            val cornerRadius = ScreenUtil.dp2px(8)
            val imageGetter = GlideImageGetter(textView, w, h, cornerRadius)
            ImageSpan(imageGetter.getDrawable(url), url)
        }
        val start = ssb.getSpanStart(annotation)
        val end = ssb.getSpanEnd(annotation)
        val flag = ssb.getSpanFlags(annotation)
        ssb.removeSpan(annotation)
        ssb.insert(end, System.getProperty("line.separator"))
        ssb.insert(end, System.getProperty("line.separator"))
        ssb.setSpan(imageSpan, start, end, flag)
//        ssb.insert(end, "\n\n")
    }
}