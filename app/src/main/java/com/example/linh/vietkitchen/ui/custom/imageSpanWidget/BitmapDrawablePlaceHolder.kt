package com.example.linh.vietkitchen.ui.custom.imageSpanWidget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.TextView
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.ctx
import com.example.linh.vietkitchen.ui.GlideApp
import com.example.linh.vietkitchen.util.ScreenUtil

private const val IMAGE_LOADING_WIDTH:Int = 64
private const val IMAGE_LOADING_HEIGHT:Int = 64

class BitmapDrawablePlaceHolder(val textView: TextView,
                                private val w: Int,
                                private val h: Int) : BitmapDrawable(), Drawable.Callback {
    private var left: Int = ((ScreenUtil.screenWidth() - w) * .5).toInt()
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