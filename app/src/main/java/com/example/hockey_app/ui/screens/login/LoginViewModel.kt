package com.example.hockey_app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val authService: AuthService
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
            authService.signInWithEmail(_email.value, _password.value)
                .onSuccess {
                    _state.value = LoginState.Success
                }
                .onFailure { error ->
                    _state.value = LoginState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            authService.signInWithGoogle()
                .onSuccess {
                    _state.value = LoginState.Success
                }
                .onFailure { error ->
                    _state.value = LoginState.Error(error.message ?: "Error al conectar con Google")
                }
        }
    }
    
    fun resetError() {
        _state.value = LoginState.Idle
    }
}
