package com.example.huddle.models

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object DataRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    private fun getTasksCollection() = db.collection("HuddleUsers").document("${currentUserId}").collection("tasks")
    private fun getHistoriesCollection() = db.collection("HuddleUsers").document("${currentUserId}").collection("histories")


    fun getTasksQuery(): Query {
        return getTasksCollection().orderBy("createdAt", Query.Direction.DESCENDING)
    }

    fun getHistoryQuery(): Query {
        return getHistoriesCollection().orderBy("createdAt", Query.Direction.DESCENDING)
    }

    fun addTask(task: TaskData) {
        val newDocRef = getTasksCollection().document()
        val taskWithId = task.copy(taskId = newDocRef.id)

        newDocRef.set(taskWithId)
    }

    fun addHistory(history: HistoryData) {
        getHistoriesCollection().add(history)
    }

    fun deleteTask(taskId: String) {
        getTasksCollection().document(taskId).delete()
    }

    fun deleteHistory(historyId: String) {
        getHistoriesCollection().document(historyId).delete()
    }

    fun updateStatus(taskId: String, status: Int) {
        getTasksCollection().document(taskId).update("status", status)
    }

    fun ensureAuthenticated(onComplete: (String?) -> Unit) {
        if (auth.currentUser != null) {
            onComplete(auth.currentUser?.uid)
        } else {
            auth.signInAnonymously().addOnCompleteListener { auth ->
                if (auth.isSuccessful) {
                    val uid = auth.result?.user?.uid
                    if(uid != null){
                        setupNewUser(uid) { succes ->
                            if (succes) onComplete(uid) else onComplete(null)
                        }
                    } else{
                        onComplete(null)
                    }
                } else {
                    onComplete(null)
                }
            }
        }
    }

    private fun setupNewUser(uid: String, onComplete: (Boolean) -> Unit) {
        val userMap = hashMapOf("userId" to uid, "createdAt" to FieldValue.serverTimestamp())
        db.collection("HuddleUsers").document(uid).set(userMap)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }


    fun getUserId(): String? {
        return currentUserId
    }

}