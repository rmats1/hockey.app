package com.example.hockey_app.ui.screens.fixture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.ComentarioModel
import com.example.hockey_app.data.models.PartidoAHBA
import com.example.hockey_app.data.models.PrediccionModel
import com.example.hockey_app.data.services.AuthService
import com.example.hockey_app.data.services.SupabaseService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MatchDetailState {
    object Loading : MatchDetailState()
    data class Success(
        val partido: PartidoAHBA,
        val comentarios: List<ComentarioModel>,
        val predicciones: List<PrediccionModel>
    ) : MatchDetailState()
    data class Error(val message: String) : MatchDetailState()
}

@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    private val supabaseService: SupabaseService,
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow<MatchDetailState>(MatchDetailState.Loading)
    val state: StateFlow<MatchDetailState> = _state

    fun loadMatch(matchId: String) {
        viewModelScope.launch {
            _state.value = MatchDetailState.Loading
            val partido = supabaseService.getPartido(matchId)
            if (partido != null) {
                val comentarios = supabaseService.getComentarios(matchId)
                val predicciones = supabaseService.getPredicciones(matchId)
                _state.value = MatchDetailState.Success(partido, comentarios, predicciones)
            } else {
                _state.value = MatchDetailState.Error("No se encontró el partido.")
            }
        }
    }

    fun postComentario(matchId: String, texto: String) {
        viewModelScope.launch {
            val user = authService.getCurrentUser() ?: authService.getLocalUser()
            if (user != null) {
                val comentario = ComentarioModel(
                    partido_id = matchId,
                    user_id = user.id,
                    user_name = user.nombre,
                    texto = texto,
                    fecha = System.currentTimeMillis().toString()
                )
                if (supabaseService.postComentario(comentario)) {
                    loadMatch(matchId) // Refresh
                }
            }
        }
    }

    fun postPrediccion(matchId: String, golesL: Int, golesV: Int) {
        viewModelScope.launch {
            val user = authService.getCurrentUser() ?: authService.getLocalUser()
            if (user != null) {
                val prediccion = PrediccionModel(
                    partido_id = matchId,
                    user_id = user.id,
                    user_name = user.nombre,
                    goles_local = golesL,
                    goles_visitante = golesV,
                    fecha = System.currentTimeMillis().toString()
                )
                if (supabaseService.postPrediccion(prediccion)) {
                    loadMatch(matchId) // Refresh
                }
            }
        }
    }
}
