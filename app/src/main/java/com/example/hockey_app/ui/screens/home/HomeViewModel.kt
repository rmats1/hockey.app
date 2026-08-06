package com.example.hockey_app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.UserModel
import com.example.hockey_app.data.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value = authService.getCurrentUser()
        }
    }

    fun onTabSelected(index: Int) {
        _currentIndex.value = index
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authService.logout()
            onSuccess()
        }
    }
}
