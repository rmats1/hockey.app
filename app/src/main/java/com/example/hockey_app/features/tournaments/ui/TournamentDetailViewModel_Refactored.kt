package com.example.hockey_app.features.tournaments.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.GoleadorAHBA
import com.example.hockey_app.data.models.PartidoAHBA
import com.example.hockey_app.data.models.PosicionAHBA
import com.example.hockey_app.data.services.AuthService
import com.example.hockey_app.features.tournaments.domain.usecases.CalculatePerformanceUseCase
import com.example.hockey_app.features.tournaments.domain.usecases.GetTournamentDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

sealed class TournamentDetailState {
    object Loading : TournamentDetailState()
    data class Success(
        val posiciones: ImmutableList<PosicionAHBA>,
        val partidos: ImmutableList<PartidoAHBA>,
        val goleadores: ImmutableList<GoleadorAHBA>,
        val performancePoints: ImmutableList<Float> = persistentListOf(),
        val performanceLabels: ImmutableList<String> = persistentListOf(),
        val targetTeam: String = ""
    ) : TournamentDetailState()
    data class Error(val message: String) : TournamentDetailState()
}

@HiltViewModel
class TournamentDetailViewModel_Refactored @Inject constructor(
    private val getTournamentDetailUseCase: GetTournamentDetailUseCase,
    private val calculatePerformanceUseCase: CalculatePerformanceUseCase,
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow<TournamentDetailState>(TournamentDetailState.Loading)
    val state: StateFlow<TournamentDetailState> = _state

    fun loadData(torneoId: String) {
        viewModelScope.launch {
            _state.value = TournamentDetailState.Loading
            
            getTournamentDetailUseCase(torneoId).onSuccess { detail ->
                val user = authService.getLocalUser()
                var teamToAnalyze = user?.club_nombre ?: ""
                
                if (teamToAnalyze.isEmpty() || teamToAnalyze == "Sin Club") {
                    teamToAnalyze = detail.posiciones.firstOrNull()?.clubNombre ?: ""
                }

                val (points, labels) = calculatePerformanceUseCase(teamToAnalyze, detail.partidos)
                
                _state.value = TournamentDetailState.Success(
                    posiciones = detail.posiciones.toImmutableList(), 
                    partidos = detail.partidos.toImmutableList(), 
                    goleadores = detail.goleadores.toImmutableList(),
                    performancePoints = points.toImmutableList(),
                    performanceLabels = labels.toImmutableList(),
                    targetTeam = teamToAnalyze
                )
            }.onFailure { e ->
                _state.value = TournamentDetailState.Error(e.message ?: "Error al cargar detalle")
            }
        }
    }
}
