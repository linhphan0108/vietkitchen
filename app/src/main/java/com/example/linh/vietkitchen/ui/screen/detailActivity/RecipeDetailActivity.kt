package com.example.linh.vietkitchen.ui.screen.detailActivity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
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
import android.view.MenuItem
import android.view.ViewGroup
import com.example.linh.vietkitchen.extension.*
import com.example.linh.vietkitchen.ui.baseMVVM.BaseActivity
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.util.GlideUtil
import com.example.linh.vietkitchen.util.ScreenUtil
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail_content.*


internal const val BK_THUMB_IMAGE_TRANSITION_NAME = "BK_THUMB_IMAGE_TRANSITION_NAME"
internal const val EXTRA_BUNDLE = "EXTRA_BUNDLE"
internal const val BK_RECIPE = "BK_RECIPE"
const val BK_LIKE_STATE_JUST_CHANGED = "BK_LIKE_STATE_JUST_CHANGED"

class RecipeDetailActivity : BaseActivity(), View.OnClickListener {
    private lateinit var viewModel: RecipeDetailViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCreate(intent)
        viewModel.recipe.observe(this, Observer<Recipe> { recipe ->
            recipe?.let {
                setupToolbar(recipe.name)
                populateUI(recipe)
            }
        })
        viewModel.likeState.observe(this, Observer {hasLiked ->
            onFabStateChanged(hasLiked!!)
        })
        fab.setOnClickListener(this)
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

    override fun onBackPressed() {
        if (viewModel.stateHasChanged()){
            intent.putExtra(BK_LIKE_STATE_JUST_CHANGED, viewModel.likeState.value)
            setResult(Activity.RESULT_OK, intent)
        }
        super.onBackPressed()
    }

    override fun getActivityLayoutRes() = R.layout.activity_detail

    override fun getViewModel(): BaseViewModel {
        val factory = DetailViewModelFactory(application)
        viewModel = ViewModelProviders.of(this, factory).get(RecipeDetailViewModel::class.java)
        return viewModel
    }

    override fun observeViewModel() {
    }

    //==
    override fun onClick(v: View) {
        v.lookTemporary()
        when(v.id){
            R.id.fab -> {
                if (viewModel.likeState.value!!){
                    confirmUnlike()
                }else {
                    doLikeActions()
                }
            }
        }
    }

    //region inner methods =========================================================================
    private fun setupToolbar(title: String){
        appBarLayout.layoutParams.height = ScreenUtil.screenWidth()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        collapsingToolbarLayout.title = title
        applyPalette(null, collapsingToolbarLayout)
    }

    private fun populateUI(recipe: Recipe) {
        GlideUtil.widthLoadingHolder(this, imgHeaderImage, recipe.imageUrl, object : RequestListener<Drawable?>{
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                if (resource != null){
                    try {
                        Palette.from(resource.toBitmap()).generate { palette ->
                            palette?.also {applyPalette(it, collapsingToolbarLayout)}
                        }}catch (e: Exception){
                        toast("exception thrown when generate palette")
                    }
                }
                return false
            }

        }).override(ScreenUtil.getMaxWidthImage())
                .into(imgHeaderImage)


        with(recipe) {
//            txtTitle.text = name.capWords()
            intro?.let { txtDescription.setTextAsSpannable(it)}
            txtIngredients.setTextAsSpannable(ingredient)

            (txtSpices.parent as ViewGroup).visibility = if (spice.isNotBlank()){
                txtSpices.setTextAsSpannable(spice)
                View.VISIBLE
            } else View.GONE

            llStepsToPreProcess.visibility = if (preparation.isNotBlank()){
                txtStepsToPreparation.setTextAsSpannable(preparation)
                View.VISIBLE
            } else View.GONE

            txtStepsToProcess.setTextAsSpannable(processing)

            (txtNotes.parent as ViewGroup).visibility = if(notes.isNotNullAndNotBlank()){
                txtNotes.setTextAsSpannable(notes!!)
                View.VISIBLE
            } else View.GONE
        }
    }

    private fun onFabStateChanged(state: Boolean){
        val fabIcon = if(state) R.drawable.ic_heart_pink else R.drawable.ic_heart_grey
        fab.setImageResource(fabIcon)
    }

    private fun updateBackground(fab: FloatingActionButton, palette: Palette) {
        val lightVibrantColor = palette.getLightVibrantColor(color(android.R.color.white))
        val vibrantColor = palette.getVibrantColor(color(R.color.colorAccent))

        fab.rippleColor = lightVibrantColor
        fab.backgroundTintList = ColorStateList.valueOf(vibrantColor)
    }


    private fun confirmUnlike(){
        val message = getString(R.string.message_confirm_unlike, viewModel.recipe.value!!.name)
        val action = getString(R.string.label_ok)
        showSnackBar(coordinatorLayout, message, action = action, listener = View.OnClickListener {
            doLikeActions()
        })
    }

    private fun doLikeActions(){
        viewModel.onLikeChanged()
    }
    //endregion inner methods
}

//inner classes ====================================================================================
