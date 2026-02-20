package com.example.huddle.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.huddle.models.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {


    // We use LiveData or StateFlow to tell the UI what's happening
    val loginStatus = MutableLiveData<Boolean>()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    init {
        DataRepository.ensureAuthenticated { uid ->
            if (uid != null) {
                _userId.value = uid
                _isAuthenticated.value = true
            } else {
                _isAuthenticated.value = false
            }
        }
    }
}