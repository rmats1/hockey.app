package com.example.hockey_app.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.UserModel
import com.example.hockey_app.features.auth.domain.repositories.AuthRepository
import com.example.hockey_app.features.auth.domain.usecases.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel_Refactored @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value = getCurrentUserUseCase()
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout().onSuccess {
                onSuccess()
            }
        }
    }
}
