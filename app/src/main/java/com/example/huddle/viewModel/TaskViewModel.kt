package com.example.huddle.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huddle.models.HistoryData
import com.example.huddle.models.TaskData
import com.example.huddle.models.DataRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel: ViewModel() {

    private var tasksListener: ListenerRegistration? = null

    private val _taskList = MutableStateFlow<List<TaskData>>(emptyList())
    val taskList: StateFlow<List<TaskData>> = _taskList

    private val _selectedTask = MutableStateFlow<TaskData?>(null)
    val selectedTask: StateFlow<TaskData?> = _selectedTask

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        startListeningToTasks()
    }

    fun startListeningToTasks() {
        val uid = DataRepository.getUserId()
        if (uid == null) {
            _isLoading.value = false
            return
        }

        _isLoading.value = true

        tasksListener = DataRepository.getTasksQuery().addSnapshotListener { snapshots, error ->
            if (error == null && snapshots != null) {
                _taskList.value = snapshots.toObjects(TaskData::class.java)
                _isLoading.value = false
            }
        }
    }

    fun selectTask(task: TaskData) {
        _selectedTask.value = task
    }

    fun clearSelection() {
        _selectedTask.value = null
    }

    fun addTask(task: TaskData) {
        viewModelScope.launch {
            DataRepository.addTask(task)
        }
    }

    fun addHistory(history: HistoryData) {
        viewModelScope.launch {
            DataRepository.addHistory(history)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch { DataRepository.deleteTask(taskId) }
    }

    fun updateTaskStatus(taskId: String, newStatus:Int) {
        viewModelScope.launch { DataRepository.updateStatus(taskId, newStatus) }
    }

    override fun onCleared() {
        super.onCleared()
        tasksListener?.remove()
    }
}