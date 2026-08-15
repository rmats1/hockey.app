package com.example.hockey_app.ui.screens.torneos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.GoleadorAHBA
import com.example.hockey_app.data.models.PosicionAHBA
import com.example.hockey_app.data.models.TorneoResumen
import com.example.hockey_app.domain.catalog.CatalogRepository
import com.example.hockey_app.domain.competition.CompetitionRepository
import com.example.hockey_app.data.constants.CompetitionCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

sealed class TorneosState {
    object Loading : TorneosState()
    data class Success(val torneos: ImmutableList<TorneoResumen>) : TorneosState()
    data class Error(val message: String) : TorneosState()
}

@HiltViewModel
class TorneosViewModel @Inject constructor(
    private val dataService: CatalogRepository,
    private val supabaseService: CompetitionRepository
) : ViewModel() {

    private val _allTorneos = MutableStateFlow<List<TorneoResumen>>(emptyList())
    private val _state = MutableStateFlow<TorneosState>(TorneosState.Loading)
    val state: StateFlow<TorneosState> = _state

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    private val _filtroRama = MutableStateFlow("Todas")
    val filtroRama: StateFlow<String> = _filtroRama

    private val _filtroCategoria = MutableStateFlow("Todas")
    val filtroCategoria: StateFlow<String> = _filtroCategoria
    private val _categoriasDisponibles = MutableStateFlow(CompetitionCatalog.categories("Damas"))
    val categoriasDisponibles: StateFlow<List<String>> = _categoriasDisponibles

    init {
        loadTorneos()
        observeFilters()
    }

    private fun loadTorneos() {
        viewModelScope.launch {
            _state.value = TorneosState.Loading
            try {
                // Prioridad absoluta a Supabase para tener datos reales de 2026
                val list = supabaseService.getTorneos()
                Timber.d("Torneos received from Supabase: ${list.size}")
                
                val processedList = if (list.isEmpty()) {
                    // Fallback solo a los de 2026 en assets si la nube está vacía
                    val fallback = dataService.getTorneosResumen().filter { it.temporada == "2026" }
                    Timber.d("Fallback to assets 2026: ${fallback.size}")
                    fallback
                } else {
                    val filtered = list.filter { it.temporada == "2026" }
                    Timber.d("Filtered Supabase torneos for 2026: ${filtered.size}")
                    filtered
                }

                if (processedList.isEmpty()) {
                    Timber.w("No torneos found for 2026 in Supabase or Assets")
                }

                _allTorneos.value = processedList.distinctBy { "${it.nombre}-${it.rama}-${it.categoria}-${it.division}" }
                applyFilters()
            } catch (e: Exception) {
                Timber.e(e, "Error loading torneos")
                _state.value = TorneosState.Error(e.message ?: "Error al cargar torneos")
            }
        }
    }

    private fun observeFilters() {
        combine(_busqueda, _filtroRama, _filtroCategoria) { _, _, _ ->
            applyFilters()
        }.launchIn(viewModelScope)
    }

    private fun applyFilters() {
        val filtered = _allTorneos.value.filter { t ->
            val matchesRama = _filtroRama.value == "Todas" || 
                t.rama.contains(_filtroRama.value, ignoreCase = true) ||
                (t.rama == "Femenino" && _filtroRama.value == "F") ||
                (t.rama == "Damas" && _filtroRama.value == "F") ||
                (t.rama == "Masculino" && _filtroRama.value == "M") ||
                (t.rama == "Caballeros" && _filtroRama.value == "M")
            
            val matchesCat = _filtroCategoria.value == "Todas" || t.categoria.contains(_filtroCategoria.value, ignoreCase = true)
            val matchesSearch = _busqueda.value.isBlank() || 
                t.nombre.contains(_busqueda.value, ignoreCase = true) ||
                t.categoria.contains(_busqueda.value, ignoreCase = true) ||
                t.division.contains(_busqueda.value, ignoreCase = true)
            
            matchesRama && matchesCat && matchesSearch
        }
        _state.value = TorneosState.Success(filtered.toImmutableList())
    }

    fun onSearchChange(query: String) {
        _busqueda.value = query
    }

    fun onRamaChange(rama: String) {
        _filtroRama.value = rama
        updateAvailableCategories(rama)
        if (_filtroCategoria.value != "Todas" && _filtroCategoria.value !in _categoriasDisponibles.value) _filtroCategoria.value = "Todas"
    }

    private fun updateAvailableCategories(branchFilter: String = _filtroRama.value) {
        val branch = when (branchFilter) { "F" -> "Damas"; "M" -> "Caballeros"; else -> null }
        _categoriasDisponibles.value = if (branch == null) _allTorneos.value.map { it.categoria }.filter { it.isNotBlank() }.distinct().ifEmpty { CompetitionCatalog.categories("Damas") + CompetitionCatalog.categories("Caballeros") } else _allTorneos.value.filter { it.rama.contains(if (branch == "Damas") "Femen" else "Mascul", true) || it.rama == branch }.map { it.categoria }.filter { it.isNotBlank() }.distinct().ifEmpty { CompetitionCatalog.categories(branch) }
    }

    fun onCategoriaChange(cat: String) {
        _filtroCategoria.value = cat
    }

    fun refresh() {
        loadTorneos()
    }

    private val _goleadores = MutableStateFlow<List<GoleadorAHBA>>(emptyList())
    val goleadores: StateFlow<List<GoleadorAHBA>> = _goleadores

    private val _posiciones = MutableStateFlow<List<PosicionAHBA>>(emptyList())
    val posiciones: StateFlow<List<PosicionAHBA>> = _posiciones

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadEstadisticas(torneoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _goleadores.value = supabaseService.getGoleadores(torneoId)
            _posiciones.value = supabaseService.getPosiciones(torneoId)
            _isLoading.value = false
        }
    }
}
