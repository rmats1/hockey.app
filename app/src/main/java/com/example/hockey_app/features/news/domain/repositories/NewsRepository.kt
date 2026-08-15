package com.example.hockey_app.features.news.domain.repositories

import com.example.hockey_app.data.models.NewsModel

interface NewsRepository {
    suspend fun getNoticias(): List<NewsModel>
}
