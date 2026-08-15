package com.example.hockey_app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.domain.auth.AuthRepository
import com.example.hockey_app.domain.auth.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val signIn: SignInUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    fun onEmailChange(newValue: String) {
        _email.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    fun login() {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _state.value = LoginState.Error("Por favor completa todos los campos")
            return
        }

        viewModelScope.launch {
            _state.value = LoginState.Loading
            signIn(_email.value, _password.value)
                .onSuccess {
                    _state.value = LoginState.Success
                }
                .onFailure { error ->
                    _state.value = LoginState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    init {
        viewModelScope.launch {
            authRepository.sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated) {
                    _state.value = LoginState.Success
                }
            }
        }
    }
    
    fun resetError() {
        _state.value = LoginState.Idle
    }
}
