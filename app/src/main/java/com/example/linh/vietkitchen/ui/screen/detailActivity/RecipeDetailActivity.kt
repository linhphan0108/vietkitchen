package com.example.linh.vietkitchen.ui.screen.detailActivity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.extension.capWords
import com.example.linh.vietkitchen.ui.GlideApp
import com.example.linh.vietkitchen.ui.mvpBase.BaseActivity
import kotlinx.android.synthetic.main.activity_detail.*
import android.text.Html
import android.text.Spannable
import android.view.Gravity
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.linh.vietkitchen.extension.ctx
import com.example.linh.vietkitchen.util.ScreenUtil
import timber.log.Timber


private const val BK_THUMB_IMAGE_TRANSITION_NAME = "BK_THUMB_IMAGE_TRANSITION_NAME"
private const val EXTRA_BUNDLE = "EXTRA_BUNDLE"
private const val BK_RECIPE = "BK_RECIPE"
private val SCREEN_WIDTH = ScreenUtil.screenWidth()
//    private val IMAGE_HEIGH = (SCREEN_WIDTH * 3)/4
private val IMAGE_ROUNDED_CORNERS_RADIUS = ScreenUtil.dp2px(8)

class RecipeDetailActivity : BaseActivity<RecipeDetailViewContract, RecipeDetailPresenter>(),
        RecipeDetailViewContract, Animation.AnimationListener {
    companion object {
        fun createIntent(context: Context?, thumbImageTransitionName: String, recipe: Recipe): Intent{
            val intent = Intent(context, RecipeDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putString(BK_THUMB_IMAGE_TRANSITION_NAME, thumbImageTransitionName)
            bundle.putParcelable(BK_RECIPE, recipe)
            intent.putExtra(EXTRA_BUNDLE, bundle)
            return intent
        }
    }

    lateinit var fadeInAnim: Animation
    lateinit var slideDownAnim:Animation
    lateinit var slideUpAnim:Animation
    lateinit var slideUpAnimOne:Animation
    lateinit var slideOutDown:Animation
    lateinit var slideOutDownOne:Animation
    lateinit var slideOutUp:Animation
    lateinit var fadeOutAnim:Animation

    private var enableBackBtn = false
    private var shouldHidePreProcessLayout = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.getBundleExtra(EXTRA_BUNDLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imgBlurBg.transitionName = bundle.getString(BK_THUMB_IMAGE_TRANSITION_NAME)
        }
        val recipe = bundle.getParcelable<Recipe>(BK_RECIPE)
//        initAnimationObjects()
        populateUI(recipe)
    }

//    override fun onBackPressed() {
//        if (enableBackBtn) {
//            llStepsToPreProcess.startAnimation(slideOutDown)
//            enableBackBtn = false
//        }
//    }

    //region MVP callbacks =========================================================================
    override val viewContext: Context?
        get() = this

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun initPresenter() = RecipeDetailPresenter()

    override fun getViewContract() = this

    override fun getActivityLayoutRes() = R.layout.activity_detail
    //endregion MVP callbacks

    //region Animation callbacks ===================================================================
    override fun onAnimationRepeat(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
        if (animation === fadeInAnim) {
            llDescriptionBox.visibility = View.VISIBLE
            llDescriptionBox.animation = slideDownAnim
        }
        if (animation === slideDownAnim) {
            llIngredientsBox.visibility = View.VISIBLE
            llIngredientsBox.animation = slideUpAnim
        }
        if (animation === slideUpAnim) {
            if (!shouldHidePreProcessLayout){
                llStepsToPreProcess.visibility = View.VISIBLE
                llStepsToPreProcess.animation = slideUpAnimOne
            }
            llStepsToProcess.visibility = View.VISIBLE
            llStepsToProcess.animation = slideUpAnimOne
//            imgResult.visibility = View.VISIBLE
//            imgResult.animation = slideUpAnimOne
            enableBackBtn = true
        }
        if (animation === slideOutDown) {
            llStepsToPreProcess.visibility = View.INVISIBLE
            llIngredientsBox.animation = slideOutDownOne
        }
        if (animation === slideOutDownOne) {
            llIngredientsBox.visibility = View.INVISIBLE
            llDescriptionBox.animation = slideOutUp
        }
        if (animation === slideOutUp) {
            llDescriptionBox.visibility = View.INVISIBLE
            bottomScrim.startAnimation(fadeOutAnim)
        }
        if (animation === fadeOutAnim) {
            bottomScrim.visibility = View.INVISIBLE
            super.onBackPressed()
        }
    }

    override fun onAnimationStart(animation: Animation?) {
    }
    //endregion animation callbacks

    //region inner methods =========================================================================
    private fun initAnimationObjects(){
        fadeInAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        fadeInAnim.duration = 1000
        fadeInAnim.setAnimationListener(this)

        slideDownAnim = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        slideDownAnim.duration = 1000
        slideDownAnim.setAnimationListener(this)

        slideUpAnim = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        slideUpAnim.duration = 1000
        slideUpAnim.setAnimationListener(this)

        slideUpAnimOne = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        slideUpAnimOne.duration = 500
        slideUpAnimOne.setAnimationListener(this)

        slideOutDown = AnimationUtils.loadAnimation(this, R.anim.slide_out_down)
        slideOutDown.setAnimationListener(this)

        slideOutDownOne = AnimationUtils.loadAnimation(this, R.anim.slide_out_down)
        slideOutDownOne.setAnimationListener(this)

        slideOutUp = AnimationUtils.loadAnimation(this, R.anim.slide_out_up)
        slideOutUp.setAnimationListener(this)

        fadeOutAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
        fadeOutAnim.duration = 500
        fadeOutAnim.setAnimationListener(this)


        bottomScrim.startAnimation(fadeInAnim)
    }

    private fun populateUI(recipe: Recipe) {
        GlideApp.with(this)
                .load(recipe.imageUrl)
                .listener(object : RequestListener<Drawable?>{
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                        thumbProgressbar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        thumbProgressbar.visibility = View.GONE
                        return false
                    }

                })
                .into(imgBlurBg)
//        GlideApp.with(this)
//                .load(recipe.imageUrl)
//                .override(SCREEN_WIDTH, SCREEN_WIDTH)
//                .into(imgResult)
        with(recipe) {
            val builder = StringBuilder()
            for ((key, value) in ingredient){
                builder.append(key)
                if (value.notes.isNullOrBlank()) builder.append("${value.notes}\n")
                builder.append("${value.quantity}${value.unit}\n")
            }

            TxtTitle.text = name.capWords()
            txtDescription.text = intro
            txtIngredients.text = builder.substring(0, builder.length - 1)
            txtSpices.text = spice

            val w = SCREEN_WIDTH - ScreenUtil.dp2px(this@RecipeDetailActivity, 64)//minus the parent's padding
            val h = (w * 3) / 4

            if (preliminaryProcessing.isBlank()) {
                llStepsToPreProcess.visibility = View.GONE
                shouldHidePreProcessLayout = true
            }else{
                val imageGetterPre = ImageGetter(txtStepsToPreProcess, w, h, IMAGE_ROUNDED_CORNERS_RADIUS)
                txtStepsToPreProcess.text  = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Html.fromHtml(preliminaryProcessing, Html.FROM_HTML_MODE_LEGACY, imageGetterPre, null) as Spannable
                } else {

                    Html.fromHtml(preliminaryProcessing, imageGetterPre, null) as Spannable
                }
            }

            val imageGetter = ImageGetter(txtStepsToProcess, w, h, IMAGE_ROUNDED_CORNERS_RADIUS)
            txtStepsToProcess.text = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(processing, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
            } else {

                Html.fromHtml(processing, imageGetter, null) as Spannable
            }
        }
    }
    //endregion inner methods
}

class ImageGetter(private val textView: TextView, private val w: Int, private val h: Int, val cornerRadius: Int = 0)
    : Html.ImageGetter{
    override fun getDrawable(source: String?): Drawable {
        val holder = BitmapDrawablePlaceHolder(textView, w, h)
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

class BitmapDrawablePlaceHolder(val textView: TextView, private val w: Int,
                                private val h: Int) : BitmapDrawable() {
    private var left: Int = (SCREEN_WIDTH - w ) /2

    init {
        setBounds(left, 0, w, h)
    }

    var drawable: BitmapDrawable? = null
        set(value) {
        field = value
        value?.let {
            it.setBounds(left, 0, w, h)
            it.gravity = Gravity.TOP
            textView.invalidate()
        }

    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (drawable != null) {
            drawable!!.draw(canvas)
        }else{
//            holder.draw(canvas)
        }
    }

    val target = object: SimpleTarget<Bitmap>(w, h){
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            drawable = BitmapDrawable(textView.ctx.resources, resource)
        }
    }
}