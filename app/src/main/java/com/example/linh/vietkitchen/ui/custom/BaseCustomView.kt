package com.example.linh.vietkitchen.ui.custom

import android.content.Context
import android.view.View
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import timber.log.Timber


abstract class BaseCustomView : View{
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

    protected abstract fun onConstructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Timber.v("Chart onMeasure w : ${MeasureSpec.toString(widthMeasureSpec)}")
        Timber.v("Chart onMeasure h: ${MeasureSpec.toString(heightMeasureSpec)}")
        val width = getMeasurementSize(widthMeasureSpec, getDesiredWidthSize())
        val height = getMeasurementSize(heightMeasureSpec, getDesiredHeightSize())
        setMeasuredDimension(width, height)
    }

    private fun getMeasurementSize(measureSpec: Int, defaultSize: Int): Int {
        val mode = View.MeasureSpec.getMode(measureSpec)
        val size = View.MeasureSpec.getSize(measureSpec)
        when (mode) {
        /**
         *  match_parent: the size will be equal parent's size
         *  exact pixels: specified size which is set
         */
            View.MeasureSpec.EXACTLY -> return size

        /**
         *wrap_content:  The size that gets passed could be much larger, taking up the rest of the space. So it might
        say, “I have 411 pixels. Tell me your size that doesn’t exceed 411 pixels.” The question then to the
        programmer is: What should I return?
         */
            View.MeasureSpec.AT_MOST -> return Math.min(defaultSize, size)

        /** Documentation says that this mode is passed in when the layout wants to
        know what the true size is. True size could be as big as it could be; layout will likely then scroll it.
        With that thought, we have returned the maximum size for our view.
         */
            View.MeasureSpec.UNSPECIFIED -> return defaultSize
            else -> return defaultSize
        }
    }

    protected abstract fun getDesiredWidthSize() : Int
    protected abstract fun getDesiredHeightSize() : Int
}