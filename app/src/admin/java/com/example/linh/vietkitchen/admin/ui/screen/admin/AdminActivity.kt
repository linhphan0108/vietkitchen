package com.example.linh.vietkitchen.admin.ui.screen.admin

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.design.chip.ChipGroup
import android.support.v7.graphics.Palette
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.capWords
import com.example.linh.vietkitchen.ui.mvpBase.BaseActivity
import com.example.linh.vietkitchen.util.SDKUtil
import com.example.linh.vietkitchen.util.ScreenUtil
import kotlinx.android.synthetic.admin.activity_admin.*
import kotlinx.android.synthetic.admin.activity_admin_content.*
import timber.log.Timber
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.linh.vietkitchen.extension.showSnackBar
import com.example.linh.vietkitchen.extension.toBitmap
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.util.Constants
import com.example.linh.vietkitchen.util.GlideUtil


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

    private var imageUri: Uri? = null
    private lateinit var listImagesUri: MutableList<Uri>
    private lateinit var listCatsChecked: MutableList<DrawerNavChildItem>

    //#region life circle ==================================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categories = intent.extras!!.getParcelableArrayList<DrawerNavGroupItem>(Constants.BK_CATEGORIES)!!.toList()
        presenter.setCategoriesList(categories)
        setupToolbar()
        setupCategoryChip(categories)
        iBtnUpdateImage.setOnClickListener(this)
        iBtnPreparationBrowser.setOnClickListener(this)
        iBtnProcessBrowser.setOnClickListener(this)
        listImagesUri = mutableListOf()
        listCatsChecked = mutableListOf()
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
                        Timber.v("REQUEST_IMAGE $imageUri")
                    }
                }
            }
            REQUEST_IMAGE_PREPARATION -> {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        onRequestPreparationImage(data.data)
                        listImagesUri.add(data.data)
                        Timber.v("REQUEST_IMAGE $imageUri")
                    }
                }
            }
            REQUEST_IMAGE_PROCESS -> {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        onRequestProcessImage(data.data)
                        listImagesUri.add(data.data)
                        Timber.v("REQUEST_IMAGE $imageUri")
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

    override fun onNoInternetException() {

    }

    override fun onGetTagsSuccess(tags: List<String>) {
        if (tags.isEmpty()) return
        val arrAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, tags)
        edtTags.threshold = 1
        edtTags.setAdapter(arrAdapter)

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

    private fun setupCategoryChip(categories: List<DrawerNavGroupItem>) {
        categories.forEach {groupItem ->
            if (groupItem.itemsList.isNullOrEmpty()) return@forEach
            val llChipGroup = LayoutInflater.from(this).inflate(R.layout.chip_category, llContent, false) as LinearLayout
            val txtTitle = llChipGroup.getChildAt(0) as TextView
            val chipGroup = llChipGroup.getChildAt(1) as ChipGroup
            txtTitle.text = groupItem.headerTile
            groupItem.itemsList?.forEach {childItem ->
                val chip = Chip(this)
                chip.setChipBackgroundColorResource(R.color.bg_chip_states)
                chip.setTextAppearance(R.style.ChipTextStyle_Selected)
                chip.text = childItem.itemTitle
//                chip.chipIcon = ContextCompat.getDrawable(requireContext(), baseline_person_black_18)
                chip.isCheckable = true
                chip.setOnCheckedChangeListener { _, checked ->
                    if (checked){
                        if (!listCatsChecked.contains(childItem)) {
                            listCatsChecked.add(childItem)
                        }
                    }else {
                        if (listCatsChecked.contains(childItem)) {
                            listCatsChecked.remove(childItem)
                        }
                    }
                }
                chipGroup.addView(chip as View)
            }
            llContent.addView(llChipGroup)
        }
    }

//    private fun setupTagsEditText(){
//        edtTagIngredients.setTagsWithSpacesEnabled(true)
////        edtTagIngredients.setAdapter(ArrayAdapter<String>(this,
////                android.R.layout.simple_dropdown_item_1line, arrayOfNulls(0)))
////        edtTagIngredients.threshold = 1
//        iBtnAddIngredient.setOnClickListener {
//            val dialog = IngredientInputDialog()
//            dialog.listener = object : IngredientInputDialog.OnResultListeners{
//                override fun onResult(ingredient: Ingredient) {
//                    ingredients[ingredient.name!!] = ingredient
//                    edtTagIngredients.setTags(ingredients.toArrayString())
//                }
//
//            }
//            dialog.show(supportFragmentManager, IngredientInputDialog::class.java.simpleName)
//        }
//
//        edtTagIngredients.setTagsListener(object : TagsEditText.TagsEditListener{
//            override fun onTagsChanged(tags: Collection<String>) {
//                ingredients = ingredients.filterValues {
//                    tags.contains(it.toString())
//                }.toMutableMap()
//            }
//
//            override fun onEditingFinished() {
//            }
//
//        })
//    }

    private fun onSaveAction(){
        if(invalidateRecipe())
            presenter.putARecipe(combineRecipe(), listImagesUri)
    }

    private fun onPreviewAction(){
        presenter.preview(combineRecipe())
    }

    /**
     * @return true if the input data is valid otherwise return false
     */
    private fun invalidateRecipe(): Boolean{

        if(imageUri.toString().isBlank()){
            toast(getString(R.string.error_msg_thumb_image_null))
            return false
        }

        if(edtRecipeTitle.text.isNullOrBlank()){
            edtRecipeTitle.error = getString(R.string.error_msg_title_empty)
            return false
        }

        if(edtIngredients.text.isNullOrBlank()){
            edtIngredients.error = getString(R.string.error_msg_ingredient_empty)
            return false
        }

        if(edtTags.tags.isNullOrEmpty()){
            edtTags.error = getString(R.string.error_msg_tags_empty)
            return false
        }

        if(listCatsChecked.isNullOrEmpty()){
            toast(getString(R.string.error_msg_categories_un_selected))
            return false
        }

        if (edtProcess.text.isNullOrBlank()){
            edtProcess.error = getString(R.string.error_msg_process_step_empty)
            return false
        }

        return true
    }

    private fun combineRecipe(): Recipe {
        val imageUrl = imageUri.toString()
        val thumbUrl = imageUrl
        val title = edtRecipeTitle.text.toString().trim().capWords()
        val shortIntro = edtShortIntro.text.toString().trim().capitalize()
        val ingredients = edtIngredients.text.toString().trim().capitalize()
        val spices = edtSpices.text.toString().trim().capitalize()
        val tags = edtTags.tags

        val categories = listCatsChecked.map {childItem ->
            childItem.itemTitle
        }

        val preparation = edtPreparation.text.trim()
        val process = edtProcess.text.trim()
        val notes = edtNotes.text.toString().trim()
        return Recipe("", title, shortIntro, ingredients, spices, preparation, process, notes,
                categories, tags, thumbUrl, imageUrl)
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
        imageUri = uri
        GlideUtil.widthLoadingHolder(this, imgHeaderImage, uri, object: RequestListener<Drawable?> {
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
        })
    }

    //#endregion inner methods
}