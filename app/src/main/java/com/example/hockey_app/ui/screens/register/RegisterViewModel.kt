package com.example.hockey_app.ui.screens.register

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

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authService: AuthService,
    private val dataService: DataService
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state

    private val _clubes = MutableStateFlow<List<ClubModel>>(emptyList())
    val clubes: StateFlow<List<ClubModel>> = _clubes

    // Form Fields
    var userType = MutableStateFlow("jugador")
    var rama = MutableStateFlow("Damas")
    var categoria = MutableStateFlow("Primera")
    var division = MutableStateFlow<String?>(null)
    var selectedClub = MutableStateFlow<ClubModel?>(null)
    var name = MutableStateFlow("")
    var email = MutableStateFlow("")
    var password = MutableStateFlow("")
    var confirmPassword = MutableStateFlow("")
    var numeroCamiseta = MutableStateFlow("")
    var posicion = MutableStateFlow<String?>(null)
    var rolCuerpoTecnico = MutableStateFlow<String?>(null)

    init {
        loadClubes()
    }

    private fun loadClubes() {
        viewModelScope.launch {
            _clubes.value = dataService.getClubes()
        }
    }

    fun register() {
        if (selectedClub.value == null) {
            _state.value = RegisterState.Error("Por favor selecciona tu club")
            return
        }
        if (userType.value == "jugador" && division.value == null) {
            _state.value = RegisterState.Error("Por favor selecciona tu división")
            return
        }
        if (password.value != confirmPassword.value) {
            _state.value = RegisterState.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _state.value = RegisterState.Loading
            
            val userMetadata = UserModel(
                id = "", // Will be set by Auth
                email = email.value.trim(),
                nombre = name.value.trim(),
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

            authService.signUpWithEmail(email.value.trim(), password.value, userMetadata)
                .onSuccess {
                    _state.value = RegisterState.Success
                }
                .onFailure { error ->
                    _state.value = RegisterState.Error(error.message ?: "Error al registrar")
                }
        }
    }

    fun resetError() {
        _state.value = RegisterState.Idle
    }
}
