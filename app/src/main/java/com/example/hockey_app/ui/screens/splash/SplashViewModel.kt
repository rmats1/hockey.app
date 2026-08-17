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
            // Monitorizamos el estado hasta que deje de ser Initializing
            authService.sessionStatus.collect { status ->
                Timber.d("Splash check current status: ${status::class.simpleName}")
                if (status !is SessionStatus.Initializing) {
                    _isLoggedIn.value = status is SessionStatus.Authenticated
                    _isReady.value = true
                    // Una vez determinado el estado, dejamos de observar
                    return@collect 
                }
            }
        }
    }
}
