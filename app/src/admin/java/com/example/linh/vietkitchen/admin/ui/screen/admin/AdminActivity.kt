package com.example.linh.vietkitchen.admin.ui.screen.admin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.capWords
import com.example.linh.vietkitchen.ui.custom.TagsEditText
import com.example.linh.vietkitchen.ui.model.Ingredient
import com.example.linh.vietkitchen.ui.mvpBase.BaseActivity
import com.example.linh.vietkitchen.util.SDKUtil
import com.example.linh.vietkitchen.util.ScreenUtil
import kotlinx.android.synthetic.admin.activity_admin.*
import kotlinx.android.synthetic.admin.activity_admin_content.*
import timber.log.Timber
import android.widget.ArrayAdapter
import com.example.linh.vietkitchen.extension.showSnackBar
import com.example.linh.vietkitchen.extension.toArrayString
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.ui.GlideApp
import com.example.linh.vietkitchen.ui.dialog.IngredientInputDialog
import com.example.linh.vietkitchen.ui.model.Recipe


private const val REQUEST_IMAGE = 98
private const val REQUEST_IMAGE_PREPARATION = 97
private const val REQUEST_IMAGE_PROCESS = 96

class AdminActivity : BaseActivity<AdminContractView, AdminContractPresenter>(), AdminContractView, View.OnClickListener {

    companion object {
        fun createIntent(context: Context): Intent{
            return Intent(context, AdminActivity::class.java)
        }

        fun navigate(context: Context){

        }
    }

    private var imageUrl: Uri? = null
    private lateinit var listImagesUri: MutableList<Uri>
    var ingredients: MutableMap<String, Ingredient> = mutableMapOf()

    //#region life circle ==================================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        setupTagsEditText()
        iBtnUpdateImage.setOnClickListener(this)
        iBtnPreparationBrowser.setOnClickListener(this)
        iBtnProcessBrowser.setOnClickListener(this)
        listImagesUri = mutableListOf()
        presenter.getTags()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE -> {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        onRequestHeaderImage(data.data)
                        listImagesUri.add(data.data)
                        Timber.v("REQUEST_IMAGE $imageUrl")
                    }
                }
            }
            REQUEST_IMAGE_PREPARATION -> {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        onRequestPreparationImage(data.data)
                        listImagesUri.add(data.data)
                        Timber.v("REQUEST_IMAGE $imageUrl")
                    }
                }
            }
            REQUEST_IMAGE_PROCESS -> {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        onRequestProcessImage(data.data)
                        listImagesUri.add(data.data)
                        Timber.v("REQUEST_IMAGE $imageUrl")
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_save ->{
                onSaveAction()
                true
            }

            R.id.action_preview -> {
                onPreviewAction()
                true
            }

            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> false
        }
    }
    //#endregion life circle

    //#region MVP callbacks ========================================================================
    override val viewContext: Context?
        get() = this

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun initPresenter() = AdminPresenter()

    override fun getViewContract() = this

    override fun getActivityLayoutRes() = R.layout.activity_admin

    override fun onGetTagsSuccess(tags: List<String>) {
        if (tags.isEmpty()) return
        val arrAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, tags)
        edtTagIngredients.threshold = 1
        edtTagIngredients.setAdapter(arrAdapter)

    }

    override fun onGetTagsFailed(message: String?) {
        message?.let { toast(it) }
    }

    override fun onPutNewTagsSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPutNewTagsFailed(message: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPutRecipeSuccess() {
        showSnackBar(coordinatorLayout, "recipe saved successfully")
    }

    override fun onPutRecipeFailed(message: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //#endregion MVP callbacks

    //#region callbacks=============================================================================
    override fun onClick(v: View) {
        when(v.id){
            R.id.iBtnUpdateImage ->
                requestImage(REQUEST_IMAGE)

            R.id.iBtnPreparationBrowser ->
                requestImage(REQUEST_IMAGE_PREPARATION)

            R.id.iBtnProcessBrowser ->
                requestImage(REQUEST_IMAGE_PROCESS)
        }
    }
    //#ednregion callbacks

    //#region inner methods ========================================================================
    private fun setupToolbar(){
        appBarLayout.layoutParams.height = ScreenUtil.screenWidth()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        collapsingToolbarLayout.title = title
    }

    private fun setupTagsEditText(){
        edtTagIngredients.setTagsWithSpacesEnabled(true)
//        edtTagIngredients.setAdapter(ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, arrayOfNulls(0)))
//        edtTagIngredients.threshold = 1
        iBtnAddIngredient.setOnClickListener {
            val dialog = IngredientInputDialog()
            dialog.listener = object : IngredientInputDialog.OnResultListeners{
                override fun onResult(ingredient: Ingredient) {
                    ingredients[ingredient.name!!] = ingredient
                    edtTagIngredients.setTags(ingredients.toArrayString())
                }

            }
            dialog.show(supportFragmentManager, IngredientInputDialog::class.java.simpleName)
        }

        edtTagIngredients.setTagsListener(object : TagsEditText.TagsEditListener{
            override fun onTagsChanged(tags: Collection<String>) {
                ingredients = ingredients.filterValues {
                    tags.contains(it.toString())
                }.toMutableMap()
            }

            override fun onEditingFinished() {
            }

        })
    }

    private fun onSaveAction(){
        presenter.putARecipe(combineRecipe(), listImagesUri)
    }

    private fun onPreviewAction(){
        presenter.preview(combineRecipe())
    }

    private fun combineRecipe(): Recipe {
        val imageUrl = if(imageUrl.toString().isBlank())"" else imageUrl.toString()
        val thumbUrl = imageUrl
        val title = edtRecipeTitle.text.toString().trim().capWords()
        val shortIntro = edtShortIntro.text.toString().trim().capitalize()
        val spices = edtSpices.text.toString().trim().capitalize()
        val method = edtMethod.tags
        val benefit = edtBenefit.tags
        val season = edtSeason.tags
        val region = edtRegion.text.toString().trim()
        val specialDay = edtSpecialDay.text.toString().trim()
        val tags = edtTags.tags

        val preparation = edtPreparation.text.trim()
        val process = edtProcess.text.trim()
        return Recipe("", title, shortIntro, ingredients, spices, preparation, process,
                method, benefit, season, region, specialDay, tags, thumbUrl, imageUrl, false)
    }

//    private fun openGallery() {
//        if (Build.VERSION.SDK_INT < 19) {
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(Intent.createChooser(intent, resources.getString(R.string.select_picture)), GALLERY_INTENT_CALLED)
//        } else {
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.type = "image/*"
//            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED)
//        }
//    }

    private fun requestImage(requestCode: Int){
        val intent = Intent(if(SDKUtil.atLeastKitKat())Intent.ACTION_OPEN_DOCUMENT
        else Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    private fun onRequestPreparationImage(uri: Uri){
        val pos = edtPreparation.selectionEnd
        val editable = edtPreparation.editableText
        val annotation = "<annotation src=\"$uri\"/>"
        editable.insert(pos, annotation)
    }

    private fun onRequestProcessImage(uri: Uri){
        val pos = edtProcess.selectionEnd
        val editable = edtProcess.editableText
        val annotation = "<annotation src=\"$uri\"/>"
        editable.insert(pos, annotation)
    }

    private fun onRequestHeaderImage(uri: Uri){
        imageUrl = uri
        GlideApp.with(this)
                .load(uri)
                .into(imgHeaderImage)
    }

    //#endregion inner methods
}