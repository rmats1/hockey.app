package com.example.hockey_app.ui.screens.torneos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.GoleadorAHBA
import com.example.hockey_app.data.models.PartidoAHBA
import com.example.hockey_app.data.models.PosicionAHBA
import com.example.hockey_app.domain.auth.AuthRepository
import com.example.hockey_app.domain.competition.CompetitionRepository
import com.example.hockey_app.features.tournaments.domain.usecases.CalculatePerformanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

sealed class TorneoDetalleState {
    object Loading : TorneoDetalleState()
    data class Success(
        val posiciones: ImmutableList<PosicionAHBA>,
        val partidos: ImmutableList<PartidoAHBA>,
        val goleadores: ImmutableList<GoleadorAHBA>,
        val performancePoints: ImmutableList<Float> = persistentListOf(),
        val performanceLabels: ImmutableList<String> = persistentListOf(),
        val targetTeam: String = ""
    ) : TorneoDetalleState()
    data class Error(val message: String) : TorneoDetalleState()
}

@HiltViewModel
class TorneoDetalleViewModel @Inject constructor(
    private val supabaseService: CompetitionRepository,
    private val authService: AuthRepository,
    private val calculatePerformanceUseCase: CalculatePerformanceUseCase
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

                val (points, labels) = calculatePerformanceUseCase(teamToAnalyze, partidos)
                
                _state.value = TorneoDetalleState.Success(
                    posiciones.toImmutableList(), 
                    partidos.toImmutableList(), 
                    goleadores.toImmutableList(),
                    performancePoints = points.toImmutableList(),
                    performanceLabels = labels.toImmutableList(),
                    targetTeam = teamToAnalyze
                )
            } catch (e: Exception) {
                _state.value = TorneoDetalleState.Error(e.message ?: "Error al cargar detalle del torneo")
            }
        }
    }
}
