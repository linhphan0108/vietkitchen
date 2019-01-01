package com.example.linh.vietkitchen.ui.custom.imageSpanWidget

import android.graphics.Typeface
import android.text.Annotation
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.style.AlignmentSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.widget.TextView
import com.example.linh.vietkitchen.util.ScreenUtil

object SpannableUtil{
    fun replaceAnnotationByImageSpan(textView: TextView, ssb: SpannableStringBuilder, annotation: Annotation) {
        val ctx = textView.context
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

    fun replaceAnnotationByAlignmentSpan(ssb: SpannableStringBuilder, annotation: Annotation){
        val alignment = when (annotation.value){
            "start" -> Layout.Alignment.ALIGN_NORMAL
            "center" -> Layout.Alignment.ALIGN_CENTER
            "end" -> Layout.Alignment.ALIGN_OPPOSITE
            else -> Layout.Alignment.ALIGN_NORMAL
        }
        val start = ssb.getSpanStart(annotation)
        val end = ssb.getSpanEnd(annotation)
        val flag = ssb.getSpanFlags(annotation)
        val alignmentSpan = AlignmentSpan.Standard(alignment)
        ssb.insert(end, System.getProperty("line.separator"))
        ssb.setSpan(alignmentSpan, start, end, flag)
    }

    fun replaceAnnotationByStyle(ssb: SpannableStringBuilder, annotation: Annotation){
        val start = ssb.getSpanStart(annotation)
        val end = ssb.getSpanEnd(annotation)
        val flag = ssb.getSpanFlags(annotation)
        val style = when(annotation.value.toLowerCase()){
            Style.BOLD.name.toLowerCase() -> Typeface.BOLD
            Style.BOLD_ITALIC.name.toLowerCase() -> Typeface.BOLD_ITALIC
            Style.ITALIC.name.toLowerCase() -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }
        val span = StyleSpan(style)
        ssb.setSpan(span, start, end, flag)
    }

    enum class Style{
        NORMAL {
            override fun toString(): String {
                return "normal"
            }
        }

        , BOLD {
            override fun toString(): String {
                return "bold"
            }
        }
        , ITALIC {
            override fun toString(): String {
                return "italic"
            }
        }, BOLD_ITALIC;

        override fun toString(): String {
            return "bold_italic"
        }}
}