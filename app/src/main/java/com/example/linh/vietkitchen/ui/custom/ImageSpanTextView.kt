package com.example.linh.vietkitchen.ui.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.*
import android.text.Annotation
import android.text.style.AlignmentSpan
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.widget.TextView
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.ctx
import com.example.linh.vietkitchen.ui.GlideApp
import com.example.linh.vietkitchen.util.ScreenUtil
import timber.log.Timber

private val SCREEN_WIDTH = ScreenUtil.screenWidth()
//    private val IMAGE_HEIGH = (SCREEN_WIDTH * 3)/4
private val IMAGE_ROUNDED_CORNERS_RADIUS = ScreenUtil.dp2px(8)
private const val IMAGE_LOADING_WIDTH:Int = 64
private const val IMAGE_LOADING_HEIGHT:Int = 64

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
            attractAnnotationSpan(text as Spannable)?.forEach {
                when(it.key){
                    "p" -> replaceAnnotationByAlignmentSpan(ssb, it)
                    "src" -> replaceAnnotationByImageSpan(ssb, it)
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

    private fun attractAnnotationSpan(spannable: Spannable): Array<out Annotation>? {
        return spannable.getSpans(0, spannable.length, Annotation::class.java)
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

    private fun replaceAnnotationByImageSpan(ssb: SpannableStringBuilder, annotation: Annotation) {
        val url = annotation.value
        val id = resources.getIdentifier(url, null, ctx.packageName)
        val imageSpan = if (id != 0){//local drawable resource
            ImageSpan(ctx, id, ImageSpan.ALIGN_BASELINE)
        }else{//internet resource
            val w = SCREEN_WIDTH - ScreenUtil.dp2px(ctx, 64)//minus the parent's padding
            val h = (w * 3) / 4
            val cornerRadius = ScreenUtil.dp2px(8)
            val imageGetter = GlideImageGetter(this, w, h, cornerRadius)
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

    private fun generateHtml(textView: TextView, htmlString: String, w: Int, h: Int): Spannable {
        val imageGetter = GlideImageGetter(textView, w, h, IMAGE_ROUNDED_CORNERS_RADIUS)
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
        } else {
            Html.fromHtml(htmlString, imageGetter, null) as Spannable
        }
    }
}

//inner classes ====================================================================================
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

class BitmapDrawablePlaceHolder(val textView: TextView,
                                private val w: Int,
                                private val h: Int) : BitmapDrawable(), Drawable.Callback {
    private var left: Int = (SCREEN_WIDTH - w ) /2
    private val top: Int = 0
    private val right = left + w
    private val bottom = top + h
    var gdLoading: GifDrawable? = null
    val context = textView.ctx
    init {
//        val resource = textView.ctx.resources
        GlideApp.with(context)
                .asGif()
                .load(R.drawable.ic_loading_gif)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .into(object : SimpleTarget<GifDrawable>(){
                    override fun onResourceReady(resource: GifDrawable, transition: Transition<in GifDrawable>?) {
                        val top = h/2 - IMAGE_LOADING_HEIGHT /2
                        val left = w/2 - IMAGE_LOADING_WIDTH /2
                        val right = left + IMAGE_LOADING_WIDTH
                        val bottom = top + IMAGE_LOADING_HEIGHT
                        gdLoading = resource
                        gdLoading!!.setBounds(left, top, right, bottom)
                        textView.invalidate()
                    }

                })
        setBounds(left, top, right, bottom)
    }

    var drawable: BitmapDrawable? = null
        set(value) {
            field = value
            value?.setBounds(0, 0, right, bottom)
        }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (drawable != null) {
            drawable!!.draw(canvas)
            if (gdLoading != null && gdLoading!!.isRunning){
                gdLoading!!.stop()
            }
        }else
            if (gdLoading != null){
                gdLoading!!.draw(canvas)
                if (!gdLoading!!.isRunning) {
                    gdLoading!!.callback = this@BitmapDrawablePlaceHolder
                    gdLoading!!.start()
                }
            }
    }

    val target = object: SimpleTarget<Bitmap>(w, h){
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            drawable = BitmapDrawable(textView.ctx.resources, resource)
        }
    }

    override fun unscheduleDrawable(who: Drawable?, what: Runnable?) {
        gdLoading?.recycle()
        textView.removeCallbacks(what)
    }

    override fun invalidateDrawable(who: Drawable?) {
        textView.invalidate()
    }

    override fun scheduleDrawable(who: Drawable?, what: Runnable?, `when`: Long) {
        textView.postDelayed(what, `when`)
    }
}

class Newline(private val mNumNewlines: Int)