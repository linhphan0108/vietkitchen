package com.example.linh.vietkitchen.admin.ui.screen.admin

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.domain.command.RequestTagsCommand
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.domain.model.Recipe as RecipeDomain
import timber.log.Timber
import com.example.linh.vietkitchen.ui.service.PutRecipeService
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.net.Uri
import android.os.*
import com.example.linh.vietkitchen.extension.filterFirst
import com.example.linh.vietkitchen.extension.toListOfStringOfKey
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.baseMVVM.StatusBox
import com.example.linh.vietkitchen.util.RecipeUtil
import com.example.linh.vietkitchen.util.TimberUtils


class AdminViewModel(application: Application,
        private val requestTagsCommand: RequestTagsCommand = RequestTagsCommand())
    : BaseViewModel(application) {

    internal val listTagsOnServerStatus: MutableLiveData<StatusBox<List<String>>> =  MutableLiveData()
    internal val serviceUploadingStatus: MutableLiveData<StatusBox<UploadProgress>> = MutableLiveData()

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
    private val clientMessage = Messenger(IncomingHandler())

    override fun onCleared() {
        doUnbindService()
        super.onCleared()
    }
    //==============================================================================================
    fun putARecipe(recipe: Recipe, listImagesUri: MutableList<Uri>) {
        launchDataLoad({
            val message = Message.obtain(null, PutRecipeService.MSG_PREPARING_FOR_UPLOADING)
            clientMessage.send(message)
            val extractedListImageUris = withComputationContext {
                TimberUtils.checkNotMainThread()
                extractContentImagePaths(recipe, listImagesUri)
            }
            val newTags = withComputationContext {
                TimberUtils.checkNotMainThread()
                recipe.tags?.filterNot {tag ->
                    listTagsOnServerStatus.value?.data?.contains(tag) ?: false
                }
            }

            val updatedCategory = withComputationContext{
                TimberUtils.checkNotMainThread()
                updateCategories(recipe)
            }
            doBindService(recipe, extractedListImageUris, newTags, updatedCategory)
        }, {
            Timber.e(it)
        }, false)
    }


    fun getTags() {
        launchDataLoad({
            val listTags = withIoContext {
                val map = requestTagsCommand.execute(getApplication())
                map.data!!.toListOfStringOfKey()
            }
            listTagsOnServerStatus.value = StatusBox(Status.SUCCESS, data = listTags)
        }, {
            listTagsOnServerStatus.value = StatusBox(Status.ERROR, it.message)
        }, false)
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

    private fun updateCategories(recipe: Recipe): List<DrawerNavGroupItem> {
        return VietKitchenApp.category.value?.let { cat ->
            val clonedCat = cat.map { it.clone() }
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
            clonedCat
        } ?: listOf()
    }

    private fun doBindService(recipe: Recipe, listImagesUri: List<Uri>, newTags: List<String>?, newDrawerNav: List<DrawerNavGroupItem>) {
        // Attempts to establish a connection with the service.  We use an
        // explicit class name because we want a specific service
        // implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other
        // applications).
        val intent = PutRecipeService.createIntent(getApplication())
        mConnection.recipe = recipe
        mConnection.listImagesUri = listImagesUri
        mConnection.newTags = newTags
        mConnection.newDrawerNav = newDrawerNav
        when {
            isBounded -> sendUploadCommandToService(recipe, listImagesUri, newTags, newDrawerNav)
            getApplication<Application>().bindService(intent, mConnection, Context.BIND_AUTO_CREATE) -> isBounded = true
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
            getApplication<Application>().unbindService(mConnection)
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
                    serviceUploadingStatus.value = StatusBox(PutRecipeService.MSG_UPLOAD_IMAGE_PROGRESS,
                            data = UploadProgress(progress, counter, total))
                }
                PutRecipeService.MSG_PREPARING_FOR_UPLOADING -> {
                    serviceUploadingStatus.value = StatusBox(PutRecipeService.MSG_PREPARING_FOR_UPLOADING)
                }
                PutRecipeService.MSG_START_STORING_RECIPE_TO_DB -> {
                    serviceUploadingStatus.value = StatusBox(PutRecipeService.MSG_START_STORING_RECIPE_TO_DB)
                }
                PutRecipeService.MSG_EXTRACT_IMAGES_FROM_RECIPE_CONTENT -> {
                    serviceUploadingStatus.value = StatusBox(PutRecipeService.MSG_EXTRACT_IMAGES_FROM_RECIPE_CONTENT)
                }
                PutRecipeService.MSG_OPTIMIZING_IMAGES_BEFORE_UPLOADING -> {
                    serviceUploadingStatus.value = StatusBox(PutRecipeService.MSG_OPTIMIZING_IMAGES_BEFORE_UPLOADING)
                }

                PutRecipeService.MSG_START_UPLOADING_IMAGES ->{
                    serviceUploadingStatus.value = StatusBox(PutRecipeService.MSG_START_UPLOADING_IMAGES)
                }

                PutRecipeService.MSG_STORE_RECIPE_TO_DB_SUCCESS -> {
                    serviceUploadingStatus.value = StatusBox(PutRecipeService.MSG_STORE_RECIPE_TO_DB_SUCCESS)
                }
                PutRecipeService.MSG_STORE_RECIPE_TO_DB_FAILED -> {
                    serviceUploadingStatus.value = StatusBox(PutRecipeService.MSG_STORE_RECIPE_TO_DB_FAILED)
                }
                PutRecipeService.MSG_UPDATE_NEW_CATEGORIES ->{
                    serviceUploadingStatus.value = StatusBox(PutRecipeService.MSG_UPDATE_NEW_CATEGORIES)
                }
                PutRecipeService.MSG_PUT_NEW_TAGS -> {
                    serviceUploadingStatus.value = StatusBox(PutRecipeService.MSG_PUT_NEW_TAGS)
                }
                PutRecipeService.MSG_STORE_RECIPE_TOTALLY_FINISHED ->{
                    serviceUploadingStatus.value = StatusBox(PutRecipeService.MSG_STORE_RECIPE_TOTALLY_FINISHED)
                }
                else -> super.handleMessage(msg)
            }
        }
    }
}