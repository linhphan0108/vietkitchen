package com.example.linh.vietkitchen.admin.ui.screen.admin

import android.annotation.SuppressLint
import com.example.linh.vietkitchen.domain.command.RequestTagsCommand
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.domain.model.Recipe as RecipeDomain
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import com.example.linh.vietkitchen.ui.screen.detailActivity.RecipeDetailActivity
import timber.log.Timber
import com.example.linh.vietkitchen.extension.generateAnnotationSpan
import com.example.linh.vietkitchen.ui.service.PutRecipeService
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.net.Uri
import android.os.*
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.filterFirst
import com.example.linh.vietkitchen.extension.toListOfStringOfKey
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.util.RecipeUtil
import com.example.linh.vietkitchen.util.TimberUtils


class AdminPresenter(private val requestTagsCommand: RequestTagsCommand = RequestTagsCommand())
    : BasePresenter<AdminContractView>(), AdminContractPresenter {

    private lateinit var listTagsOnServer: List<String>
    private lateinit var categories: List<DrawerNavGroupItem>

    // Don't attempt to unbind from the service unless the client has received some
    // information about the service's state.
    private var isBounded: Boolean = false
    // To invoke the bound service, first make sure that this value
    // is not null.
//    private var boundService: PutRecipeService? = null
    /** Messenger for communicating with service.  */
    var serviceMessenger: Messenger? = null
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    val clientMessage = Messenger(IncomingHandler())


    override fun attachView(view: AdminContractView) {
        super.attachView(view)
    }

    override fun detachView() {
        super.detachView()
        doUnbindService()
    }

    //==============================================================================================
    override fun setCategoriesList(categories: List<DrawerNavGroupItem>) {
        this.categories = categories
    }

    override fun openCategoryDialogChecker() {
        val dialog = CategoryChecker.newInstance(categories)
        dialog.callback = object: CategoryChecker.OnDismissCallback {
            override fun onDismiss(listCatsChecked: MutableList<DrawerNavChildItem>) {

            }
        }
        dialog.show(activity!!.supportFragmentManager, CategoryChecker::class.java.name)
    }

    override fun preview(recipe: Recipe) {
        with(recipe){
            val charPreparation = preparation.generateAnnotationSpan()
            val charProcess = processing.generateAnnotationSpan()
            val charIntro = intro?.generateAnnotationSpan()
            val charIngredient = ingredient.generateAnnotationSpan()
            val charSpice = spice.generateAnnotationSpan()
            val charNotes = notes?.generateAnnotationSpan()
            val data = Recipe(id, name, charIntro, charIngredient, charSpice, charPreparation,
                    charProcess, charNotes, categories, tags,thumbUrl, imageUrl, false)
            val intent = RecipeDetailActivity.createIntent(context, "", data)
            context?.startActivity(intent)
        }
    }

    override fun putARecipe(recipe: Recipe, listImagesUri: MutableList<Uri>) {
        viewContract?.showProgressDialog()
        launchDataLoad({
            val message = Message.obtain(null, PutRecipeService.MSG_PREPARING_FOR_UPLOADING)
            clientMessage.send(message)
            val extractedListImageUris = withComputationContext {
                TimberUtils.checkNotMainThread()
                extractContentImagePaths(recipe, listImagesUri)
            }
            val newTags = withComputationContext {
                TimberUtils.checkNotMainThread()
                recipe.tags?.filterNot {
                    listTagsOnServer.contains(it)
                }
            }

            val updatedCategory = withComputationContext{
                TimberUtils.checkNotMainThread()
                updateCategories(recipe, categories)
            }
            doBindService(recipe, extractedListImageUris, newTags, updatedCategory)
        }, {

        })
    }


    override fun getTags() {
        launchDataLoad({
            listTagsOnServer = withIoContext {
                val map = requestTagsCommand.executeOnTheInternet(context!!)
                map.data!!.toListOfStringOfKey()
            }
            viewContract?.onGetTagsSuccess(listTagsOnServer)
        }, {
            viewContract?.onGetTagsFailed(it.message)
        })
    }

    private fun extractContentImagePaths(recipe: Recipe, listImages: List<Uri>): List<Uri> {
        Timber.d("extract Image path from the recipe's content")
        val listMatchedUris = mutableListOf<Uri>()
        listImages.filterFirst {it.toString() == recipe.imageUrl}
                ?.let { listMatchedUris.add(it) }
        RecipeUtil.extractImagePathOnlyInContent(recipe).forEach {extractedUrl ->
            listImages.filterFirst {it.toString() == extractedUrl}
                    ?.let { listMatchedUris.add(it) }
        }
        Timber.d("extract ${listMatchedUris.count()} images uri from recipe")
        return listMatchedUris
    }

    private fun updateCategories(recipe: Recipe, categories: List<DrawerNavGroupItem>): List<DrawerNavGroupItem> {
        val clonedCat = categories.map { it.clone() }
        clonedCat.forEach { groupCat ->
            var hasContained = false
            groupCat.itemsList?.forEach { childCat ->
                if(recipe.categories.contains(childCat.itemTitle)) {
                    childCat.numberItems++
                    hasContained = true
                }
            }
            if (hasContained) groupCat.numberItems++
        }

        //increase the all item
        clonedCat.first().numberItems++
        return clonedCat
    }

    private fun doBindService(recipe: Recipe, listImagesUri: List<Uri>, newTags: List<String>?, newDrawerNav: List<DrawerNavGroupItem>) {
        // Attempts to establish a connection with the service.  We use an
        // explicit class name because we want a specific service
        // implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other
        // applications).
        if (context == null) return
        val intent = PutRecipeService.createIntent(context!!)
        mConnection.recipe = recipe
        mConnection.listImagesUri = listImagesUri
        mConnection.newTags = newTags
        mConnection.newDrawerNav = newDrawerNav
        when {
            isBounded -> sendUploadCommandToService(recipe, listImagesUri, newTags, newDrawerNav)
            context?.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)!! -> isBounded = true
            else -> Timber.e("Error: The requested service doesn't exist, or this client isn't allowed access to it.")
        }
    }

    private fun doUnbindService() {
        if (isBounded) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (serviceMessenger != null) {
                try {
                    val msg = Message.obtain (null, PutRecipeService.MSG_UNREGISTER_CLIENT)
                    msg.replyTo = clientMessage
                    serviceMessenger?.send(msg)
                } catch (e: RemoteException) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }
            //Detach our existing connection.
            context?.unbindService(mConnection)
            isBounded = false
        }
    }

    private fun sendUploadCommandToService(recipe: Recipe, listImagesUri: List<Uri>, newTags: List<String>?, newDrawerNav: List<DrawerNavGroupItem>) {
        // We want to monitor the service for as long as we are
        // connected to it.
        try {
            var msg = Message.obtain(null, PutRecipeService.MSG_REGISTER_CLIENT)
            msg.replyTo = clientMessage
            serviceMessenger?.send(msg)

            // Give it some value as an example.
            msg = Message.obtain(null, PutRecipeService.MSG_UPLOAD_RECIPE)
            val bundle = Bundle()
            bundle.putParcelable(PutRecipeService.BK_RECIPE_TO_UPLOAD, recipe)
            bundle.putParcelableArrayList(PutRecipeService.BK_LIST_IMAGES_URI, ArrayList(listImagesUri))
            newTags?.also {
                bundle.putStringArrayList(PutRecipeService.BK_LIST_NEW_TAGS, ArrayList(it))
            }
            bundle.putParcelableArrayList(PutRecipeService.BK_NEW_CATEGORIES, ArrayList(newDrawerNav))

            msg.data = bundle
            serviceMessenger?.send(msg)
//
        } catch (e: RemoteException) {
            // In this case the service has crashed before we could even
            // do anything with it; we can count on soon being
            // disconnected (and then reconnected if it can be restarted)
            // so there is no need to do anything here.
        }
    }

    private val mConnection = object : ServiceConnection {
        lateinit var recipe: Recipe
        lateinit var listImagesUri: List<Uri>
        var newTags: List<String>? = null
        lateinit var newDrawerNav: List<DrawerNavGroupItem>
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            serviceMessenger = Messenger(service)
            sendUploadCommandToService(recipe, listImagesUri, newTags, newDrawerNav)
            // Tell the user about this for our demo.
            Timber.d("service has connected")
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            serviceMessenger = null
            Timber.d("service has disconnected")
        }
    }

    /**
     * Handler of incoming messages from service.
     */
    @SuppressLint("HandlerLeak")
    internal inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                PutRecipeService.MSG_UPLOAD_IMAGE_PROGRESS -> {
                    val bundle = msg.data
                    val progress = bundle.getInt(PutRecipeService.BK_UPLOAD_PROGRESS)
                    val counter = bundle.getInt(PutRecipeService.BK_UPLOAD_COUNTER)
                    val total = bundle.getInt(PutRecipeService.BK_UPLOAD_TOTAL)
                    viewContract?.updateProgress(total, counter, progress)
                }
                PutRecipeService.MSG_PREPARING_FOR_UPLOADING -> {
                    viewContract?.updateMessage(getStringRes(R.string.msg_prepare_uploading))
                }
                PutRecipeService.MSG_START_STORING_RECIPE_TO_DB -> {
                    viewContract?.updateMessage(getStringRes(R.string.msg_start_storing_recipe))
                }
                PutRecipeService.MSG_EXTRACT_IMAGES_FROM_RECIPE_CONTENT -> {
                    viewContract?.updateMessage(getStringRes(R.string.msg_extract_images))
                }
                PutRecipeService.MSG_OPTIMIZING_IMAGES_BEFORE_UPLOADING -> {
                    viewContract?.updateMessage(getStringRes(R.string.msg_optimizing_images))
                }

                PutRecipeService.MSG_START_UPLOADING_IMAGES ->{
                    viewContract?.updateMessage(getStringRes(R.string.msg_start_uploading_images))
                }

                PutRecipeService.MSG_STORE_RECIPE_TO_DB_SUCCESS -> {
                    viewContract?.updateMessage(getStringRes(R.string.msg_store_recipe_success))
                }
                PutRecipeService.MSG_STORE_RECIPE_TO_DB_FAILED -> {
                    viewContract?.updateMessage(getStringRes(R.string.msg_store_recipe_failed))
                }
                PutRecipeService.MSG_UPDATE_NEW_CATEGORIES ->{
                    viewContract?.updateMessage(getStringRes(R.string.msg_update_category))
                }
                PutRecipeService.MSG_PUT_NEW_TAGS -> {
                    viewContract?.updateMessage(getStringRes(R.string.msg_put_new_tags))
                }
                PutRecipeService.MSG_STORE_RECIPE_TOTALLY_FINISHED ->{
                    viewContract?.onPutRecipeSuccess()
                }
                else -> super.handleMessage(msg)
            }
        }
    }
}