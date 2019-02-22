package com.example.linh.vietkitchen.extension

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.data.response.ApiResponse
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.File

fun Query.addValueEventListenerAwait(): LiveData<ApiResponse<DataSnapshot>> {
    val mutableLiveData = MutableLiveData<ApiResponse<DataSnapshot>>()
    addValueEventListener(object: ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            mutableLiveData.value = ApiResponse.createError(p0.toException())
        }

        override fun onDataChange(p0: DataSnapshot) {
            val response = ApiResponse.createSuccess(p0)
            mutableLiveData.value = response
        }
    })
    return mutableLiveData
}

fun Query.addListenerForSingleValueEventAwait(): LiveData<ApiResponse<DataSnapshot>> {
    val mutableLiveData = MutableLiveData<ApiResponse<DataSnapshot>>()
    addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            mutableLiveData.postValue(ApiResponse.createError(p0.toException()))
        }

        override fun onDataChange(p0: DataSnapshot) {
            val response = ApiResponse.createSuccess(p0)
            mutableLiveData.postValue(response)
        }
    })
    return mutableLiveData
}

fun DatabaseReference.setValueAwait(any: Any?): LiveData<ApiResponse<String>>{
    val mutableLiveData = MutableLiveData<ApiResponse<String>>()
    setValue(any){databaseError, databaseReference ->
            if (databaseError == null){
                val response = ApiResponse.createSuccess(databaseReference.key)
                mutableLiveData.value = response
            }else{
                mutableLiveData.value = ApiResponse.createError(databaseError.toException())
            }
        }
    return mutableLiveData
}

fun DatabaseReference.removeValueAwait(): LiveData<ApiResponse<Boolean>>{
    val mutableLiveData = MutableLiveData<ApiResponse<Boolean>>()
    removeValue()
            .addOnCompleteListener {
                val response = ApiResponse.createSuccess(it.isSuccessful)
                mutableLiveData.value = response
            }.addOnFailureListener {
                mutableLiveData.value = ApiResponse.createError(it)
            }
    return mutableLiveData
}

fun StorageReference.deleteAwait(): LiveData<ApiResponse<Boolean>> {
    val mutableLiveData = MutableLiveData<ApiResponse<Boolean>>()
    delete().addOnCompleteListener { task ->
        val response = if (task.isSuccessful) {
            ApiResponse.createSuccess(true)
        } else {
            ApiResponse.createError(task.exception!!)
        }
        mutableLiveData.value = response
    }
    return mutableLiveData
}

fun StorageReference.putImageAwait(image: ImageUpload): LiveData<ApiResponse<ImageUpload>>{
    val mutableLiveData = MutableLiveData<ApiResponse<ImageUpload>>()
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
                val response = if (task.isSuccessful) {
                    Timber.d("uploaded ${image.originalPath} into storage ")
                    val remoteUri = (task.result as Uri).toString()
                    image.progress = 100
                    image.remotePath = remoteUri
                    EventBus.getDefault().post(image)
                    ApiResponse.createSuccess(image)
                } else {
                    // Handle failures
                    ApiResponse.createError(task.exception!!)
                }
                mutableLiveData.value = response
            }
    return mutableLiveData
}

fun StorageReference.putBytesAwait(resource: ByteArray): LiveData<ApiResponse<String>>{
    val mutableLiveData = MutableLiveData<ApiResponse<String>>()
    putBytes(resource)
//                .addOnProgressListener { taskSnapshot ->
//                    val progress: Int = (100 * taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount).toInt()
//                    val data = ImageUpload(image.fileName, image.originalPath, image.optimizedPath, progress)
//                    emitter.onNext(data)
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
                val response = if (task.isSuccessful) {
                    val remoteUri = (task.result as Uri).toString()
                    ApiResponse.createSuccess(remoteUri)
                } else {
                    // Handle failures
                    ApiResponse.createError(task.exception!!)
                }
                mutableLiveData.value = response
            }
    return mutableLiveData
}