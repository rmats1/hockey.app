package com.example.hockey_app.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber
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
            try {
                // Wait for session status to be determined (Authenticated or NotAuthenticated)
                // We add a timeout just in case restoration hangs
                withTimeout(3000) {
                    authService.sessionStatus
                        .filter { it !is SessionStatus.Initializing }
                        .take(1)
                        .collect { status ->
                            Timber.d("Splash check status: ${status::class.simpleName}")
                            _isLoggedIn.value = status is SessionStatus.Authenticated
                            _isReady.value = true
                        }
                }
            } catch (e: Exception) {
                Timber.w("Splash check timed out or failed. Defaulting to Login.")
                _isLoggedIn.value = false
                _isReady.value = true
            }
        }
    }
}
