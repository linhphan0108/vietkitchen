package com.example.linh.vietkitchen.exception

import com.google.firebase.database.DatabaseError

class FirebaseDataException(private var error: DatabaseError) : Exception() {
    override fun toString(): String {
        return "RxFirebaseDataException{error=" + this.error + '}'.toString()
    }
}