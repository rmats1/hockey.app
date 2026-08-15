package com.example.hockey_app.ui.screens.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.TrainingPlanModel
import com.example.hockey_app.domain.auth.AuthRepository
import com.example.hockey_app.domain.competition.CompetitionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhysicalPlanningViewModel @Inject constructor(
    private val supabaseService: CompetitionRepository,
    private val authService: AuthRepository
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun savePlan(detalle: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            val user = authService.getCurrentUser() ?: authService.getLocalUser()
            if (user != null && user.club_id != "0") {
                val plan = TrainingPlanModel(
                    club_id = user.club_id,
                    categoria = user.categoria,
                    division = user.division,
                    plan_detalle = detalle,
                    fecha_creacion = System.currentTimeMillis().toString()
                )
                if (supabaseService.postTrainingPlan(plan)) {
                    onComplete()
                }
            }
            _isSaving.value = false
        }
    }
}
