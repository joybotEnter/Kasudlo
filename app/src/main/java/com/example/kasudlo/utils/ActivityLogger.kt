package com.example.kasudlo.utils

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

object ActivityLogger {

    fun log(
        db: FirebaseFirestore,
        uid: String,
        type: String,
        title: String,
        description: String
    ) {
        val data = hashMapOf(
            "nurseUid" to uid,
            "type" to type,
            "title" to title,
            "description" to description,
            "createdAt" to Timestamp.now()
        )

        db.collection("activity_logs").add(data)
    }
}