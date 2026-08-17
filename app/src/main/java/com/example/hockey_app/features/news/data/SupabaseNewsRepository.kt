package com.example.hockey_app.features.news.data

import com.example.hockey_app.data.models.NewsModel
import com.example.hockey_app.features.news.data.remote.NewsRemoteDataSource
import com.example.hockey_app.features.news.domain.repositories.NewsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseNewsRepository @Inject constructor(
    private val remoteDataSource: NewsRemoteDataSource
) : NewsRepository {

    override suspend fun getNoticias(): List<NewsModel> = remoteDataSource.getNoticias()
}
