package com.example.hockey_app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.ClubModel
import com.example.hockey_app.data.models.UserModel
import com.example.hockey_app.data.services.AuthService
import com.example.hockey_app.data.services.DataService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OnboardingState {
    object Idle : OnboardingState()
    object Loading : OnboardingState()
    object Success : OnboardingState()
    data class Error(val message: String) : OnboardingState()
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authService: AuthService,
    private val dataService: DataService
) : ViewModel() {

    private val _state = MutableStateFlow<OnboardingState>(OnboardingState.Idle)
    val state: StateFlow<OnboardingState> = _state

    private val _clubes = MutableStateFlow<List<ClubModel>>(emptyList())
    val clubes: StateFlow<List<ClubModel>> = _clubes

    // Form Fields
    val userType = MutableStateFlow("jugador")
    val rama = MutableStateFlow("Damas")
    val categoria = MutableStateFlow("Primera")
    val division = MutableStateFlow<String?>(null)
    val selectedClub = MutableStateFlow<ClubModel?>(null)
    val numeroCamiseta = MutableStateFlow("")
    val posicion = MutableStateFlow<String?>(null)
    val rolCuerpoTecnico = MutableStateFlow<String?>(null)

    init {
        loadClubes()
    }

    private fun loadClubes() {
        viewModelScope.launch {
            _clubes.value = dataService.getClubes()
        }
    }

    fun finishOnboarding() {
        viewModelScope.launch {
            _state.value = OnboardingState.Loading
            
            val currentUser = authService.getCurrentUser()
            if (currentUser == null) {
                _state.value = OnboardingState.Error("No se pudo obtener el usuario actual")
                return@launch
            }

            val updatedUser = currentUser.copy(
                user_type = userType.value,
                rama = rama.value,
                categoria = categoria.value,
                division = division.value,
                club_id = selectedClub.value?.id ?: "0",
                club_nombre = selectedClub.value?.nombre ?: "Sin Club",
                club_escudo = selectedClub.value?.escudoUrl,
                numero_camiseta = numeroCamiseta.value.toIntOrNull(),
                posicion = posicion.value,
                rol_cuerpo_tecnico = rolCuerpoTecnico.value
            )

            authService.completeProfile(updatedUser)
                .onSuccess {
                    _state.value = OnboardingState.Success
                }
                .onFailure { error ->
                    _state.value = OnboardingState.Error(error.message ?: "Error al completar el perfil")
                }
        }
    }

    fun resetError() {
        _state.value = OnboardingState.Idle
    }
}
