package com.example.linh.vietkitchen.ui.screen.detailActivity

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.graphics.Palette
import android.view.View
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
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.example.linh.vietkitchen.extension.color
import com.example.linh.vietkitchen.extension.toBitmap
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.util.ScreenUtil


private const val BK_THUMB_IMAGE_TRANSITION_NAME = "BK_THUMB_IMAGE_TRANSITION_NAME"
private const val EXTRA_BUNDLE = "EXTRA_BUNDLE"
private const val BK_RECIPE = "BK_RECIPE"

class RecipeDetailActivity : BaseActivity<RecipeDetailViewContract, RecipeDetailPresenter>(),
        RecipeDetailViewContract {
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
    private var shouldHidePreProcessLayout = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getBundleExtra(EXTRA_BUNDLE).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                appBarLayout.transitionName = it.getString(BK_THUMB_IMAGE_TRANSITION_NAME)
            }
            val recipe = it.getParcelable<Recipe>(BK_RECIPE)
            setupToolbar(recipe.name)
            populateUI(recipe)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home->{
                onBackPressed()
                true
            }
            else ->{
                super.onOptionsItemSelected(item)
            }
        }
    }

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

    //endregion animation callbacks

    //region inner methods =========================================================================

    private fun setupToolbar(title: String){
        appBarLayout.layoutParams.height = ScreenUtil.screenWidth()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        collapsingToolbarLayout.title = title
        applyPalette(null)
    }

    private fun populateUI(recipe: Recipe) {
        image.scaleType = ImageView.ScaleType.CENTER_INSIDE
        GlideApp.with(this)
                .load(recipe.imageUrl)
                .disallowHardwareConfig()
                .placeholder(R.drawable.ic_loading_gif)
                .override(ScreenUtil.screenWidth())
                .listener(object : RequestListener<Drawable?>{
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        if (resource != null){
                            try {
                            Palette.from(resource.toBitmap()).generate {
                                applyPalette(it)
                            }}catch (e: Exception){
                                toast("exception thrown when generate palette")
                            }
                        }
                        image.scaleType = ImageView.ScaleType.CENTER_CROP
                        return false
                    }

                })
                .into(image)

        with(recipe) {
            val builder = StringBuilder()
            for ((key, value) in ingredient){
                builder.append(key)
                if (value.notes.isNullOrBlank()) builder.append("${value.notes}\n")
                builder.append("${value.quantity}${value.unit}\n")
            }

            txtTitle.text = name.capWords()
            txtDescription.text = intro
            txtIngredients.text = builder.substring(0, builder.length - 1)
            txtSpices.text = spice

            if (preliminaryProcessing.isBlank()) {
                llStepsToPreProcess.visibility = View.GONE
                shouldHidePreProcessLayout = true
            }else{
                txtStepsToPreProcess.setText(preliminaryProcessing, TextView.BufferType.SPANNABLE)
            }

            txtStepsToProcess.setText(processing, TextView.BufferType.SPANNABLE)
        }
    }

    private fun applyPalette(palette: Palette?) {
        val transparent = color(android.R.color.transparent)
        val primaryDark = color(R.color.colorPrimaryDark)
        val primary = color(R.color.colorPrimary)
        val mutedPrimary = palette?.getMutedColor(primary) ?: primary
        val mutedPrimaryDark = palette?.getDarkMutedColor(primaryDark) ?: primaryDark
        collapsingToolbarLayout.setContentScrimColor(mutedPrimary)
        collapsingToolbarLayout.setStatusBarScrimColor(mutedPrimaryDark)
        collapsingToolbarLayout.setExpandedTitleColor(transparent)
    }

    private fun updateBackground(fab: FloatingActionButton, palette: Palette) {
        val lightVibrantColor = palette.getLightVibrantColor(color(android.R.color.white))
        val vibrantColor = palette.getVibrantColor(color(R.color.colorAccent))

        fab.rippleColor = lightVibrantColor
        fab.backgroundTintList = ColorStateList.valueOf(vibrantColor)
}
    //endregion inner methods
}

//inner classes ====================================================================================
