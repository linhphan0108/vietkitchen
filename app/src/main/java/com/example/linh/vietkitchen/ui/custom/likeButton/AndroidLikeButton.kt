package com.example.linh.vietkitchen.ui.custom.likeButton

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView

import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.util.ScreenUtil
import kotlinx.android.synthetic.main.like_layout.view.*

import java.io.IOException

class AndroidLikeButton : FrameLayout, View.OnClickListener {

    companion object {
        var DEFAULT_WIDTH = ScreenUtil.dp2px(24)
        var DEFAULT_HEIGHT = ScreenUtil.dp2px(24)
    }

    /* isChecked is used for making decision that which imageView is set on imageView
    * animator set is used to run all animation as a set
     * */
    private var isChecked: Boolean = false
    private var animatorSet: AnimatorSet? = null
    private var dotColor1: Int = 0
    private var dotColor2: Int = 0
    private var circleStartColor: Int = 0
    private var circleEndColor: Int = 0

    /* starIcon and endIcon is reflection of like and unlike icon  */

    private var startIcon: Int = 0
    private var endIcon: Int = 0
    private var isLiked: Boolean = false

    /* These variable is used as thre name suggest  */

    private lateinit var likeBitmap: Bitmap
    private lateinit var unlikeBitmap: Bitmap
    private var dotActive: Boolean = false
    private var circleActive: Boolean = false

