package com.example.hockey_app.ui.screens.torneos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.GoleadorAHBA
import com.example.hockey_app.data.models.PosicionAHBA
import com.example.hockey_app.data.models.TorneoResumen
import com.example.hockey_app.data.services.DataService
import com.example.hockey_app.data.services.SupabaseService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TorneosState {
    object Loading : TorneosState()
    data class Success(val torneos: List<TorneoResumen>) : TorneosState()
    data class Error(val message: String) : TorneosState()
}

@HiltViewModel
class TorneosViewModel @Inject constructor(
    private val dataService: DataService,
    private val supabaseService: SupabaseService
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

    init {
        loadTorneos()
        observeFilters()
    }

    private fun loadTorneos() {
        viewModelScope.launch {
            _state.value = TorneosState.Loading
            val list = dataService.getTorneosResumen()
            _allTorneos.value = list.filter { it.temporada == "2026" }
                .distinctBy { "${it.nombre}-${it.rama}-${it.categoria}-${it.division}" }
            applyFilters()
        }
    }

    private fun observeFilters() {
        combine(_busqueda, _filtroRama, _filtroCategoria) { _, _, _ ->
            applyFilters()
        }.launchIn(viewModelScope)
    }

    private fun applyFilters() {
        val filtered = _allTorneos.value.filter { t ->
            val matchesRama = _filtroRama.value == "Todas" || t.rama == _filtroRama.value
            val matchesCat = _filtroCategoria.value == "Todas" || t.categoria.contains(_filtroCategoria.value, ignoreCase = true)
            val matchesSearch = _busqueda.value.isBlank() || 
                t.nombre.contains(_busqueda.value, ignoreCase = true) ||
                t.categoria.contains(_busqueda.value, ignoreCase = true) ||
                t.division.contains(_busqueda.value, ignoreCase = true)
            
            matchesRama && matchesCat && matchesSearch
        }
        _state.value = TorneosState.Success(filtered)
    }

    fun onSearchChange(query: String) {
        _busqueda.value = query
    }

    fun onRamaChange(rama: String) {
        _filtroRama.value = rama
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
