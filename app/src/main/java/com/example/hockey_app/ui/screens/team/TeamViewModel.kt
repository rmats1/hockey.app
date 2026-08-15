package com.example.hockey_app.ui.screens.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.CallUpModel
import com.example.hockey_app.data.models.TrainingPlanModel
import com.example.hockey_app.data.models.UserModel
import com.example.hockey_app.domain.auth.AuthRepository
import com.example.hockey_app.domain.competition.CompetitionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val authService: AuthRepository,
    private val supabaseService: CompetitionRepository
) : ViewModel() {

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    private val _trainingPlan = MutableStateFlow<TrainingPlanModel?>(null)
    val trainingPlan: StateFlow<TrainingPlanModel?> = _trainingPlan

    private val _callUp = MutableStateFlow<CallUpModel?>(null)
    val callUp: StateFlow<CallUpModel?> = _callUp

    private val _jugadores = MutableStateFlow<List<UserModel>>(emptyList())
    val jugadores: StateFlow<List<UserModel>> = _jugadores

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUser = authService.getCurrentUser() ?: authService.getLocalUser()
            _user.value = currentUser
            
            currentUser?.let { user ->
                if (user.club_id != "0") {
                    _trainingPlan.value = supabaseService.getLatestTrainingPlan(
                        clubId = user.club_id,
                        categoria = user.categoria,
                        division = user.division
                    )
                    _callUp.value = supabaseService.getUserCallUp(user.id)
                }
            }
            _isLoading.value = false
        }
    }

    fun buscarJugadores(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _jugadores.value = supabaseService.buscarJugadores(query)
            _isLoading.value = false
        }
    }
}
