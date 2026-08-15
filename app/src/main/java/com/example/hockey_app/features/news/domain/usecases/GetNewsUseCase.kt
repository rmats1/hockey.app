package com.example.hockey_app.features.news.domain.usecases

import com.example.hockey_app.data.models.NewsModel
import com.example.hockey_app.features.news.domain.repositories.NewsRepository
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(): List<NewsModel> {
        return repository.getNoticias()
    }
}
