package com.example.linh.vietkitchen.data.cloud

import android.os.Handler
import androidx.lifecycle.LiveData
import com.google.firebase.database.*
import timber.log.Timber


class FirebaseQueryLiveData : LiveData<DataSnapshot> {
    private val query: Query
    private val listener = MyValueEventListener()
    private var listenerRemovePending = false
    private val handler = Handler()
    private val removeListener by lazy {
        Runnable {
//            this.query.removeEventListener(listener)
//            listenerRemovePending = false
        }
    }

    constructor(query: Query) : super() {
        this.query = query
    }

    constructor(ref: DatabaseReference): super() {
        this.query = ref
    }

    override fun onActive() {
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener)
        }
        else {
            query.addValueEventListener(listener)
        }
        listenerRemovePending = false
    }

    override fun onInactive() {
        // Listener removal is schedule on a two second delay
        handler.postDelayed(removeListener, 2000)
        listenerRemovePending = true
    }

    private inner class MyValueEventListener : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            this@FirebaseQueryLiveData.setValue(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Timber.e(databaseError.toException())
        }
    }
}