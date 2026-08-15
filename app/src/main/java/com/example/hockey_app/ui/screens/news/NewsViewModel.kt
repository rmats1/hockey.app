package com.example.hockey_app.ui.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.NewsModel
import com.example.hockey_app.domain.competition.CompetitionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NewsState {
    object Loading : NewsState()
    data class Success(val news: List<NewsModel>) : NewsState()
    data class Error(val message: String) : NewsState()
}

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val supabaseService: CompetitionRepository
) : ViewModel() {

    private val _state = MutableStateFlow<NewsState>(NewsState.Loading)
    val state: StateFlow<NewsState> = _state

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch {
            _state.value = NewsState.Loading
            try {
                val news = supabaseService.getNoticias()
                _state.value = NewsState.Success(news)
            } catch (e: Exception) {
                _state.value = NewsState.Error(e.message ?: "Error al cargar noticias")
            }
        }
    }
}
