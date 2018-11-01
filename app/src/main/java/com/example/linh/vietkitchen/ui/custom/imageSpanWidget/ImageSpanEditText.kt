package com.example.linh.vietkitchen.ui.custom.imageSpanWidget

import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.Annotation
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.widget.EditText

class ImageSpanEditText : EditText{
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

    fun insertImageSpan(uri: Uri, position: Int){
        val holder = "&#2228;"
        val endOffset = position + holder.length
        val ssb = SpannableStringBuilder(editableText)
        ssb.append(holder)
        val annotation = Annotation("src", uri.toString())
        ssb.setSpan(annotation, position, endOffset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ImageSpanUtil.replaceAnnotationByImageSpan(this, ssb, annotation)
        setText(ssb, BufferType.SPANNABLE)
    }
}