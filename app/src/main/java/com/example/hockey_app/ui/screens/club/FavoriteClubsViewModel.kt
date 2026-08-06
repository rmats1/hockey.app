package com.example.hockey_app.ui.screens.club

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.ClubModel
import com.example.hockey_app.data.models.FavoritoModel
import com.example.hockey_app.data.services.AuthService
import com.example.hockey_app.data.services.DataService
import com.example.hockey_app.data.services.SupabaseService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FavoriteClubsState {
    object Loading : FavoriteClubsState()
    data class Success(val clubes: List<ClubModel>, val favoritosIds: Set<String>) : FavoriteClubsState()
    data class Error(val message: String) : FavoriteClubsState()
}

@HiltViewModel
class FavoriteClubsViewModel @Inject constructor(
    private val dataService: DataService,
    private val supabaseService: SupabaseService,
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow<FavoriteClubsState>(FavoriteClubsState.Loading)
    val state: StateFlow<FavoriteClubsState> = _state

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = FavoriteClubsState.Loading
            val user = authService.getCurrentUser() ?: authService.getLocalUser()
            if (user != null) {
                val allClubs = dataService.getClubes()
                val favoritos = supabaseService.getFavoritos(user.id)
                val favIds = favoritos.map { it.club_id }.toSet()
                _state.value = FavoriteClubsState.Success(allClubs, favIds)
            } else {
                _state.value = FavoriteClubsState.Error("No se pudo identificar al usuario.")
            }
        }
    }

    fun toggleFavorito(clubId: String) {
        viewModelScope.launch {
            val user = authService.getCurrentUser() ?: authService.getLocalUser()
            val currentState = _state.value
            if (user != null && currentState is FavoriteClubsState.Success) {
                val isFav = currentState.favoritosIds.contains(clubId)
                if (isFav) {
                    if (supabaseService.deleteFavorito(user.id, clubId)) {
                        _state.value = currentState.copy(favoritosIds = currentState.favoritosIds - clubId)
                    }
                } else {
                    val favorito = FavoritoModel(
                        user_id = user.id,
                        club_id = clubId,
                        fecha_agregado = System.currentTimeMillis().toString()
                    )
                    if (supabaseService.postFavorito(favorito)) {
                        _state.value = currentState.copy(favoritosIds = currentState.favoritosIds + clubId)
                    }
                }
            }
        }
    }
}
