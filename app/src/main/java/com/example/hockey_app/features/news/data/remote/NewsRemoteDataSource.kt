package com.example.hockey_app.features.news.data.remote

import com.example.hockey_app.data.models.NewsModel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRemoteDataSource @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getNoticias(): List<NewsModel> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("noticias")
                .select() {
                    order("fecha_publicacion", order = Order.DESCENDING)
                }
                .decodeList<NewsModel>()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
