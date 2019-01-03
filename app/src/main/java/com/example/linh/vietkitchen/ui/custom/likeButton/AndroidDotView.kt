package com.example.linh.vietkitchen.ui.custom.likeButton

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import com.example.linh.vietkitchen.ui.custom.BaseCustomView

class AndroidDotView : BaseCustomView {
    companion object {
        /** DOTS_COUNT is used for number of dots will generate in circle so for that we choose 7 dots
            In order to create 7 Dots we need make an angle for each dot so after calculation 51 is angle for each dot
         **/
        private const val DOTS_COUNT = 7
        private const val OUTER_DOTS_POSITION_ANGLE = 51
        private const val NUMBER_COLOR_PAINT = 4
    }

    /* These two colors which appears in dots */
    private var color1 = -0x3ef9
    private var color2 = -0x6800

    /* These two variable are for dot radius and dot size
   * */
    private var maxOuterDotsRadius: Int = 0
    private var maxDotSize: Int = 0

    /* All these variable is used as there name implies */
    private var center: Point = Point()
    private val circlePaint = arrayOfNulls<Paint>(NUMBER_COLOR_PAINT)
    private var currentProgress = 0f
    private var currentRadius = 0f
    private var currentDotSize = 0f

    private var isActive = true

    private val argbEvaluator = ArgbEvaluator()


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onConstructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {

    }

    override fun getDesiredWidthSize(): Int {
        return AndroidLikeButton.DEFAULT_WIDTH
    }

    override fun getDesiredHeightSize(): Int {
        return AndroidLikeButton.DEFAULT_HEIGHT
    }

    // init the paint array;
    init{
        for (i in circlePaint.indices) {
            circlePaint[i] = Paint()
            circlePaint[i]?.style = Paint.Style.FILL
        }
    }

    /** This value is used when change dimension is not true  */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        center.x = width / 2
        center.y = height / 2
        maxOuterDotsRadius = width
        maxDotSize = getDotSize()
    }

    // I make a method used to disable the DotView so in order to do that i create a boolean variable isActive
    override fun onDraw(canvas: Canvas) {
        if (isActive) drawOuterDotsFrame(canvas)
    }

    /* This is math calculation used to find the dot size according for the width and height of the view*/

    private fun getDotSize(): Int {
        return (width * 0.05).toInt()
    }

    private fun drawOuterDotsFrame(canvas: Canvas) {
        for (i in 0 until DOTS_COUNT) {
            val cX = (center.x + currentRadius * Math.cos(i.toDouble() * OUTER_DOTS_POSITION_ANGLE.toDouble() * Math.PI / 180)).toInt()
            val cY = (center.y + currentRadius * Math.sin(i.toDouble() * OUTER_DOTS_POSITION_ANGLE.toDouble() * Math.PI / 180)).toInt()
            canvas.drawCircle(cX.toFloat(), cY.toFloat(), currentDotSize, circlePaint[i % NUMBER_COLOR_PAINT]!!)
        }
    }

    /* This method is call when animation is executed then it's increase the radius of dot's **/

    fun setCurrentProgress(value: Float) {
        currentProgress = value
        currentRadius = maxOuterDotsRadius * value
        currentDotSize = maxDotSize * value
        updateDotsPaints()
        updateDotsAlpha()
        invalidate()
    }

    /* Above two variable is declare for color so when animation is started then it slightly change color */
    private fun updateDotsPaints() {
        if (currentProgress < 0.5f) {
            val progress = currentProgress
            //float progress = (float) Utils.mapValueFromRangeToRange(currentProgress, 0f, 0.5f, 0, 1f);
            circlePaint[0]?.color = argbEvaluator.evaluate(progress, color1, color2) as Int
            circlePaint[1]?.color = argbEvaluator.evaluate(progress, color2, color1) as Int
            circlePaint[2]?.color = argbEvaluator.evaluate(progress, color1, color2) as Int
            circlePaint[3]?.color = argbEvaluator.evaluate(progress, color2, color1) as Int
        } else {
            val progress = Utils.mapValueFromRangeToRange(currentProgress.toDouble(), 0.5, 1.0, 0.0, 1.0).toFloat()
            circlePaint[0]?.color = argbEvaluator.evaluate(progress, color2, color1) as Int
            circlePaint[1]?.color = argbEvaluator.evaluate(progress, color1, color2) as Int
            circlePaint[2]?.color = argbEvaluator.evaluate(progress, color2, color1) as Int
            circlePaint[3]?.color = argbEvaluator.evaluate(progress, color1, color2) as Int
        }
    }

    /* There are two method for make fade effect in the end of animation */
    private fun setViewAlpha() {
        val progress: Float
        val alpha: Int
        if (currentProgress < 0.8f) {
            progress = Utils.clamp(currentProgress.toDouble(), 0.9, 1.0).toFloat()
            alpha = (progress * 255).toInt()
        } else {
            alpha = ((1 - currentProgress) * 255).toInt()
        }

        for (i in circlePaint.indices) {
            circlePaint[i]?.alpha = alpha
        }
        //circlePaint[0].setAlpha((int) 0.4);
    }

    /* This is second mehtod to fade out effect in the end of animation */

    private fun updateDotsAlpha() {
        val progress = Utils.clamp(currentProgress.toDouble(), 0.6, 1.0).toFloat()
        val alpha = Utils.mapValueFromRangeToRange(progress.toDouble(), 0.6, 1.0, 255.0, 0.0).toInt()
        circlePaint[0]?.alpha = alpha
        circlePaint[1]?.alpha = alpha
        circlePaint[2]?.alpha = alpha
        circlePaint[3]?.alpha = alpha
    }

    /* This method is used in @link AndroidLikeButton for change the width and height of DotView*/
    fun setWidthAndHeight(width: Int, height: Int) {
        layoutParams.width = width
        layoutParams.height = height
        post {
            requestLayout()
        }
    }


    fun setDotColor1(color: Int) {
        if (color != 0)
            this.color1 = color
    }

    fun setDotColor2(color: Int) {
        if (color != 0)
            this.color2 = color
    }

    //This method is used to make disable the AndroidDotView
    fun setDotActive(isActive: Boolean) {
        this.isActive = isActive
    }

    fun setAllDotColor(color1: Int, color2: Int) {
        this.color1 = color1
        this.color2 = color2

    }
}
