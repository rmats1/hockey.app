package com.example.hockey_app.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authService: AuthRepository
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            // Wait for session status to be determined (Authenticated or NotAuthenticated)
            authService.sessionStatus.collect { status ->
                if (status !is io.github.jan.supabase.auth.status.SessionStatus.Initializing) {
                    _isLoggedIn.value = status is io.github.jan.supabase.auth.status.SessionStatus.Authenticated
                    _isReady.value = true
                }
            }
        }
    }
}
