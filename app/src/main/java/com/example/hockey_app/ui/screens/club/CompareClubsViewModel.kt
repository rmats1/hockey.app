package com.example.hockey_app.ui.screens.club

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.ClubModel
import com.example.hockey_app.data.services.DataService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareClubsViewModel @Inject constructor(
    private val dataService: DataService
) : ViewModel() {

    private val _clubes = MutableStateFlow<List<ClubModel>>(emptyList())
    val clubes: StateFlow<List<ClubModel>> = _clubes

    init {
        loadClubes()
    }

    private fun loadClubes() {
        viewModelScope.launch {
            _clubes.value = dataService.getClubes()
        }
    }
}
