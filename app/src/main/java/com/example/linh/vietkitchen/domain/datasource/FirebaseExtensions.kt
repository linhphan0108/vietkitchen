package com.example.linh.vietkitchen.domain.datasource

import android.net.Uri
import com.example.linh.vietkitchen.R.string.progress
import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.util.ResponseCode
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun Query.addValueEventListenerAwait(): DataSnapshot {
    return suspendCoroutine { continuation ->
        addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                continuation.resumeWithException(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                continuation.resume(p0)
            }
        })
    }
}

suspend fun Query.addListenerForSingleValueEventAwait(): DataSnapshot {
    return suspendCoroutine { continuation ->
        addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                continuation.resumeWithException(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                continuation.resume(p0)
            }
        })
    }
}

suspend fun DatabaseReference.setValueAwait(any: Any?): String{
    return suspendCoroutine { continuation ->
        setValue(any){databaseError, databaseReference ->
            if (databaseError != null){
                continuation.resumeWithException(databaseError.toException())
            }else{
                continuation.resume(databaseReference.key.toString())
            }
        }
    }
}

suspend fun DatabaseReference.removeValueAwait(): Boolean{
    return suspendCoroutine { continuation ->
        removeValue()
                .addOnCompleteListener {
                    continuation.resume(it.isSuccessful)
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
    }
}

suspend fun StorageReference.deleteAwait(): Response<Boolean>{
    return suspendCoroutine { continuation ->
        delete().addOnCompleteListener {task ->
            if (task.isSuccessful) {
                continuation.resume(Response(ResponseCode.RESPONSE_SUCCESS, true))
            } else {
                // Handle failures
                continuation.resumeWithException(task.exception!!)
            }
        }
    }
}

suspend fun StorageReference.putImageAwait(image: ImageUpload): Response<ImageUpload>{
    return suspendCoroutine { continuation ->
        putFile(Uri.fromFile(File(image.optimizedPath)))
                .addOnProgressListener { taskSnapshot ->
                    val progress: Int = (100 * taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount).toInt()
                    val message = ImageUpload(image.fileName, image.originalPath, image.optimizedPath, progress)
                    EventBus.getDefault().post(message)
                }
                .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation downloadUrl
                })
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.d("uploaded ${image.originalPath} into storage ")
                        val remoteUri = (task.result as Uri).toString()
                        image.progress = 100
                        image.remotePath = remoteUri
                        EventBus.getDefault().post(image)
                        continuation.resume(Response(ResponseCode.RESPONSE_SUCCESS, image))
                    } else {
                        // Handle failures
                        continuation.resumeWithException(task.exception!!)
                    }
                }
    }
}

suspend fun StorageReference.putBytesAwait(resource: ByteArray): Response<String>{
    return suspendCoroutine { continuation ->
        putBytes(resource)
//                .addOnProgressListener { taskSnapshot ->
//                    val progress: Int = (100 * taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount).toInt()
//                    val message = ImageUpload(image.fileName, image.originalPath, image.optimizedPath, progress)
//                    emitter.onNext(message)
//                }
                .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation downloadUrl
                })
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val remoteUri = (task.result as Uri).toString()
                        continuation.resume(Response(ResponseCode.RESPONSE_SUCCESS, remoteUri))
                    } else {
                        // Handle failures
                        continuation.resumeWithException(task.exception!!)
                    }
                }
    }
}