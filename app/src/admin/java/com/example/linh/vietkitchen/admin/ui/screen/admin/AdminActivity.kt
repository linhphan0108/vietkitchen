package com.example.linh.vietkitchen.admin.ui.screen.admin

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import androidx.palette.graphics.Palette
import android.view.*
import android.widget.*
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.util.SDKUtil
import com.example.linh.vietkitchen.util.ScreenUtil
import kotlinx.android.synthetic.admin.activity_admin.*
import kotlinx.android.synthetic.admin.activity_admin_content.*
import timber.log.Timber
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.linh.vietkitchen.extension.*
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.baseMVVM.BaseActivity
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.custom.imageSpanWidget.AnnotationKey
import com.example.linh.vietkitchen.ui.custom.imageSpanWidget.Style
import com.example.linh.vietkitchen.ui.dialog.ProgressDialog
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.detailActivity.RecipeDetailActivity.Companion.createIntent
import com.example.linh.vietkitchen.ui.service.PutRecipeService
import com.example.linh.vietkitchen.util.Constants
import com.example.linh.vietkitchen.util.GlideUtil


private const val REQUEST_IMAGE = 98
private const val REQUEST_IMAGE_PREPARATION = 97
private const val REQUEST_IMAGE_PROCESS = 96

class AdminActivity : BaseActivity(),
        View.OnClickListener, ProgressDialog.Listener, View.OnFocusChangeListener {

    companion object {
        fun createIntent(context: Context): Intent{
            return Intent(context, AdminActivity::class.java)
        }

        fun navigate(context: Context){

        }
    }

    private lateinit var viewModel: AdminViewModel
    private val progressDialog: ProgressDialog by lazy { ProgressDialog() }

    private var imageUri: Uri? = null
    private lateinit var listImagesUri: MutableList<Uri>
    private lateinit var listCatsChecked: MutableList<DrawerNavChildItem>
    private var styleableEditableHasFocused = false
    private var styleableEditableImageHasFocused = false

    //#region life circle ==================================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        setupCategoryChip()
        iBtnUpdateImage.setOnClickListener(this)
        setStyleableEditableViews()
        listImagesUri = mutableListOf()
        listCatsChecked = mutableListOf()
        viewModel.getTags()
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
        if(styleableEditableHasFocused){
            menuInflater.inflate(R.menu.styleable_editable_tool_bar, menu)
            val addPhotoMenuItem = menu.findItem(R.id.action_insert_image)
            val addPhotoIcon = if(styleableEditableImageHasFocused) {
                R.drawable.ic_baseline_add_photo_24_enable
            }else{
                R.drawable.ic_baseline_add_photo_24_disable
            }
            addPhotoMenuItem.setIcon(addPhotoIcon)
        }else{
            menuInflater.inflate(R.menu.admin_menu, menu)
        }
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

            R.id.action_insert_image -> {
                if (styleableEditableImageHasFocused) {
                    requestImage(getRequestCodeForRequestImage())
                }
                true
            }

            R.id.action_style_bold -> {
                onActionStyleBoldSelected()
                true
            }

            R.id.action_paste -> {
                onActionPasteSelected()
                true
            }
            else -> false
        }
    }

    override fun onDestroy() {
        if (progressDialog.isVisible)
            progressDialog.dismiss()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if(styleableEditableHasFocused){
            styleableEditableHasFocused = false
            styleableEditableImageHasFocused = false
            currentFocus?.clearFocus()
        }else {
            super.onBackPressed()
        }
    }

    //#endregion life circle

    //#region MVP callbacks ========================================================================
    override fun getActivityLayoutRes() = R.layout.activity_admin

    override fun getViewModel(): BaseViewModel {
        val factory = AdminViewModelFactory(application)
        viewModel = ViewModelProviders.of(this, factory).get(AdminViewModel::class.java)
        return viewModel
    }

    private fun updateProgress(totalFiles: Int, counter: Int, progress: Int) {
        if (progressDialog.isVisible) {
            progressDialog.updateProgress(totalFiles, counter, progress)
        }
    }

    private fun updateMessage(msg: String) {
        progressDialog.updateMessage(msg)
    }

    private fun showProgressDialog() {
        if (!progressDialog.isVisible)
            progressDialog.show(supportFragmentManager, ProgressDialog::class.java.name)
    }


    private fun onGetTagsSuccess(tags: List<String>) {
        if (tags.isEmpty()) return
        val arrAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, tags)
        edtTags.threshold = 1
        edtTags.setAdapter(arrAdapter)

    }

    private fun onGetTagsFailed(message: String?) {
        message?.let { toast(it) }
    }

    fun onPutNewTagsSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onPutNewTagsFailed(message: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun onPutRecipeSuccess() {
        progressDialog.updateMessage(getString(R.string.msg_store_recipe_finished))
        progressDialog.progressFinish()
    }

    fun onPutRecipeFailed(message: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //#endregion MVP callbacks

    //#region callbacks=============================================================================
    override fun onClick(v: View) {
        when(v.id){
            R.id.iBtnUpdateImage ->
                requestImage(REQUEST_IMAGE)

//            R.id.iBtnPreparationBrowser ->
//                requestImage(REQUEST_IMAGE_PREPARATION)
//
//            R.id.iBtnProcessBrowser ->
//                requestImage(REQUEST_IMAGE_PROCESS)
        }
    }

    //on edit text view focus changed
    override fun onFocusChange(v: View, hasFocus: Boolean) {
        styleableEditableHasFocused = hasFocus
        styleableEditableImageHasFocused = hasFocus && (v == edtPreparation || v == edtProcess)
        invalidateOptionsMenu()
        appBarLayout.setExpanded(false, true)
        if (styleableEditableImageHasFocused){
            scrollToTop(v)
        }
    }

    //ProgressDialog.Listener
    override fun onNewRecipe() {
        resetComposer()
    }
    //#ednregion callbacks

    override fun observeViewModel() {
        viewModel.serviceUploadingStatus.observe(this, Observer {box ->
            box?.let {
                when(it.code){
                    PutRecipeService.MSG_PREPARING_FOR_UPLOADING -> {
                        updateMessage(getString(R.string.msg_prepare_uploading))
                    }
                    PutRecipeService.MSG_UPLOAD_IMAGE_PROGRESS -> {
                        val data = box.data!!
                        updateProgress(data.totalFiles, data.uploadedFiles, data.progress)
                    }
                    PutRecipeService.MSG_START_STORING_RECIPE_TO_DB -> {
                        updateMessage(getString(R.string.msg_start_storing_recipe))
                    }
                    PutRecipeService.MSG_EXTRACT_IMAGES_FROM_RECIPE_CONTENT -> {
                        updateMessage(getString(R.string.msg_extract_images))
                    }
                    PutRecipeService.MSG_OPTIMIZING_IMAGES_BEFORE_UPLOADING -> {
                        updateMessage(getString(R.string.msg_optimizing_images))
                    }

                    PutRecipeService.MSG_START_UPLOADING_IMAGES ->{
                        updateMessage(getString(R.string.msg_start_uploading_images))
                    }

                    PutRecipeService.MSG_STORE_RECIPE_TO_DB_SUCCESS -> {
                        updateMessage(getString(R.string.msg_store_recipe_success))
                    }
                    PutRecipeService.MSG_STORE_RECIPE_TO_DB_FAILED -> {
                        updateMessage(getString(R.string.msg_store_recipe_failed))
                    }
                    PutRecipeService.MSG_UPDATE_NEW_CATEGORIES ->{
                        updateMessage(getString(R.string.msg_update_category))
                    }
                    PutRecipeService.MSG_PUT_NEW_TAGS -> {
                        updateMessage(getString(R.string.msg_put_new_tags))
                    }
                    PutRecipeService.MSG_STORE_RECIPE_TOTALLY_FINISHED ->{
                        onPutRecipeSuccess()
                    }
                }
            }
        })

        viewModel.listTagsOnServerStatus.observe(this, Observer {box ->
            box?.let {
                when(it.code){
                    Status.SUCCESS -> {onGetTagsSuccess(it.data!!)}
                    Status.ERROR -> {onGetTagsFailed(it.message)}
                }
            }
        })

        VietKitchenApp.category.observe(this, Observer {
            setupCategoryChip()
        })
    }

    private fun setupToolbar(){
        appBarLayout.layoutParams.height = ScreenUtil.screenWidth()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        title = ""
        collapsingToolbarLayout.title = title
    }

    private fun setStyleableEditableViews(){
        edtRecipeTitle.onFocusChangeListener = this
        edtShortIntro.onFocusChangeListener = this
        edtIngredients.onFocusChangeListener = this
        edtSpices.onFocusChangeListener = this
        edtPreparation.onFocusChangeListener = this
        edtProcess.onFocusChangeListener = this
        edtNotes.onFocusChangeListener = this
    }

    private fun getRequestCodeForRequestImage(): Int{
        return if (edtPreparation.isFocused){
            REQUEST_IMAGE_PREPARATION
        }else{
            REQUEST_IMAGE_PROCESS
        }
    }

    private fun getCurrentFocusedStyleableEdt(): EditText{
        return currentFocus as EditText
    }

    private fun scrollToTop(view: View){
        appBarLayout.setExpanded(false, true)
        var parentView: View = view.parent as View
        var targetView: View = view
        while (parentView.id != llContent.id){
            targetView = parentView
            parentView = parentView.parent as View
        }
        val relativeToScrollView =  targetView.top - llContent.paddingTop - nestedScrollView.scrollY
        nestedScrollView.smoothScrollBy(0, relativeToScrollView)
    }

    private fun onActionStyleBoldSelected(){
        val currentFocusedEdt = getCurrentFocusedStyleableEdt()
        val editable = currentFocusedEdt.editableText
        val currentSelectionStart = currentFocusedEdt.selectionStart
        val currentSelectionEnd = currentFocusedEdt.selectionEnd
        val selectionText = editable.subSequence(currentSelectionStart, currentSelectionEnd)
        val annotation = "<annotation ${AnnotationKey.STYLE.key}=\"${Style.BOLD}\">$selectionText</annotation> "
        editable.replace(currentSelectionStart, currentSelectionEnd, annotation)
        val newSelection = currentSelectionStart + annotation.length - 13
        currentFocusedEdt.setSelection(newSelection)
    }

    private fun onActionPasteSelected(){
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // If the clipboard doesn't contain data, disable the paste menu item.
        // If it does contain data, decide if you can handle the data.
//        mPasteItem.isEnabled = when {
//            !clipboard.hasPrimaryClip() -> {
//                false
//            }
//            !(clipboard.primaryClipDescription.hasMimeType(MIMETYPE_TEXT_PLAIN)) -> {
//                // This disables the paste menu item, since the clipboard has data but it is not plain text
//                false
//            }
//            else -> {
//                // This enables the paste menu item, since the clipboard contains plain text.
//                true
//            }
//        }

        // Examines the item on the clipboard. If getText() does not return null, the clip item
        // contains the text. Assumes that this application can only handle one item at a time.
        val item = clipboard.primaryClip?.getItemAt(0)
        // Gets the clipboard as text.
        val pasteData = item?.text
        if (pasteData != null){
            val currentFocusedEdt = getCurrentFocusedStyleableEdt()
            val editable = currentFocusedEdt.editableText
            val currentSelectionEnd = currentFocusedEdt.selectionEnd
            editable.insert(currentSelectionEnd, pasteData.toString().toLowerCase())
        }else{
            toast("no data from clipboard")
        }
    }

    private fun setupCategoryChip() {
        VietKitchenApp.category.value?.forEach { groupItem ->
            if (groupItem.itemsList.isNullOrEmpty()) return@forEach
            val llChipGroup = LayoutInflater.from(this).inflate(R.layout.chip_category, llContent, false) as LinearLayout
            val txtTitle = llChipGroup.getChildAt(0) as TextView
            val chipGroup = llChipGroup.getChildAt(1) as ChipGroup
            txtTitle.text = groupItem.headerTile
            groupItem.itemsList.forEach {childItem ->
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
            llChips.addView(llChipGroup)
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
        if(invalidateRecipe()) {
            showProgressDialog()
            viewModel.putARecipe(combineRecipe(), listImagesUri)
        }
    }

    private fun onPreviewAction(){
        with(combineRecipe()){
            val charPreparation = preparation.generateAnnotationSpan()
            val charProcess = processing.generateAnnotationSpan()
            val charIntro = intro?.generateAnnotationSpan()
            val charIngredient = ingredient.generateAnnotationSpan()
            val charSpice = spice.generateAnnotationSpan()
            val charNotes = notes?.generateAnnotationSpan()
            val data = com.example.linh.vietkitchen.ui.model.Recipe(id, name, charIntro, charIngredient, charSpice, charPreparation,
                    charProcess, charNotes, categories, tags, thumbUrl, imageUrl, false)
            val intent = createIntent(this@AdminActivity, "", data)
            startActivity(intent)
        }
    }

    /**
     * @return true if the input data is valid otherwise return false
     */
    private fun invalidateRecipe(): Boolean{
        if(imageUri != null && imageUri.toString().isNullOrBlank()){
            showSnackBar(llContent, R.string.error_msg_thumb_image_null)
            appBarLayout.setExpanded(true, true)
            return false
        }

        if(edtRecipeTitle.text.isNullOrBlank()){
            appBarLayout.setExpanded(false, true)
            scrollToTop(edtRecipeTitle)
            edtRecipeTitle.error = getString(R.string.error_msg_title_empty)
            edtRecipeTitle.postDelayed({
                edtRecipeTitle.requestFocus()
            }, 500)
            return false
        }

        if(edtIngredients.text.isNullOrBlank()){
            appBarLayout.setExpanded(false, true)
            scrollToTop(edtIngredients)
            edtIngredients.error = getString(R.string.error_msg_ingredient_empty)
            edtIngredients.postDelayed({
                edtIngredients.requestFocus()
            }, 500)
            return false
        }

        if (edtProcess.text.isNullOrBlank()){
            appBarLayout.setExpanded(false, true)
            scrollToTop(edtProcess)
            edtProcess.error = getString(R.string.error_msg_process_step_empty)
            edtProcess.postDelayed({
                edtProcess.requestFocus()
            }, 500)
            return false
        }

        if(edtTags.tags.isNullOrEmpty()){
            appBarLayout.setExpanded(false, true)
            scrollToTop(edtTags)
            edtTags.error = getString(R.string.error_msg_tags_empty)
            edtTags.postDelayed({
                edtTags.requestFocus()
            }, 500)
            return false
        }

        if(listCatsChecked.isNullOrEmpty()){
            toast(getString(R.string.error_msg_categories_un_selected))
            appBarLayout.setExpanded(false, true)
            scrollToTop(llChips)
            return false
        }

        return true
    }

    private fun combineRecipe(): Recipe {
        val imageUrl = imageUri.toString()
        val thumbUrl = imageUrl
        val title = edtRecipeTitle.text.toString().trim().capWords()
        val shortIntro = edtShortIntro.text.toString().trim().capParagraph()
        val ingredients = edtIngredients.text.toString().trim().capParagraph()
        val spices = edtSpices.text.toString().trim().capParagraph()
        val tags = edtTags.tags

        val categories = listCatsChecked.map {childItem ->
            childItem.itemTitle
        }

        val preparation = edtPreparation.text.trim().capParagraph()
        val process = edtProcess.text.trim().capParagraph()
        val notes = edtNotes.text.trim().capParagraph()
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
        var annotation: CharSequence = "<annotation src=\"$uri\"/>"
        if (editable.isNotBlank()){
            annotation = annotation.breakLineFirst().breakLineLast()
        }
        editable.insert(pos, annotation)
    }

    private fun onRequestProcessImage(uri: Uri){
        val pos = edtProcess.selectionEnd
        val editable = edtProcess.editableText
        val annotation = "\n<annotation src=\"$uri\"/>\n"
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
                        androidx.palette.graphics.Palette.from(resource.toBitmap()).generate { palette ->
                            palette?.also {applyPalette(it, collapsingToolbarLayout)}
                        }}catch (e: Exception){
                        toast("exception thrown when generate palette")
                    }
                }
                return false
            }
        }).into(imgHeaderImage)
    }

    private fun resetComposer(){
        imageUri = null
        listImagesUri.clear()
        listCatsChecked.clear()
        edtRecipeTitle.setText("")
        edtShortIntro.setText("")
        edtIngredients.setText("")
        edtSpices.setText("")
        edtPreparation.setText("")
        edtProcess.setText("")
        edtNotes.setText("")
        edtTags.setTags(null)
        imgHeaderImage.setImageResource(0)
        resetChips()
        currentFocus?.clearFocus()
        scrollToTop(edtRecipeTitle)
        appBarLayout.setExpanded(true, true)
    }

    private fun resetChips(){
        for (i in 0 until llChips.childCount){
            val llChipGroup = llChips.getChildAt(i) as ViewGroup
            for (j in 0 until  llChipGroup.childCount) {
                if (llChipGroup.getChildAt(j) is ChipGroup) {
                    val chipGroup = llChipGroup.getChildAt(j) as ChipGroup
                    for (k in 0 until chipGroup.childCount) {
                        val chip = chipGroup.getChildAt(k) as Chip
                        if (chip.isChecked) chip.isChecked = false
                    }
                }
            }
        }
    }

    //#endregion inner methods
}