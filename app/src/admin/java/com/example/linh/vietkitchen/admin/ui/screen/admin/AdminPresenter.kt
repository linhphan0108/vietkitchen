package com.example.linh.vietkitchen.admin.ui.screen.admin

import com.example.linh.vietkitchen.domain.command.PutTagsCommand
import com.example.linh.vietkitchen.domain.command.RequestTagsCommand
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.domain.model.Recipe as RecipeDomain
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import com.example.linh.vietkitchen.ui.screen.detailActivity.RecipeDetailActivity
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import com.example.linh.vietkitchen.extension.generateAnnotationSpan
import com.example.linh.vietkitchen.ui.service.PutRecipeService
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.net.Uri
import android.os.*
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.toListOfStringOfKey
import com.example.linh.vietkitchen.extension.toMapOfStringBoolean
import com.example.linh.vietkitchen.ui.dialog.ProgressDialog
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem


class AdminPresenter(private val putTagsCommand: PutTagsCommand = PutTagsCommand(),
                     private val requestTagsCommand: RequestTagsCommand = RequestTagsCommand())
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

    private lateinit var progressDialog: ProgressDialog

    override fun attachView(view: AdminContractView) {
        super.attachView(view)
        progressDialog = ProgressDialog()
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
            val data = Recipe(id, name, intro, ingredient, spice, charPreparation, charProcess,
                    notes, categories, tags,thumbUrl, imageUrl, false)
            val intent = RecipeDetailActivity.createIntent(context, "", data)
            context?.startActivity(intent)
        }
    }

    override fun putARecipe(recipe: Recipe, listImagesUri: MutableList<Uri>) {
        showProgressDialog()
        val newTags = recipe.tags?.filterNot {
            listTagsOnServer.contains(it)
        }
        if(newTags != null && newTags.isNotEmpty()) putNewTags(newTags)

        doBindService(recipe, listImagesUri)
    }

    override fun putNewTags(tags: List<String>) {
        putTagsCommand.tags = tags.toMapOfStringBoolean()
        compositeDisposable.add(putTagsCommand.execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    Timber.d("just put ${tags.size} tags successfully")
                }, {
                    Timber.e(it)
                })
        )
    }


    override fun getTags() {
        compositeDisposable.add(requestTagsCommand.execute()
                .map { it.toListOfStringOfKey() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    listTagsOnServer = it
                    viewContract?.onGetTagsSuccess(it)
                }, {
                    viewContract?.onGetTagsFailed(it.message)
                },{
                    viewContract?.onGetTagsSuccess(listOf())
                }))

    }

    private fun showProgressDialog(){
        if (!progressDialog.isVisible)
            progressDialog.show(activity?.supportFragmentManager, ProgressDialog::class.java.name)
    }

    private fun doBindService(recipe: Recipe, listImagesUri: MutableList<Uri>) {
        // Attempts to establish a connection with the service.  We use an
        // explicit class name because we want a specific service
        // implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other
        // applications).
        if (context == null) return
        val intent = PutRecipeService.createIntent(context!!)
        mConnection.recipe = recipe
        mConnection.listImagesUri = listImagesUri
        if (context?.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)!!) {
            isBounded = true
        } else {
            Timber.e("Error: The requested service doesn't exist, or this client isn't allowed access to it.")
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

    private val mConnection = object : ServiceConnection {
        lateinit var recipe: Recipe
        lateinit var listImagesUri: MutableList<Uri>
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            serviceMessenger = Messenger(service)

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
                bundle.putParcelableArray(PutRecipeService.BK_LIST_IMAGES_URI, listImagesUri.toTypedArray())
                msg.data = bundle
                serviceMessenger?.send(msg)
//
            } catch (e: RemoteException) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }


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
    internal inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                PutRecipeService.MSG_UPLOAD_IMAGE_PROGRESS -> {
                    val bundle = msg.data
                    val progress = bundle.getInt(PutRecipeService.BK_UPLOAD_PROGRESS)
                    val counter = bundle.getInt(PutRecipeService.BK_UPLOAD_COUNTER)
                    val total = bundle.getInt(PutRecipeService.BK_UPLOAD_TOTAL)
                    if (progressDialog.isVisible) {
                        progressDialog.updateProgress(total, counter, progress)
                    }
                }
                PutRecipeService.MSG_START_STORING_RECIPE_TO_DB -> {
                    if (progressDialog.isVisible) {
                        progressDialog.updateMessage(getStringRes(R.string.msg_start_storing_recipe))
                    }
                }
                PutRecipeService.MSG_OPTIMIZING_IMAGE -> {
                    if (progressDialog.isVisible) {
                        progressDialog.updateMessage(getStringRes(R.string.msg_optimizing_images))
                    }
                }
                PutRecipeService.MSG_STORE_RECIPE_TO_DB_SUCCESS -> {
                    if (progressDialog.isVisible) {
                        progressDialog.updateMessage(getStringRes(R.string.msg_store_recipe_success))
                    }
                }
                PutRecipeService.MSG_STORE_RECIPE_TO_DB_FAILED -> {
                    if (progressDialog.isVisible) {
                        progressDialog.updateMessage(getStringRes(R.string.msg_store_recipe_failed))
                    }
                }
                else -> super.handleMessage(msg)
            }
        }
    }
}