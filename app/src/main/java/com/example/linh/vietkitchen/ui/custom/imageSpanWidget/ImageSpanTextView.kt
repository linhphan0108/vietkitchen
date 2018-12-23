package com.example.linh.vietkitchen.ui.custom.imageSpanWidget

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.*
import android.text.Annotation
import android.text.style.AlignmentSpan
import android.util.AttributeSet
import android.widget.TextView
import com.example.linh.vietkitchen.R.anim.delay
import com.example.linh.vietkitchen.extension.attractAnnotationSpan
import timber.log.Timber


private const val TIME_BETWEEN_EACH_INVALIDATION = 16 // milliseconds

class ImageSpanTextView : TextView{
    private var lastTimeInvalidate = 0L //milliseconds
    private var hasQueueValidationCAll = false

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

    /**
     * @deprecated
     * for displaying images and more use {@link ImageSpanTextView#setTextAsSpannable} instead
     */
    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
    }

    override fun invalidate() {
        if(hasQueueValidationCAll) return
        val elapsedTime = System.currentTimeMillis() - lastTimeInvalidate
        if(elapsedTime >= TIME_BETWEEN_EACH_INVALIDATION){
            lastTimeInvalidate = System.currentTimeMillis()
            Timber.d("super.invalidate()")
            super.invalidate()
            lastTimeInvalidate = System.currentTimeMillis()
        }else{
            hasQueueValidationCAll = true
            postDelayed({
                super.invalidate()
                Timber.d("super.invalidate()")
                lastTimeInvalidate = System.currentTimeMillis()
                hasQueueValidationCAll = false
            }, TIME_BETWEEN_EACH_INVALIDATION.toLong())
        }
    }

    //========= inner methods ======================================================================
    fun setTextAsSpannable(text: CharSequence){
        setText(replaceSpan(text), TextView.BufferType.SPANNABLE)
    }

    private fun replaceSpan(txt: CharSequence?): CharSequence {
        if (txt.isNullOrBlank()) return ""
        val ssb = SpannableStringBuilder(txt.trim())
        ssb.attractAnnotationSpan()?.forEach { annotation ->
            when(annotation.key){
                "p" -> replaceAnnotationByAlignmentSpan(ssb, annotation)
                "src" -> ImageSpanUtil.replaceAnnotationByImageSpan(this, ssb, annotation)
            }
        }
        Timber.d(ssb.toString())
        return ssb
//        (Regex.fromLiteral("\\n\\n"), "\n")
//                .(Pattern.compile("[\\r\\n]+")), "\n")
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