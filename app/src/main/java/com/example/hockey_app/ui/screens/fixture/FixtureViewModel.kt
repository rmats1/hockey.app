package com.example.hockey_app.ui.screens.fixture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.TorneoResumen
import com.example.hockey_app.domain.catalog.CatalogRepository
import com.example.hockey_app.domain.competition.CompetitionRepository
import com.example.hockey_app.data.constants.CompetitionCatalog
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
    private val dataService: CatalogRepository,
    private val supabaseService: CompetitionRepository
) : ViewModel() {

    private val _allTorneos = MutableStateFlow<List<TorneoResumen>>(emptyList())
    private val _state = MutableStateFlow<FixtureState>(FixtureState.Loading)
    val state: StateFlow<FixtureState> = _state

    private val _rama = MutableStateFlow("F")
    val rama: StateFlow<String> = _rama

    private val _categoria = MutableStateFlow("Todas")
    val categoria: StateFlow<String> = _categoria
    private val _categoriasDisponibles = MutableStateFlow(CompetitionCatalog.categories("Damas"))
    val categoriasDisponibles: StateFlow<List<String>> = _categoriasDisponibles

    init {
        loadTorneos()
        observeFilters()
    }

    private fun loadTorneos() {
        viewModelScope.launch {
            _state.value = FixtureState.Loading
            try {
                // Sincronizar desde Supabase con prioridad a 2026
                val list = supabaseService.getTorneos()
                val processedList = if (list.isEmpty()) {
                    dataService.getTorneosResumen().filter { it.temporada == "2026" }
                } else {
                    list.filter { it.temporada == "2026" }
                }
                _allTorneos.value = processedList.distinctBy { "${it.nombre}-${it.rama}-${it.categoria}-${it.division}" }
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
            val matchesRama = t.rama.contains(_rama.value, ignoreCase = true) ||
                (t.rama == "Femenino" && _rama.value == "F") ||
                (t.rama == "Damas" && _rama.value == "F") ||
                (t.rama == "Masculino" && _rama.value == "M") ||
                (t.rama == "Caballeros" && _rama.value == "M")
            val matchesCat = _categoria.value == "Todas" || t.categoria.contains(_categoria.value, ignoreCase = true)
            matchesRama && matchesCat
        }
        _state.value = FixtureState.Success(filtered)
    }

    fun onRamaChange(newRama: String) {
        _rama.value = newRama
        updateAvailableCategories(newRama)
        if (_categoria.value != "Todas" && _categoria.value !in _categoriasDisponibles.value) _categoria.value = "Todas"
    }

    fun onCategoriaChange(newCat: String) {
        _categoria.value = newCat
    }

    private fun updateAvailableCategories(branchFilter: String = _rama.value) {
        val branch = if (branchFilter == "F") "Damas" else "Caballeros"
        _categoriasDisponibles.value = _allTorneos.value.filter { it.rama.contains(if (branch == "Damas") "Femen" else "Mascul", true) || it.rama == branch }.map { it.categoria }.filter { it.isNotBlank() }.distinct().ifEmpty { CompetitionCatalog.categories(branch) }
    }

    fun refresh() {
        loadTorneos()
    }
}
