package com.example.linh.vietkitchen.ui.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import androidx.core.app.NotificationCompat
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.domain.command.RequestRecipeCommand
import com.example.linh.vietkitchen.domain.command.UpdateRecipeCommand
import com.example.linh.vietkitchen.extension.deleteAwait
import com.example.linh.vietkitchen.extension.putBytesAwait
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.extension.attractUrlFromAnnotation
import com.example.linh.vietkitchen.ui.GlideApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import timber.log.Timber

class ReStructureImageFileService : BaseService() {
    private lateinit var nn: NotificationManager
    private val storageRecipeRef by lazy { FirebaseStorage.getInstance().reference.child("images/recipes/")}
    private val requestRecipeCommand by lazy { RequestRecipeCommand(limit = 10_000_000) }
    private val updateRecipeCommand by lazy { UpdateRecipeCommand() }
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private val messenger = Messenger(IncomingHandler())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        nn = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        retrieveAllRecipe()
    }

    override fun onBind(intent: Intent?): IBinder = messenger.binder
    inner class LocalBinder : Binder() {
        fun bind(): ReStructureImageFileService = this@ReStructureImageFileService
    }

    /**
     * Handler of incoming messages from clients.
     */
    @SuppressLint("HandlerLeak")
    internal inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }

    private fun startInForeground(message: String) {
//        val notificationIntent = Intent(this, WorkoutActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_bep_viet_launcher)
                .setContentTitle("Re-structure image Files")
                .setContentText(message)
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

    private fun retrieveAllRecipe(){
        startInForeground("retrieve all recipes")
        launchDataLoad{
            val listRecipes = withIoContext {
                requestRecipeCommand.execute(this).data

            }
            listRecipes?.let {
                restructureImageFileInFireStorage(it)
            }
        }
    }

    private fun restructureImageFileInFireStorage(listRecipes: List<Recipe>) {
        startInForeground("restructure firestorage images")
        launchDataLoad {
            listRecipes.forEachIndexed { index, recipe ->
                startInForeground("restructure firestorage images ${index +1}/${listRecipes.count()}")
                if (index <= 0) {
                    withIoContext {
                        val newRecipe = with(recipe) {
                            val recipeImageStorage = storageRecipeRef.child(this.id!!)

                            thumbUrl = thumbUrl.let {
                                moveFile(recipeImageStorage, thumbUrl)!!
                            }

                            imageUrl = imageUrl.let {
                                moveFile(recipeImageStorage, imageUrl)!!
                            }

                            preparation.attractUrlFromAnnotation()?.forEachIndexed { _, s ->
                                val reUploadUrl = moveFile(recipeImageStorage, s)
                                reUploadUrl?.let {
                                    preparation.replace(s, reUploadUrl)
                                }
                            }
                            processing.attractUrlFromAnnotation()?.forEachIndexed { _, s ->
                                val reUploadUrl = moveFile(recipeImageStorage, s)
                                reUploadUrl?.let {
                                    processing.replace(s, reUploadUrl)
                                }
                            }
                            this
                        }
                        updateRecipeCommand.recipe = newRecipe
                        updateRecipeCommand.execute(this)
                        Timber.d("re-uploaded ${recipe.name}")
                    }
                }
            }
        }
        startInForeground("restructure firestorage images finished")
        stopForegroundService()
    }

    private suspend fun moveFile(recipeImageStorage: StorageReference, originUrl: String): String? {
        val bytes = downloadFile(originUrl)
        return bytes?.let {
            val fileName = Uri.parse(originUrl).lastPathSegment!!.split("/").last()
            val removeURi = uploadFile(recipeImageStorage, fileName, it)
            deleteImageFile(originUrl)
            removeURi
        }
    }

    private suspend fun downloadFile(origin: String): ByteArray? {
        return GlideApp.with(this)
                .`as`(ByteArray::class.java)
                .load(origin)
                .submit()
                .get()
    }

    private suspend fun uploadFile(recipeImageStorage: StorageReference, destination: String, resource: ByteArray): String {
        val fileName = destination.split("/").last()
        val ref = recipeImageStorage.child(fileName)
        return ref.putBytesAwait(resource).data!!
    }

    private suspend fun deleteImageFile(uri: String){
        FirebaseStorage.getInstance().getReferenceFromUrl(uri).deleteAwait()
    }
}