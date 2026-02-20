package com.example.huddle.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class TaskData(
    val taskId: String = "",
    val prioLevel: Int = 0,
    val status: Int = 0 ,
    val title: String = "",
    val details: String = "",
    val mentorName:String = "",

    @ServerTimestamp
    val createdAt: Date? = null
)
