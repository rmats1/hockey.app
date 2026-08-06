package com.example.hockey_app.ui.screens.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.CallUpModel
import com.example.hockey_app.data.models.UserModel
import com.example.hockey_app.data.services.AuthService
import com.example.hockey_app.data.services.SupabaseService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CallUpManagementState {
    object Loading : CallUpManagementState()
    data class Success(val jugadores: List<UserModel>) : CallUpManagementState()
    data class Error(val message: String) : CallUpManagementState()
}

@HiltViewModel
class CallUpManagementViewModel @Inject constructor(
    private val supabaseService: SupabaseService,
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow<CallUpManagementState>(CallUpManagementState.Loading)
    val state: StateFlow<CallUpManagementState> = _state

    private val _selectedPlayerIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedPlayerIds: StateFlow<Set<String>> = _selectedPlayerIds

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    init {
        loadJugadores()
    }

    private fun loadJugadores() {
        viewModelScope.launch {
            _state.value = CallUpManagementState.Loading
            val user = authService.getCurrentUser() ?: authService.getLocalUser()
            if (user != null && user.club_id != "0") {
                val jugadores = supabaseService.getJugadoresPorClub(user.club_id)
                _state.value = CallUpManagementState.Success(jugadores)
            } else {
                _state.value = CallUpManagementState.Error("No se pudo identificar el club.")
            }
        }
    }

    fun togglePlayerSelection(playerId: String) {
        val current = _selectedPlayerIds.value
        if (current.contains(playerId)) {
            _selectedPlayerIds.value = current - playerId
        } else {
            _selectedPlayerIds.value = current + playerId
        }
    }

    fun saveCallUps(lugar: String, horario: String, rival: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            val user = authService.getCurrentUser() ?: authService.getLocalUser()
            if (user != null && _selectedPlayerIds.value.isNotEmpty()) {
                val callUps = _selectedPlayerIds.value.map { playerId ->
                    val calendar = java.util.Calendar.getInstance()
                    calendar.add(java.util.Calendar.DAY_OF_YEAR, 3)
                    val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(calendar.time)

                    CallUpModel(
                        user_id = playerId,
                        club_id = user.club_id,
                        lugar = lugar,
                        horario_citacion = horario,
                        rival_nombre = rival,
                        fecha_partido = dateStr,
                        categoria = user.categoria
                    )
                }
                if (supabaseService.postCallUps(callUps)) {
                    onComplete()
                }
            }
            _isSaving.value = false
        }
    }
}
