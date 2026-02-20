package com.example.huddle.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huddle.models.HistoryData
import com.example.huddle.models.DataRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel: ViewModel() {

    private var historiesListener: ListenerRegistration? = null

    private val _historyList = MutableStateFlow<List<HistoryData>>(emptyList())
    val historyList: StateFlow<List<HistoryData>> = _historyList

    private val _selectedHistory = MutableStateFlow<HistoryData?>(null)
    val selectedTask: StateFlow<HistoryData?> = _selectedHistory

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        startListeningToHistories()
    }

    private fun startListeningToHistories() {
        _isLoading.value = true
        historiesListener = DataRepository.getHistoryQuery().addSnapshotListener { snapshots, error ->
            if (error == null && snapshots != null) {
                _historyList.value = snapshots.toObjects(HistoryData::class.java)
                _isLoading.value = false
            }
        }
    }

    fun selectTask(history: HistoryData) {
        _selectedHistory.value = history
    }

    fun clearSelection() {
        _selectedHistory.value = null
    }

    fun addHistory(history: HistoryData) {
        viewModelScope.launch {
            DataRepository.addHistory(history)
        }
    }

    fun deleteHistory(historyId: String) {
        viewModelScope.launch { DataRepository.deleteTask(historyId) }
    }

    fun updateTaskStatus(taskId: String, isComplete: Boolean) {
        val newStatus = if (isComplete) 1 else 0
        viewModelScope.launch { DataRepository.updateStatus(taskId, newStatus) }
    }

    override fun onCleared() {
        super.onCleared()
        historiesListener?.remove()
    }
}
