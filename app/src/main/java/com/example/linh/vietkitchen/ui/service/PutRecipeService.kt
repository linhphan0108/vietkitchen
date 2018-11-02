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
import com.example.linh.vietkitchen.domain.command.UploadImageCommand
import com.example.linh.vietkitchen.extension.attractUrlFromAnnotation
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.util.ImageOptimizationUtil
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
        const val MSG_OPTIMIZING_IMAGE = 8
        const val MSG_STORE_RECIPE_TO_DB_SUCCESS = 6
        const val MSG_STORE_RECIPE_TO_DB_FAILED = 7
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
    private lateinit var putRecipeCommand: PutRecipeCommand
    private lateinit var uploadImageCommand: UploadImageCommand
    private lateinit var imageCompressionUtil: ImageOptimizationUtil

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
        putRecipeCommand = PutRecipeCommand()
        uploadImageCommand = UploadImageCommand()
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
                    val recipe: Recipe = bundle.getParcelable(BK_RECIPE_TO_UPLOAD)
                    val listImages: MutableList<Uri>  = (bundle.getParcelableArray(BK_LIST_IMAGES_URI) as Array<Uri>).toMutableList()
                    putRecipe(recipe, listImages)
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

    private fun putRecipe(recipe: Recipe, listImages: MutableList<Uri>) {
        sendMessageToClients(MSG_START_STORING_RECIPE_TO_DB)
        compositeDisposable.add(Flowable.just(recipe)
                .map { extractImagePaths(it, listImages) }
                .map { optimizeImages(it) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ multipartFiles ->
                    uploadImages(multipartFiles){storeRecipeToDb(recipe, it)}
                }, { e -> Timber.e(e)}))
    }

    private fun storeRecipeToDb(recipe: Recipe, multipartFiles: List<ImageUpload>) {
        compositeDisposable.add(Flowable.just(recipe)
                .map {r ->
                    multipartFiles.forEach {
                        updateRemoteUriToRecipe(r, it.originalPath, it.remotePath!!)
                    }
                    recipeMapper.toDomain(r)
                }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ recipeUI ->
                    putRecipeCommand.recipe = recipeUI
                    compositeDisposable.add(putRecipeCommand.execute()
                            .observeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe ({
                                sendMessageToClients(MSG_STORE_RECIPE_TO_DB_SUCCESS)
                                if (shouldShowNotification) {
                                    updateUploadNotification("the recipe was put into remote server success")
                                }
                                Timber.d("a recipe was put into firebase server")
                            }, {e ->
                                sendMessageToClients(MSG_STORE_RECIPE_TO_DB_FAILED)
                                Timber.e(e)
                            }))
                }, {
                    Timber.e(it)
                }))
    }

    private fun optimizeImages(extractImageUris: List<Uri>): List<ImageUpload>{
        sendMessageToClients(MSG_OPTIMIZING_IMAGE)
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

    private fun extractImagePaths(recipe: Recipe, listImages: MutableList<Uri>): List<Uri> {
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

    private fun uploadImages(multipartFiles: List<ImageUpload>, completionCallback: (multipartFiles: List<ImageUpload>) -> Unit) {
        val result = mutableListOf<ImageUpload>()
        val totalFiles = multipartFiles.size
        var counter = 1
        uploadImageCommand.multiPartFileMap = multipartFiles
        compositeDisposable.add(uploadImageCommand.execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({message ->
                    if (message.progress == 100 && !message.remotePath.isNullOrBlank()) {
                        result.add(message)
                        counter++
                    }else{
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
                    }
                }, {
                    Timber.e(it)
                }, {
                    completionCallback(result)
                    toast("upload multipartFiles go into onComplete()")
                    Timber.d("upload multipartFiles go into onComplete()")
                }))
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