    /* This is instance of  interface for like  or unlike icon */
    private var onLikeEventListener: OnLikeEventListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    /* In this constructor i am getting all values from custom xml attributes */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.AndroidLikeButton)
        dotColor1 = array.getColor(R.styleable.AndroidLikeButton_dot_color_1, 0)
        dotColor2 = array.getColor(R.styleable.AndroidLikeButton_dot_color_2, 0)
        circleEndColor = array.getColor(R.styleable.AndroidLikeButton_circle_endColor, 0)
        circleStartColor = array.getColor(R.styleable.AndroidLikeButton_circle_startColor, 0)
        startIcon = array.getResourceId(R.styleable.AndroidLikeButton_like_icon, 0)
        endIcon = array.getResourceId(R.styleable.AndroidLikeButton_unlike_icon, 0)
        isLiked = array.getBoolean(R.styleable.AndroidLikeButton_liked, false)
        dotActive = array.getBoolean(R.styleable.AndroidLikeButton_dot_active, true)
        circleActive = array.getBoolean(R.styleable.AndroidLikeButton_circle_active, true)

        array.recycle()
        init()
    }

    /* This method used to make dimension and select whether the circle and dot view should be active or not
     * and also set there color and set image and getting there bitmap for further use.
      * */
    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.like_layout, this, true)
        circleView.setColorEnd(circleEndColor)
        circleView.setColorStart(circleStartColor)
        circleView.setIsActive(circleActive)
        dotView.setDotColor1(dotColor1)
        dotView.setDotColor2(dotColor2)
        dotView.setDotActive(dotActive)

        if (startIcon != 0 && endIcon != 0) {
            likeBitmap = getBitmapFromResId(startIcon)
            unlikeBitmap = getBitmapFromResId(endIcon)
            setLikeButtonImage()
        }

        setOnClickListener(this)
    }

    /* in onSizeChanged we set all view dimension dimension  */

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setImageView(w, h)
    }

    fun setOnLikeEventListener(onLikeEventListener: OnLikeEventListener) {
        this.onLikeEventListener = onLikeEventListener
    }

    /* This view extends the frameLayout and i set click listener in the init() method so when some one click on the
     * view then using this method we perform the animation in appropriate manner
      * */

    override fun onClick(view: View) {
        isChecked = !isChecked

        imgStar.setImageBitmap(if (isChecked) likeBitmap else unlikeBitmap)

        if (animatorSet != null) {
            animatorSet!!.cancel()
        }

        if (isChecked) {
            imgStar.animate().cancel()
            imgStar.scaleX = 0f
            imgStar.scaleY = 0f
            circleView.innerCircleRadiusProgress = 0f
            circleView.progress = 0f
            dotView.setCurrentProgress(0f)

            animatorSet = AnimatorSet()

            val outerCircleAnimator = ObjectAnimator.ofFloat(circleView, "progress", 0.1f, 1f)
            outerCircleAnimator.duration = 250
            outerCircleAnimator.interpolator = DecelerateInterpolator()


            val innerCircleAnimator = ObjectAnimator.ofFloat(circleView, "innerCircleRadiusProgress", 0.1f, 1f)
            innerCircleAnimator.duration = 200
            innerCircleAnimator.startDelay = 200
            innerCircleAnimator.interpolator = DecelerateInterpolator()

            val starScaleYAnimator = ObjectAnimator.ofFloat(imgStar, ImageView.SCALE_Y, 0.2f, 1f)
            starScaleYAnimator.duration = 350
            starScaleYAnimator.startDelay = 250
            starScaleYAnimator.interpolator = OvershootInterpolator()

            val starScaleXAnimator = ObjectAnimator.ofFloat(imgStar, ImageView.SCALE_X, 0.2f, 1f)
            starScaleXAnimator.duration = 350
            starScaleXAnimator.startDelay = 250
            starScaleXAnimator.interpolator = OvershootInterpolator()


            val dotsAnimator = ObjectAnimator.ofFloat(dotView, "currentProgress", 0.1f, 1f)

            dotsAnimator.duration = 900
            dotsAnimator.startDelay = 50
            dotsAnimator.interpolator = AccelerateDecelerateInterpolator()

            animatorSet!!.playTogether(
                    outerCircleAnimator,
                    innerCircleAnimator,
                    starScaleYAnimator,
                    starScaleXAnimator,
                    dotsAnimator
            )


            animatorSet!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    circleView.innerCircleRadiusProgress = 0f
                    circleView.progress = 0f
                    dotView.setCurrentProgress(0f)
                    imgStar.scaleX = 1f
                    imgStar.scaleY = 1f
                }
            })

            animatorSet!!.start()

            onLikeEventListener?.onLikeClicked(this)
        } else {
            onLikeEventListener?.onUnlikeClicked(this)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                imgStar.animate().cancel()
                imgStar.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).interpolator = DecelerateInterpolator()
                isPressed = true
            }

            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                val isInside = x > 0 && x < width && y > 0 && y < height
                if (isPressed != isInside) {
                    isPressed = isInside
                }
            }

            MotionEvent.ACTION_UP -> {
                imgStar.animate().cancel()
                imgStar.animate().scaleX(1f).scaleY(1f).setDuration(150).interpolator = DecelerateInterpolator()
                if (isPressed) {
                    performClick()
                    isPressed = false
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                imgStar.animate().cancel()
                imgStar.animate().scaleX(1f).scaleY(1f).setDuration(150).interpolator = DecelerateInterpolator()
                isPressed = false
            }
        }
        return true
    }

    /* These are some setter method  */

    fun setDotColor(startColor: Int, endColor: Int) {
        this.dotColor1 = startColor
        this.dotColor2 = endColor
        dotView.setAllDotColor(startColor, endColor)
    }

    fun setCircleColor(startColor: Int, endColor: Int) {
        this.circleStartColor = startColor
        this.circleEndColor = endColor
        circleView.setAllColor(startColor, endColor)
    }

    fun setDotActive(isActive: Boolean) {
        this.dotActive = isActive
        dotView.setDotActive(dotActive)
    }

    fun setCircleActive(isActive: Boolean) {
        this.circleActive = isActive
        circleView.setIsActive(circleActive)
    }

    private fun setImageView(width: Int, height: Int) {
        val starWidth = (width * 0.5).toInt()
        val startHeight = (height * 0.5).toInt()
        if (imgStar.layoutParams.width != starWidth || imgStar.layoutParams.height != startHeight) {
            imgStar.layoutParams.width = starWidth
            imgStar.layoutParams.height = startHeight
        }

        val circleWidth = (width * 0.6).toInt()
        val circleHeight = (height * 0.6).toInt()
        circleView.setWidthAndHeight(circleWidth, circleHeight)

        if (dotView.layoutParams.width != width || dotView.layoutParams.height != height) {
            dotView.setWidthAndHeight(width, height)
        }
    }

    fun setLikeIcon(resId: Int) {
        likeBitmap = getBitmapFromResId(resId)
        setLikeButtonImage()
    }

    fun setUnlikeIcon(resId: Int) {

        unlikeBitmap = getBitmapFromResId(resId)
        setLikeButtonImage()
    }

    fun setLikeIcon(bitmap: Bitmap) {

        likeBitmap = bitmap
        setLikeButtonImage()
    }

    fun setUnlikeIcon(bitmap: Bitmap) {

        unlikeBitmap = bitmap
        setLikeButtonImage()
    }

    fun setLikeIcon(uri: Uri) {
        getImageFromUri(uri)?.let {likeBitmap = it}
        setLikeButtonImage()
    }

    fun setUnlikeIcon(uri: Uri) {
        getImageFromUri(uri)?.let { unlikeBitmap = it }
        setLikeButtonImage()
    }

    private fun getBitmapFromResId(resId: Int): Bitmap {
        return BitmapFactory.decodeResource(context!!.resources, resId)
    }

    private fun setLikeButtonImage() {
        isChecked = isLiked
        if (isLiked) {
            imgStar.setImageBitmap(likeBitmap)
        } else {
            imgStar.setImageBitmap(unlikeBitmap)
        }
    }

    fun setCurrentlyLiked(isLiked: Boolean) {
        this.isLiked = isLiked
        this.isChecked = isLiked
        setLikeButtonImage()
    }


    private fun getDrawableToBitmap(drawable: Drawable?): Bitmap? {

        //return if drawable is null that means it doen't have a bitmap
        if (drawable == null) {
            return null
        }
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, bitmap.width, bitmap.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun getImageFromUri(uri: Uri): Bitmap? {
        var temp: Bitmap? = null
        try {
            temp = MediaStore.Images.Media.getBitmap(context!!.contentResolver, uri)
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

        return temp

    }

    interface OnLikeEventListener {
        fun onLikeClicked(androidLikeButton: AndroidLikeButton)
        fun onUnlikeClicked(androidLikeButton: AndroidLikeButton)
    }
}
