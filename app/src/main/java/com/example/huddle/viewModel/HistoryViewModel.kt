package com.example.huddle.viewModel

import android.app.Application
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huddle.models.HistoryData
import com.example.huddle.models.DataRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryViewModel(application: Application): AndroidViewModel(application) {

    private var historiesListener: ListenerRegistration? = null

    private val _historyList = MutableStateFlow<List<HistoryData>>(emptyList())
    val historyList: StateFlow<List<HistoryData>> = _historyList

    private val _exportStatus = MutableStateFlow<String?>(null)
    val exportStatus: StateFlow<String?> = _exportStatus

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
        viewModelScope.launch { DataRepository.deleteHistory(historyId) }
    }

    fun updateTaskStatus(taskId: String, isComplete: Boolean) {
        val newStatus = if (isComplete) 1 else 0
        viewModelScope.launch { DataRepository.updateStatus(taskId, newStatus) }
    }

    override fun onCleared() {
        super.onCleared()
        historiesListener?.remove()
    }


    fun exportDataToExcel() {
        viewModelScope.launch(Dispatchers.IO) {
            _exportStatus.value = "Exporting..."

            try {
                val currentList = _historyList.value
                if (currentList.isEmpty()) {
                    _exportStatus.value = "No data to export"
                    return@launch
                }

                val fileName = "History_Report_${System.currentTimeMillis()}.xlsx"
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = getApplication<Application>().contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        generateExcelFile(currentList, outputStream)
                    }
                    _exportStatus.value = "Saved to Downloads: $fileName"
                } else {
                    _exportStatus.value = "Failed to create file entry"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _exportStatus.value = "Failed: ${e.localizedMessage}"
            }
        }
    }

    // Reset the status message after the UI shows it
    fun clearExportStatus() {
        _exportStatus.value = null
    }

    private fun generateExcelFile(historyList: List<HistoryData>, outputStream: java.io.OutputStream) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("History Logs")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // CREATE HEADERS
        val headers = listOf("ID", "Priority", "Title", "Details", "Mentor", "Date Created")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, title ->
            headerRow.createCell(index).setCellValue(title)
        }

        // FILL DATA
        historyList.forEachIndexed { index, data ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(data.historyId)
            row.createCell(1).setCellValue(data.prioLevel.toDouble())
            row.createCell(2).setCellValue(data.title)
            row.createCell(3).setCellValue(data.details)
            row.createCell(4).setCellValue(data.mentorName)

            val dateString = data.createdAt?.let { dateFormat.format(it) } ?: "N/A"
            row.createCell(5).setCellValue(dateString)
        }

        // WRITE DIRECTLY TO THE STREAM
        workbook.write(outputStream)
        workbook.close()
    }

}
