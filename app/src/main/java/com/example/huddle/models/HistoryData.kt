package com.example.huddle.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class HistoryData(
    val historyId: String = "",
    val prioLevel: Int = 0,
    val title: String = "",
    val details: String = "",
    val mentorName:String = "",

    @ServerTimestamp
    val createdAt: Date? = null
)
