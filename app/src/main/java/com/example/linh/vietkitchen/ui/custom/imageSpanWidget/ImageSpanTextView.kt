package com.example.linh.vietkitchen.ui.custom.imageSpanWidget

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import android.text.*
import android.util.AttributeSet
import android.widget.TextView
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.attractAnnotationSpan
import timber.log.Timber


private const val TIME_BETWEEN_EACH_INVALIDATION = 16 // milliseconds

class ImageSpanTextView : TextView{
    private var lastTimeInvalidate = 0L //milliseconds
    private var hasQueueValidationCAll = false
    private var spaceBetweenParagraph = 0
    private var spaceBetweenParagraphMultiplier = 1.1f

    override fun setTextSize(size: Float) {
        super.setTextSize(size)
        onTextSizeChanged()
    }

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
        val typedArray = context.theme.obtainStyledAttributes(attrs,
                R.styleable.ImageSpanTextView,0, 0)
        spaceBetweenParagraphMultiplier = typedArray.getFloat(R.styleable.ImageSpanTextView_paragraphSpace, spaceBetweenParagraphMultiplier)
        onTextSizeChanged()
        typedArray.recycle()
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
    private fun onTextSizeChanged(){
        spaceBetweenParagraph = (spaceBetweenParagraphMultiplier * textSize).toInt()
    }
    fun setTextAsSpannable(text: CharSequence){
        setText(replaceSpan(text), TextView.BufferType.SPANNABLE)
    }

    private fun replaceSpan(txt: CharSequence?): CharSequence {
        if (txt.isNullOrBlank()) return ""
        val ssb = SpannableStringBuilder(txt.trim())
        ssb.attractAnnotationSpan()?.forEach { annotation ->
            when(annotation.key){
                "p" -> SpannableUtil.replaceAnnotationByAlignmentSpan(ssb, annotation)
                AnnotationKey.STYLE.key -> SpannableUtil.replaceAnnotationByStyle(ssb, annotation)
                AnnotationKey.IMAGE.key -> SpannableUtil.replaceAnnotationByImageSpan(this, ssb, annotation)
                AnnotationKey.PARAGRAPH_SPACE.key -> SpannableUtil.replaceAnnotationByParagraphSpace(ssb, annotation, spaceBetweenParagraph)
            }
        }
        Timber.d(ssb.toString())
        return ssb
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