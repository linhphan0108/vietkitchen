package com.example.linh.vietkitchen.ui.screen.detailActivity

import android.app.Activity
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
import com.example.linh.vietkitchen.ui.mvpBase.BaseActivity
import kotlinx.android.synthetic.main.activity_detail.*
import android.view.MenuItem
import android.view.ViewGroup
import com.example.linh.vietkitchen.extension.*
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.model.UserInfo
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.util.GlideUtil
import com.example.linh.vietkitchen.util.ScreenUtil
import kotlinx.android.synthetic.main.activity_detail_content.*


private const val BK_THUMB_IMAGE_TRANSITION_NAME = "BK_THUMB_IMAGE_TRANSITION_NAME"
private const val EXTRA_BUNDLE = "EXTRA_BUNDLE"
private const val BK_RECIPE = "BK_RECIPE"
const val BK_LIKE_STATE_JUST_CHANGED = "BK_LIKE_STATE_JUST_CHANGED"

class RecipeDetailActivity : BaseActivity<RecipeDetailViewContract>(),
        RecipeDetailViewContract, View.OnClickListener {
    private val presenter: RecipeDetailPresenterContract by lazy { RecipeDetailPresenter() }
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
    private lateinit var userInfo: UserInfo
    private lateinit var recipe: Recipe
    private var likeState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = VietKitchenApp.userInfo
        intent.getBundleExtra(EXTRA_BUNDLE).let {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                appBarLayout.transitionName = it.getString(BK_THUMB_IMAGE_TRANSITION_NAME)
//            }
            recipe = it.getParcelable(BK_RECIPE)!!
            likeState = recipe.hasLiked
            setupToolbar(recipe.name)
            populateUI(recipe)
            onFabStateChanged(recipe.hasLiked)
        }
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
        if (recipe.hasLiked != likeState){
            intent.putExtra(BK_LIKE_STATE_JUST_CHANGED, likeState)
            setResult(Activity.RESULT_OK, intent)
        }
        super.onBackPressed()
    }

    //region MVP callbacks =========r================================================================
    override val viewContext: Context?
        get() = this

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun getPresenter(): BasePresenterContract<RecipeDetailViewContract> {
        return presenter
    }

    override fun getViewContract() = this

    override fun getActivityLayoutRes() = R.layout.activity_detail

    override fun onNoInternetException() {
    }

    override fun onLikeChangedSuccess(state: Boolean) {
        likeState = state
        onFabStateChanged(state)
    }

    override fun onLikeChangedFailed() {
    }
    //endregion MVP callbacks

    //==
    override fun onClick(v: View) {
        v.lookTemporary()
        when(v.id){
            R.id.fab -> {
                if (likeState){
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
            txtDescription.text = intro
            txtIngredients.text = ingredient

            (txtSpices.parent as ViewGroup).visibility = if (spice.isNotBlank()){
                txtSpices.text = spice
                View.VISIBLE
            } else View.INVISIBLE

            llStepsToPreProcess.visibility = if (preparation.isNotBlank()){
                txtStepsToPreparation.setTextAsSpannable(preparation)
                View.VISIBLE
            } else View.INVISIBLE

            txtStepsToProcess.setTextAsSpannable(processing)

            (txtNotes.parent as ViewGroup).visibility = if(notes.isNotNullAndNotBlank()){
                txtNotes.text = notes
                View.VISIBLE
            } else View.INVISIBLE
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
        val message = getString(R.string.message_confirm_unlike, recipe.name)
        val action = getString(R.string.label_ok)
        showSnackBar(coordinatorLayout, message, action = action, listener = View.OnClickListener {
            doLikeActions()
        })
    }

    private fun doLikeActions(){
        presenter.onLikeChanged(userInfo.uid, recipe.id!!, !likeState)
    }
    //endregion inner methods
}

//inner classes ====================================================================================
