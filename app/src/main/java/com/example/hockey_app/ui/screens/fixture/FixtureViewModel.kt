package com.example.hockey_app.ui.screens.fixture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.TorneoResumen
import com.example.hockey_app.data.services.DataService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FixtureState {
    object Loading : FixtureState()
    data class Success(val torneos: List<TorneoResumen>) : FixtureState()
    data class Error(val message: String) : FixtureState()
}

@HiltViewModel
class FixtureViewModel @Inject constructor(
    private val dataService: DataService
) : ViewModel() {

    private val _allTorneos = MutableStateFlow<List<TorneoResumen>>(emptyList())
    private val _state = MutableStateFlow<FixtureState>(FixtureState.Loading)
    val state: StateFlow<FixtureState> = _state

    private val _rama = MutableStateFlow("F")
    val rama: StateFlow<String> = _rama

    private val _categoria = MutableStateFlow("Todas")
    val categoria: StateFlow<String> = _categoria

    init {
        loadTorneos()
        observeFilters()
    }

    private fun loadTorneos() {
        viewModelScope.launch {
            _state.value = FixtureState.Loading
            try {
                val list = dataService.getTorneosResumen()
                _allTorneos.value = list.filter { it.temporada == "2026" }
                    .distinctBy { "${it.nombre}-${it.rama}-${it.categoria}-${it.division}" }
                applyFilters()
            } catch (e: Exception) {
                _state.value = FixtureState.Error(e.message ?: "Error al cargar fixtures")
            }
        }
    }

    private fun observeFilters() {
        combine(_rama, _categoria) { _, _ ->
            applyFilters()
        }.launchIn(viewModelScope)
    }

    private fun applyFilters() {
        val filtered = _allTorneos.value.filter { t ->
            val matchesRama = t.rama == _rama.value
            val matchesCat = _categoria.value == "Todas" || t.categoria.contains(_categoria.value, ignoreCase = true)
            matchesRama && matchesCat
        }
        _state.value = FixtureState.Success(filtered)
    }

    fun onRamaChange(newRama: String) {
        _rama.value = newRama
    }

    fun onCategoriaChange(newCat: String) {
        _categoria.value = newCat
    }

    fun refresh() {
        loadTorneos()
    }
}
