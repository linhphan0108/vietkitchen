package com.example.linh.vietkitchen.ui.service

import android.app.Service
import android.content.Intent
import timber.log.Timber
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.net.Uri
import android.os.*
import android.support.v4.app.NotificationCompat
import com.example.linh.vietkitchen.R
import android.os.Messenger
import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.domain.command.PutRecipeCommand
import com.example.linh.vietkitchen.domain.command.PutTagsCommand
import com.example.linh.vietkitchen.domain.command.UpdateCategoriesCommand
import com.example.linh.vietkitchen.domain.command.UploadImageCommand
import com.example.linh.vietkitchen.extension.attractUrlFromAnnotation
import com.example.linh.vietkitchen.extension.toMapOfStringBoolean
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.util.ImageOptimizationUtil
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers


const val NOTIFICATION_CHANNEL_ID = "50"
const val NOTIFICATION_CHANNEL_NAME = "upload"
const val NOTIFICATION_ID = 51
const val NOTIFICATION_CHANNEL_DESC = "NOTIFICATION_CHANNEL_DESC"

class PutRecipeService : Service() {
    companion object {
        private const val EXTRA_BUNDLE = "EXTRA_BUNDLE"
        const val BK_RECIPE_TO_UPLOAD = "BK_RECIPE_TO_UPLOAD"
        const val BK_LIST_IMAGES_URI = "BK_LIST_IMAGES_URI"
        const val BK_LIST_NEW_TAGS = "BK_LIST_NEW_TAGS"
        const val BK_NEW_CATEGORIES = "BK_NEW_CATEGORIES"
        const val BK_UPLOAD_PROGRESS = "BK_UPLOAD_PROGRESS"
        const val BK_UPLOAD_COUNTER = "BK_UPLOAD_COUNTER"
        const val BK_UPLOAD_TOTAL = "BK_UPLOAD_TOTAL"
        /**
         * Command to the service to register a client, receiving callbacks
         * from the service.  The Message's replyTo field must be a Messenger of
         * the client where callbacks should be sent.
         */
        const val MSG_REGISTER_CLIENT = 1

        /**
         * Command to the service to unregister a client, ot stop receiving callbacks
         * from the service.  The Message's replyTo field must be a Messenger of
         * the client as previously given with MSG_REGISTER_CLIENT.
         */
        const val MSG_UNREGISTER_CLIENT = 2

        const val MSG_UPLOAD_RECIPE = 3
        const val MSG_UPLOAD_IMAGE_PROGRESS = 4
        const val MSG_START_STORING_RECIPE_TO_DB = 5
        const val MSG_OPTIMIZING_IMAGES_BEFORE_UPLOADING = 8
        const val MSG_STORE_RECIPE_TO_DB_SUCCESS = 6
        const val MSG_STORE_RECIPE_TO_DB_FAILED = 7
        const val MSG_EXTRACT_IMAGES_FROM_RECIPE_CONTENT = 8
        const val MSG_START_UPLOADING_IMAGES = 9
        const val MSG_UPDATE_NEW_CATEGORIES = 10
        const val MSG_PUT_NEW_TAGS = 11
        const val MSG_STORE_RECIPE_TOTALLY_FINISHED = 99
        /**
         * command to show progress notification
         */
        const val MSG_SHOW_PROGRESS_NOTIFICATION = 5

        fun createIntent(context: Context, recipe: Recipe): Intent{
            val intent = Intent(context, PutRecipeService::class.java)
            val bundle = Bundle()
            intent.putExtra(EXTRA_BUNDLE, bundle)
            bundle.putParcelable(BK_RECIPE_TO_UPLOAD, recipe)
            return intent
        }

        fun createIntent(context: Context): Intent{
            return Intent(context, PutRecipeService::class.java)
        }

    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private val binder = LocalBinder()
    private lateinit var nn: NotificationManager
    /** Keeps track of all current registered clients.  */
    private var clients = ArrayList<Messenger>()
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private val messenger = Messenger(IncomingHandler())

    private lateinit var compositeDisposable: CompositeDisposable

    private lateinit var recipeMapper: RecipeMapper
    private lateinit var categoryMapper: CategoryMapper
    private lateinit var putRecipeCommand: PutRecipeCommand
    private lateinit var uploadImageCommand: UploadImageCommand
    private lateinit var imageCompressionUtil: ImageOptimizationUtil
    private lateinit var putTagsCommand: PutTagsCommand
    private lateinit var updateCategoriesCommand: UpdateCategoriesCommand

    private var shouldShowNotification = false

    /**
     * this method can not called if the service was started by call bindService()
     * instead of startService
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("Received start id $startId : $intent")
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        nn = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        compositeDisposable = CompositeDisposable()
        recipeMapper = RecipeMapper()
        categoryMapper = CategoryMapper()
        putRecipeCommand = PutRecipeCommand()
        uploadImageCommand = UploadImageCommand()
        putTagsCommand = PutTagsCommand()
        updateCategoriesCommand = UpdateCategoriesCommand()
        imageCompressionUtil = ImageOptimizationUtil()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the persistent notification.
        nn.cancel(NOTIFICATION_ID)
        compositeDisposable.clear()
    }

    override fun onBind(intent: Intent?): IBinder = messenger.binder
    inner class LocalBinder : Binder() {
        fun bind(): PutRecipeService = this@PutRecipeService
    }
    /**
     * Handler of incoming messages from clients.
     */
    internal inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_REGISTER_CLIENT -> clients.add(msg.replyTo)
                MSG_UNREGISTER_CLIENT -> clients.remove(msg.replyTo)
                MSG_UPLOAD_RECIPE -> {
                    val bundle = msg.data
                    val recipe: Recipe = bundle.getParcelable(BK_RECIPE_TO_UPLOAD)!!
                    val listImages: List<Uri>  = bundle.getParcelableArrayList(BK_LIST_IMAGES_URI)!!
                    val listNewTags: List<String>? = bundle.getStringArrayList(BK_LIST_NEW_TAGS)
                    val drawerNav: List<DrawerNavGroupItem> = bundle.getParcelableArrayList(BK_NEW_CATEGORIES)!!
                    putRecipe(recipe, listImages, drawerNav, listNewTags)
                }
                MSG_SHOW_PROGRESS_NOTIFICATION -> {
                    shouldShowNotification = true
                }
//                MSG_SET_VALUE -> {
//                    mValue = msg.arg1
//                    for (i in clients.size() - 1 downTo 0) {
//                        try {
//                            clients[i].send(Message.obtain(null,
//                                    MSG_SET_VALUE, mValue, 0))
//                        } catch (e: RemoteException) {
//                            // The client is dead.  Remove it from the list;
//                            // we are going through the list from back to front
//                            // so this is safe to do inside the loop.
//                            clients.remove(i)
//                        }
//
//                    }
//                }
                else -> super.handleMessage(msg)
            }
        }
    }

    //======== inner methods =======================================================================
    private fun sendMessageToClients(what: Int, bundle: Bundle? = null){
        clients.forEach { clientMessenger ->
            val msg = Message.obtain(null, what)
            bundle?.let { msg.data = bundle }
            clientMessenger.send(msg)
        }
    }

    private fun startInForeground() {
//        val notificationIntent = Intent(this, WorkoutActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("TEST")
                .setContentText("HELLO")
                .setTicker("TICKER")
//                .setContentIntent(pendingIntent)
        val notification = builder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = NOTIFICATION_CHANNEL_DESC
            nn.createNotificationChannel(channel)
        }
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun stopForegroundService() {
        Timber.d("Stop foreground service.")
        // Stop foreground service and remove the notification.
        stopForeground(true)

        // Stop the foreground service.
        stopSelf()
    }

    private fun updateUploadNotification(message: String){
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("create new recipe")
                .setContentText(message)
                .setTicker("TICKER")
        val notification = builder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = NOTIFICATION_CHANNEL_DESC
            nn.createNotificationChannel(channel)
        }
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun updateProgressNotification(totalFiles: Int, counter: Int, progress: Int) {
        val contentText = "$progress% - $counter/$totalFiles files"
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("uploading images")
                .setContentText(contentText)
                .setTicker("TICKER")
                .setProgress(100, progress, false)
        val notification = builder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = NOTIFICATION_CHANNEL_DESC
            nn.createNotificationChannel(channel)
        }
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun putRecipe(recipe: Recipe, listImages: List<Uri>, drawerNav: List<DrawerNavGroupItem>, listNewTags: List<String>?) {
        sendMessageToClients(MSG_START_STORING_RECIPE_TO_DB)
        compositeDisposable.add(Flowable.just(recipe)
                .map { extractImagePaths(it, listImages) }
                .map { optimizeImages(it) }
                .flatMap {
                    uploadImages(it)
                }.flatMap {multipartFiles ->
                    storeRecipeToDb(recipe, multipartFiles)
                }
                .flatMapCompletable {
                    Completable.mergeArray(updateCategories(drawerNav), putNewTags(listNewTags))
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    sendMessageToClients(MSG_STORE_RECIPE_TOTALLY_FINISHED)
                }, {e ->
                    sendMessageToClients(MSG_STORE_RECIPE_TO_DB_FAILED)
                    Timber.e(e)
                }))
    }

    private fun storeRecipeToDb(recipe: Recipe, multipartFiles: List<ImageUpload>): Flowable<String> {
        Timber.d("store the recipe into remote db")
        return Flowable.just(recipe)
                .map {r ->
                    multipartFiles.forEach {
                        updateRemoteUriToRecipe(r, it.originalPath, it.remotePath!!)
                    }
                    recipeMapper.toDomain(r)
                }.flatMap {recipeUI ->
                    putRecipeCommand.recipe = recipeUI
                    putRecipeCommand.execute()
                            .doOnComplete {
                                sendMessageToClients(MSG_STORE_RECIPE_TO_DB_SUCCESS)
                                if (shouldShowNotification) {
                                    updateUploadNotification("the recipe was put into remote server success")
                                }
                                Timber.d("a recipe was put into firebase server")
                            }
                }
    }

    private fun putNewTags(tags: List<String>?): Completable {
        if(tags.isNullOrEmpty()) return Completable.complete()
        Timber.d("put new tags into db")
        sendMessageToClients(MSG_PUT_NEW_TAGS)
        putTagsCommand.tags = tags.toMapOfStringBoolean()
        return putTagsCommand.execute()
                .doOnComplete {
                    Timber.d("just put ${tags.size} tags successfully")
                }.doOnError {
                    Timber.e(it)
                }
    }

    private fun updateCategories(drawerNav: List<DrawerNavGroupItem>): Completable{
        Timber.d("update categories")
        sendMessageToClients(MSG_UPDATE_NEW_CATEGORIES)
        updateCategoriesCommand.listCatGroup = categoryMapper.toDomain(drawerNav)
        return updateCategoriesCommand.execute()
    }

    private fun optimizeImages(extractImageUris: List<Uri>): List<ImageUpload>{
        Timber.d("optimizing images before uploading")
        sendMessageToClients(MSG_OPTIMIZING_IMAGES_BEFORE_UPLOADING)
        val result = mutableListOf<ImageUpload>()
        extractImageUris.forEachIndexed { index, uri ->
            val optimizedPath = imageCompressionUtil.optimize(this, uri)
            val opt = ImageUpload(optimizedPath.name, uri.toString(), optimizedPath.absolutePath)
            result.add(opt)

            //create thumb image
            //make sure that the first added uri is the image uri
            if(index == 0){
                val optimizedThumbPath = imageCompressionUtil.optimizeThumbImage(this, uri)
                val optThumb = ImageUpload(optimizedThumbPath.name, uri.toString(), optimizedThumbPath.absolutePath)
                result.add(optThumb)
            }
        }
        return result
    }

    private fun extractImagePaths(recipe: Recipe, listImages: List<Uri>): List<Uri> {
        Timber.d("extract Image path from the recipe's content")
        sendMessageToClients(MSG_EXTRACT_IMAGES_FROM_RECIPE_CONTENT)
        val multiPartFiles = mutableListOf<String>()
        with(recipe){
            if (imageUrl.isNotBlank()) multiPartFiles.add(imageUrl)
            preparation.attractUrlFromAnnotation()?.forEachIndexed { _, s ->
                multiPartFiles.add(s)
            }
            processing.attractUrlFromAnnotation()?.forEachIndexed { _, s ->
                multiPartFiles.add(s)
            }
        }
        return listImages.filter {uri ->
            multiPartFiles.contains(uri.toString())
        }
    }

    private fun uploadImages(multipartFiles: List<ImageUpload>): Flowable<List<ImageUpload>> {
        Timber.d("start uploading images")
        sendMessageToClients(MSG_START_UPLOADING_IMAGES)
        val result = mutableListOf<ImageUpload>()
        val totalFiles = multipartFiles.size
        var counter = 1
        uploadImageCommand.multiPartFileMap = multipartFiles
        return uploadImageCommand.execute()
                .doOnNext {message ->
                    val bundle = Bundle()
                    bundle.putInt(BK_UPLOAD_PROGRESS, message.progress)
                    bundle.putInt(BK_UPLOAD_COUNTER, counter)
                    bundle.putInt(BK_UPLOAD_TOTAL, totalFiles)
                    sendMessageToClients(MSG_UPLOAD_IMAGE_PROGRESS, bundle)
                    if (shouldShowNotification) {
                        updateProgressNotification(totalFiles, counter, message.progress)
                    }
                    Timber.d("****************************")
                    Timber.d("uploading ${message.originalPath}")
                    Timber.d("uploading ${message.progress}")
                    Timber.d("****************************")
                }.filter {message -> message.progress == 100 && !message.remotePath.isNullOrBlank()}
                .doOnNext {message ->
                    result.add(message)
                    counter++
                }
                .doOnComplete {
                    Timber.d("upload multipartFiles go into doOnComplete()")
                }.toList().toFlowable()
    }

    private fun addMultipartFileUri(multiPartFileMap: MutableMap<String, String>, path: String){
        val timeStamp = System.currentTimeMillis().toString()
        val arr = path.split(".")
        var ex = arr[arr.size-1]
        ex = if (ex.startsWith(".")) ex else ".$ex"
        val fileName = "$timeStamp-${multiPartFileMap.size}$ex"
        multiPartFileMap[fileName] = path
    }

    private fun updateRemoteUriToRecipe(recipe: Recipe, sLocalUri: String, sDownloadUri: String) {
        with(recipe) {
            when {
                sLocalUri == imageUrl -> imageUrl = sDownloadUri
                sLocalUri == thumbUrl -> thumbUrl = sDownloadUri
                preparation.contains(sLocalUri.toRegex()) -> {
                    preparation = preparation.replaceFirst(sLocalUri.toRegex(), sDownloadUri)
                    Timber.d("replace preparation")
                    Timber.d("preparation $preparation")
                }
                else -> {
                    processing = processing.replaceFirst(sLocalUri.toRegex(), sDownloadUri)
                    Timber.d("replace processing")
                    Timber.d("processing $preparation")
                }
            }
        }
    }
}