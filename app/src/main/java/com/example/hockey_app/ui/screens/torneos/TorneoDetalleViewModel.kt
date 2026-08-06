package com.example.hockey_app.ui.screens.torneos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.GoleadorAHBA
import com.example.hockey_app.data.models.PartidoAHBA
import com.example.hockey_app.data.models.PosicionAHBA
import com.example.hockey_app.data.services.AuthService
import com.example.hockey_app.data.services.SupabaseService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TorneoDetalleState {
    object Loading : TorneoDetalleState()
    data class Success(
        val posiciones: List<PosicionAHBA>,
        val partidos: List<PartidoAHBA>,
        val goleadores: List<GoleadorAHBA>,
        val performancePoints: List<Float> = emptyList(),
        val performanceLabels: List<String> = emptyList(),
        val targetTeam: String = ""
    ) : TorneoDetalleState()
    data class Error(val message: String) : TorneoDetalleState()
}

@HiltViewModel
class TorneoDetalleViewModel @Inject constructor(
    private val supabaseService: SupabaseService,
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow<TorneoDetalleState>(TorneoDetalleState.Loading)
    val state: StateFlow<TorneoDetalleState> = _state

    fun loadData(torneoId: String) {
        viewModelScope.launch {
            _state.value = TorneoDetalleState.Loading
            try {
                val posiciones = supabaseService.getPosiciones(torneoId)
                val partidos = supabaseService.getPartidos(torneoId)
                val goleadores = supabaseService.getGoleadores(torneoId)
                
                val user = authService.getLocalUser()
                var teamToAnalyze = user?.club_nombre ?: ""
                if (teamToAnalyze.isEmpty() || teamToAnalyze == "Sin Club") {
                    teamToAnalyze = posiciones.firstOrNull()?.clubNombre ?: ""
                }

                val performance = calculatePerformance(partidos, teamToAnalyze)
                
                _state.value = TorneoDetalleState.Success(
                    posiciones, 
                    partidos, 
                    goleadores,
                    performancePoints = performance.first,
                    performanceLabels = performance.second,
                    targetTeam = teamToAnalyze
                )
            } catch (e: Exception) {
                _state.value = TorneoDetalleState.Error(e.message ?: "Error al cargar detalle del torneo")
            }
        }
    }

    private fun calculatePerformance(partidos: List<PartidoAHBA>, team: String): Pair<List<Float>, List<String>> {
        if (team.isEmpty()) return emptyList<Float>() to emptyList()
        
        val playedGames = partidos.filter { 
            it.jugado && (it.nombreLocal.contains(team, true) || it.nombreVisitante.contains(team, true))
        }.sortedBy { it.numeroFecha.toIntOrNull() ?: 0 }
        
        val last5 = playedGames.takeLast(5)
        val points = last5.map { 
            val isLocal = it.nombreLocal.contains(team, true)
            val myG = if(isLocal) it.golesLocal ?: 0 else it.golesVisitante ?: 0
            val opG = if(isLocal) it.golesVisitante ?: 0 else it.golesLocal ?: 0
            
            when {
                myG > opG -> 3f
                myG == opG -> 1f
                else -> 0f
            }
        }
        val labels = last5.map { "F${it.numeroFecha}" }
        
        return points to labels
    }
}
