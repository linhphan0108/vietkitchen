package com.example.linh.vietkitchen.ui.custom.likeButton


import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet

import com.example.linh.vietkitchen.ui.custom.BaseCustomView
import timber.log.Timber


class AndroidCircleView : BaseCustomView {

    /* These are color used to make circle  */
    private var colorStart = -0xa8de
    private var colorEnd = -0x3ef9

    /*These variables are used as name implies */
    private val argbEvaluator = ArgbEvaluator()
    private val circlePaint = Paint()
    private val maskPaint = Paint()

    /* These are temp bitmap and canvas for making circleView*/
    private lateinit var tempBitmap: Bitmap
    private lateinit var tempCanvas: Canvas
    /* This methdo is call when animation is executed and update some value of circle */

    var progress = 0f
        set(value) {
            field = value
            circlePaint.color = argbEvaluator.evaluate(value, colorStart, colorEnd) as Int
            invalidate()
        }

    /* There are two circle if you see in the animation so we are performing two animation this second circle animation method */

    var innerCircleRadiusProgress = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var maxCircleRadius: Int = 0
    private var center: PointF = PointF()

    /* IsActive is used to make sure that circleView should appear in animation and changeDimension for used
    * When there is changing in dimension which is done by @link AndroidLikeButton
     * */
    private var isActive = true

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

    /*
   *  Circle is fill and maskPaint using PorterDuff Mode CLEAR which make empty
   *   */
    init {
        circlePaint.style = Paint.Style.FILL
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }


    /* Here we are using changedimension for taking decision that until @link AndroidLikeButton is not change the
    * dimension of view then if make it's default value and if AndroidLikeButton change the dimension then by using
    * requestLayout we again call onMeasure for redesign the size of the view
     * */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Timber.d("measuredWidth = $measuredWidth - measureHeight = $measuredHeight")
    }

    // when this callback method call by android system the it initialize some values which is using in order the draw view
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        /* This is used fo initialize the value for drawing the componenet on surface */
        maxCircleRadius = w/2
        center.x = (w/2).toFloat()
        center.y = (h/2).toFloat()
        tempBitmap = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888)
        tempCanvas = Canvas(tempBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        tempCanvas.drawCircle(center.x, center.y, progress * maxCircleRadius, circlePaint)
        tempCanvas.drawCircle(center.x, center.y, this.innerCircleRadiusProgress * maxCircleRadius, maskPaint)
        if (isActive)
            canvas.drawBitmap(tempBitmap, 0f, 0f, null)
    }

    /* These two method for make color in circle */

    fun setColorStart(color: Int) {
        if (color != 0)
            this.colorStart = color
    }

    fun setColorEnd(color: Int) {
        if (color != 0)
            this.colorEnd = color
    }

    /* This method is call by the @link AndroidLikeButton */
    fun setWidthAndHeight(width: Int, height: Int) {
        if (layoutParams.width != width || layoutParams.height != height) {
            Timber.d("width = $width - height = $height")
            layoutParams.width = width
            layoutParams.height = height
            post{
                requestLayout()
            }
        }
    }

    /* This method is used to enable and disable the circle  */

    fun setIsActive(isActive: Boolean) {
        this.isActive = isActive
    }

    fun setAllColor(startColor: Int, endColor: Int) {
        this.colorStart = startColor
        this.colorEnd = endColor
    }
}


