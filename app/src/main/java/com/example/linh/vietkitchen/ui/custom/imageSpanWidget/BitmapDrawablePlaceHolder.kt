package com.example.linh.vietkitchen.ui.custom.imageSpanWidget

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.TextView
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.ctx
import com.example.linh.vietkitchen.ui.GlideApp
import com.example.linh.vietkitchen.util.ScreenUtil
import pl.droidsonroids.gif.GifDrawable
import timber.log.Timber
import java.lang.ref.WeakReference

private val IMAGE_LOADING_WIDTH:Int = ScreenUtil.dp2px(16)
private val IMAGE_LOADING_HEIGHT:Int = IMAGE_LOADING_WIDTH

class BitmapDrawablePlaceHolder : BitmapDrawable, Drawable.Callback {
    private lateinit var placeHolderBounds: Rect
    private var gdLoading: GifDrawable? = null
    private var drawable: BitmapDrawable? = null
    lateinit var target: SimpleTarget<Bitmap>
    private lateinit var contextRef: WeakReference<Context>
    private lateinit var textViewRef: WeakReference<TextView>

    constructor(textView: TextView, w: Int, h: Int){
        this.textViewRef = WeakReference(textView)
        this.contextRef = WeakReference(textView.ctx)
        val left = 0
        val top = 0
        val right = left + w
        val bottom = top + h
        placeHolderBounds = Rect(left, top, right, bottom)
        bounds = placeHolderBounds
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
        BitmapDrawablePlaceHolder(textView.resources, bitmap)
        loadLoadingGif(textView.ctx)
        target = object: SimpleTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val newBottom = (placeHolderBounds.width().toFloat() * resource.height / resource.width).toInt()
                placeHolderBounds.bottom = newBottom

                Timber.d("SimpleTarget image's width = ${resource.width}")
                Timber.d("SimpleTarget image's height = ${resource.height}")
                Timber.d("SplaceHolderBounds = $placeHolderBounds")

                drawable = BitmapDrawable(contextRef.get()?.resources, resource)
                drawable?.bounds = placeHolderBounds
                bounds = placeHolderBounds
                if (gdLoading != null){
                    if(gdLoading!!.isRunning) {
                        Timber.d("stop gif loading")
                        gdLoading!!.stop()
                    }
                    gdLoading!!.callback = null
                    gdLoading!!.recycle()
                }
                textViewRef.get()?.post {
                    textViewRef.get()?.let {txt ->
                        txt.setText(txt.text, TextView.BufferType.SPANNABLE)
                    }
                }
            }
        }
    }

    constructor(res: Resources, bitmap: Bitmap): super(res, bitmap)

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (drawable != null) {
            Timber.d("draw drawable")
            Timber.d("draw drawable bounds = $bounds")
            drawable?.draw(canvas)
        }else {
            Timber.d("draw loading-gif")
            gdLoading?.draw(canvas)
        }
    }

    override fun unscheduleDrawable(who: Drawable?, what: Runnable?) {
        Timber.d("unscheduleDrawable")
        textViewRef.get()?.removeCallbacks(what)
    }

    override fun invalidateDrawable(who: Drawable?) {
        Timber.d("gif invalidateDrawable")
        textViewRef.get()?.invalidate()
    }

    override fun scheduleDrawable(who: Drawable?, what: Runnable?, `when`: Long) {
        textViewRef.get()?.postDelayed(what, `when`)
    }

    private fun loadLoadingGif(ctx: Context) {
        Timber.d("loadLoadingGif")
        GlideApp.with(ctx)
                .`as`(ByteArray::class.java)
                .load(R.drawable.ic_loading_gif)
                .override(IMAGE_LOADING_WIDTH, IMAGE_LOADING_HEIGHT)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .into(object: SimpleTarget<ByteArray>(){
                    override fun onResourceReady(resource: ByteArray, transition: Transition<in ByteArray>?) {

                        val top = placeHolderBounds.centerY() - IMAGE_LOADING_HEIGHT / 2
                        val left = placeHolderBounds.centerX() - IMAGE_LOADING_WIDTH / 2
                        val right = left + IMAGE_LOADING_WIDTH
                        val bottom = top + IMAGE_LOADING_HEIGHT

                        gdLoading = GifDrawable(resource)
//                        val callback = MultiCallback(true)
//                        callback.addView(textViewRef.get())
                        gdLoading!!.setBounds(left, top, right, bottom)
                        gdLoading!!.callback = this@BitmapDrawablePlaceHolder
                        gdLoading!!.start()

                }})
//                .into(object : SimpleTarget<GifDrawable>() {
//                    override fun onResourceReady(resource: GifDrawable, transition: Transition<in GifDrawable>?) {
//                        Timber.d("loading-gif's intrinsicWidth = ${resource.intrinsicWidth}")
//                        Timber.d("loading-gif's intrinsicHeight = ${resource.intrinsicHeight}")
//                        val top = placeHolderBounds.height() / 2 - IMAGE_LOADING_HEIGHT / 2
//                        val left = placeHolderBounds.width() / 2 - IMAGE_LOADING_WIDTH / 2
//                        val right = left + IMAGE_LOADING_WIDTH
//                        val bottom = top + IMAGE_LOADING_HEIGHT
//                        resource.setBounds(left, top, right, bottom)
//                        resource.callback = this@BitmapDrawablePlaceHolder
//                        resource.setLoopCount(GifDrawable.LOOP_FOREVER)
//                        gdLoading = resource
//                        gdLoading?.start()
//                    }
//
//                })
    }
}