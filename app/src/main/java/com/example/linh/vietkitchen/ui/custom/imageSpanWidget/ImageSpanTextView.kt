package com.example.linh.vietkitchen.ui.custom.imageSpanWidget

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.*
import android.text.Annotation
import android.text.style.AlignmentSpan
import android.util.AttributeSet
import android.widget.TextView
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.attractAnnotationSpan
import com.example.linh.vietkitchen.extension.ctx

class ImageSpanTextView : TextView{
    constructor(context: Context): super(context) {
        this.onConstructor(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        this.onConstructor(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        this.onConstructor(context, attrs, defStyleAttr, 0)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes){
        this.onConstructor(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun onConstructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int){

    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (type == BufferType.SPANNABLE){
            val ssb = SpannableStringBuilder(text)
            text?.attractAnnotationSpan()?.forEach {
                when(it.key){
                    "p" -> replaceAnnotationByAlignmentSpan(ssb, it)
                    "src" -> ImageSpanUtil.replaceAnnotationByImageSpan(this, ssb, it)
                }
            }
            super.setText(ssb, type)
        }else {
            super.setText(text, type)
        }
    }

    fun setTextAsSpannable(text: String){
        val vv = ctx.resources.getText(R.string.context_annotation) as SpannedString
        val sp = SpannedString(text)
        setText(sp, BufferType.SPANNABLE)
    }

    private fun replaceAnnotationByAlignmentSpan(ssb: SpannableStringBuilder, annotation: Annotation){
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
        ssb.removeSpan(annotation)
        ssb.insert(end, System.getProperty("line.separator"))
        ssb.insert(end, System.getProperty("line.separator"))
        ssb.setSpan(alignmentSpan, start, end, flag)
    }

//    private fun generateHtml(textView: TextView, htmlString: String, w: Int, h: Int): Spannable {
//        val imageGetter = GlideImageGetter(textView, w, h, IMAGE_ROUNDED_CORNERS_RADIUS)
//        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
//        } else {
//            Html.fromHtml(htmlString, imageGetter, null) as Spannable
//        }
//    }
}

//inner classes ====================================================================================