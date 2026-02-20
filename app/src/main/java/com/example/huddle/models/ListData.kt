package com.example.huddle.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ListData(
    val prioLevel:Int,
    val title:String,
    val details:String,

    @ServerTimestamp
    val createdAt: Date? = null
)
