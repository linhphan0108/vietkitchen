package com.example.linh.vietkitchen.ui.custom.imageSpanWidget

import android.text.Annotation
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.widget.TextView
import com.example.linh.vietkitchen.extension.ctx
import com.example.linh.vietkitchen.util.ScreenUtil

object ImageSpanUtil{
    fun replaceAnnotationByImageSpan(textView: TextView, ssb: SpannableStringBuilder, annotation: Annotation) {
        val ctx = textView.ctx
        val url = annotation.value
        val id = ctx.resources.getIdentifier(url, null, ctx.packageName)
        val imageSpan = if (id != 0){//local drawable resource
            ImageSpan(ctx, id, ImageSpan.ALIGN_BASELINE)
        }else{//internet resource
            val cornerRadius = ScreenUtil.dp2px(8)
            val w = ScreenUtil.screenWidth() - ScreenUtil.dp2px(32)//minus the parent's padding
            val h = (w * 3) / 4
            val imageGetter = GlideImageGetter(textView, w, h, cornerRadius)
            ImageSpan(imageGetter.getDrawable(url), url, ImageSpan.ALIGN_BASELINE)
        }
        val start = ssb.getSpanStart(annotation)
        val end = ssb.getSpanEnd(annotation)
        val flag = ssb.getSpanFlags(annotation)
        ssb.removeSpan(annotation)
        ssb.setSpan(imageSpan, start, end, flag)
        ssb.insert(start, System.getProperty("line.separator"))
        ssb.insert(end+1, System.getProperty("line.separator"))
    }
}