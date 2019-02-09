package com.example.linh.vietkitchen.ui.service

import android.content.Intent
import timber.log.Timber
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.net.Uri
import android.os.*
import androidx.core.app.NotificationCompat
import com.example.linh.vietkitchen.R
import android.os.Messenger
import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.command.*
import com.example.linh.vietkitchen.extension.toMapOfStringBoolean
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.util.ImageOptimizationUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


const val NOTIFICATION_CHANNEL_ID = "50"
const val NOTIFICATION_CHANNEL_NAME = "upload"
const val NOTIFICATION_ID = 51
const val NOTIFICATION_CHANNEL_DESC = "NOTIFICATION_CHANNEL_DESC"

class PutRecipeService : BaseService() {
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

        const val MSG_PREPARING_FOR_UPLOADING = 13
        const val MSG_UPLOAD_RECIPE = 3
        const val MSG_UPLOAD_IMAGE_PROGRESS = 4
        const val MSG_START_STORING_RECIPE_TO_DB = 5
        const val MSG_OPTIMIZING_IMAGES_BEFORE_UPLOADING = 8
        const val MSG_STORE_RECIPE_TO_DB_SUCCESS = 6
        const val MSG_STORE_RECIPE_TO_DB_FAILED = 7
        const val MSG_EXTRACT_IMAGES_FROM_RECIPE_CONTENT = 11
        const val MSG_START_UPLOADING_IMAGES = 9
        const val MSG_UPDATE_NEW_CATEGORIES = 10
        const val MSG_PUT_NEW_TAGS = 12
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
//    private val binder = LocalBinder()
    private val nn by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    /** Keeps track of all current registered clients.  */
    private var clients = ArrayList<Messenger>()
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private val messenger = Messenger(IncomingHandler())

    @Inject lateinit var recipeMapper: RecipeMapper
    @Inject lateinit var categoryMapper: CategoryMapper
    @Inject lateinit var putRecipeCommand: PutRecipeCommand
    @Inject lateinit var updateRecipeCommand: UpdateRecipeCommand
    @Inject lateinit var uploadImageCommand: UploadImageCommand
    @Inject lateinit var putTagsCommand: PutTagsCommand
    @Inject lateinit var updateCategoriesCommand: UpdateCategoriesCommand

    private val imageCompressionUtil by lazy { ImageOptimizationUtil() }

