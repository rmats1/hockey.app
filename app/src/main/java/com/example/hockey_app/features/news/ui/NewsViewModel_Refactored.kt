package com.example.hockey_app.features.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey_app.data.models.NewsModel
import com.example.hockey_app.features.news.domain.usecases.GetNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NewsState_Refactored {
    object Loading : NewsState_Refactored()
    data class Success(val news: List<NewsModel>) : NewsState_Refactored()
    data class Error(val message: String) : NewsState_Refactored()
}

@HiltViewModel
class NewsViewModel_Refactored @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<NewsState_Refactored>(NewsState_Refactored.Loading)
    val state: StateFlow<NewsState_Refactored> = _state

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch {
            _state.value = NewsState_Refactored.Loading
            try {
                val news = getNewsUseCase()
                _state.value = NewsState_Refactored.Success(news)
            } catch (e: Exception) {
                _state.value = NewsState_Refactored.Error(e.message ?: "Error al cargar noticias")
            }
        }
    }
}