    private var shouldShowNotification = false
    private var uploadingCounter = 1
    private var totalImageFiles = 0

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
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        // Cancel the persistent notification.
        EventBus.getDefault().unregister(this)
        nn.cancel(NOTIFICATION_ID)
        super.onDestroy()
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
                    shouldShowNotification = false
                    uploadingCounter = 1
                    totalImageFiles = 0
                    putRecipe(recipe, listImages, drawerNav, listNewTags)
                }
                MSG_SHOW_PROGRESS_NOTIFICATION -> {
                    shouldShowNotification = true
                }
                else -> super.handleMessage(msg)
            }
        }
    }

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
                .setSmallIcon(R.mipmap.ic_bep_viet_launcher)
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
                .setSmallIcon(R.mipmap.ic_bep_viet_launcher)
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
                .setSmallIcon(R.mipmap.ic_bep_viet_launcher)
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
        launchDataLoad {
            withIoContext{
                val recipeId = storeRecipeToDb(recipe)
                val listOptimizedImagesPaths = optimizeImages(listImages)
                listOptimizedImagesPaths.forEach { it.apply { this.remoteDir = recipeId } }
                val uploadImageResponse = uploadImages(listOptimizedImagesPaths)
                updateRecipeWithRemoteImageUri(recipe, recipeId, uploadImageResponse.data!!)
                val wasUpdateCategorySuccess = updateCategories(drawerNav).data!!
                if (wasUpdateCategorySuccess) VietKitchenApp.category.postValue(drawerNav)
                listNewTags?.let { putNewTags(it) }
                null
            }
            sendMessageToClients(MSG_STORE_RECIPE_TOTALLY_FINISHED)
//            sendMessageToClients(MSG_STORE_RECIPE_TO_DB_FAILED)
        }
    }

    private suspend fun storeRecipeToDb(recipe: Recipe): String {
        Timber.d("store the recipe into remote db")
        val recipeDomain = recipeMapper.toDomain(recipe)
        putRecipeCommand.recipe = recipeDomain
        val id = putRecipeCommand.execute(this)
        sendMessageToClients(MSG_STORE_RECIPE_TO_DB_SUCCESS)
        if (shouldShowNotification) {
            updateUploadNotification("the recipe was put into remote server success")
        }
        Timber.d("a recipe was put into firebase server")
        return id.data!!
    }

    private suspend fun updateRecipeWithRemoteImageUri(recipe: Recipe, id: String, remoteUris: List<ImageUpload>): Response<Boolean> {
        Timber.d("updateRecipeWithRemoteImageUri")
        val newRecipe = recipe.let {
            Recipe(id, it.name, it.intro, it.ingredient, it.spice, it.preparation, it.processing,
                    it.notes, it.categories, it.tags, it.thumbUrl, it.imageUrl, it.hasLiked)
        }
        remoteUris.forEach {
            updateRemoteUriToRecipe(newRecipe, it.originalPath, it.remotePath!!)
        }
        updateRecipeCommand.recipe = recipeMapper.toDomain(newRecipe)
        return updateRecipeCommand.execute(this)
    }

    private suspend fun putNewTags(tags: List<String>) {
        if(tags.isNullOrEmpty())
        Timber.d("put new tags into db")
        sendMessageToClients(MSG_PUT_NEW_TAGS)
        putTagsCommand.tags = tags.toMapOfStringBoolean()
        putTagsCommand.execute(this)
        Timber.d("just put ${tags.size} tags successfully")
    }

    private suspend fun updateCategories(categories: List<DrawerNavGroupItem>): Response<Boolean> {
        Timber.d("update categories")
        sendMessageToClients(MSG_UPDATE_NEW_CATEGORIES)
        updateCategoriesCommand.listCatGroup = categoryMapper.toDomain(categories)
        return updateCategoriesCommand.execute(this)
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
                val optThumb = ImageUpload("thumb_${optimizedThumbPath.name}", uri.toString(), optimizedThumbPath.absolutePath)
                result.add(optThumb)
            }
        }
        return result
    }



    private suspend fun uploadImages(multipartFiles: List<ImageUpload>): Response<List<ImageUpload>> {
        Timber.d("start uploading images")
        sendMessageToClients(MSG_START_UPLOADING_IMAGES)
        totalImageFiles = multipartFiles.count()
        uploadImageCommand.multiPartFileMap = multipartFiles
        return uploadImageCommand.execute(this)
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

    /**
     * currently coroutines doesn't support multiple times to resume
     * so that we have to temporarily use EventBus to dispatch uploading-progress
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEventBus(uploadingStatus: ImageUpload){
        Timber.d("****************************")
        Timber.d("uploading ${uploadingStatus.remotePath}")
        Timber.d("uploading ${uploadingStatus.progress}")
        Timber.d("****************************")
        val bundle = Bundle()
        if(uploadingStatus.progress == 100 && !uploadingStatus.remotePath.isNullOrBlank() &&
                uploadingCounter < totalImageFiles){
            uploadingCounter++
        }
        bundle.putInt(BK_UPLOAD_PROGRESS, uploadingStatus.progress)
        bundle.putInt(BK_UPLOAD_COUNTER, uploadingCounter)
        bundle.putInt(BK_UPLOAD_TOTAL, totalImageFiles)
        sendMessageToClients(MSG_UPLOAD_IMAGE_PROGRESS, bundle)
        if (shouldShowNotification) {
            updateProgressNotification(totalImageFiles, uploadingCounter, uploadingStatus.progress)
        }
    }
}