package com.example.hockey_app.features.news.data

import com.example.hockey_app.data.models.NewsModel
import com.example.hockey_app.features.news.domain.repositories.NewsRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseNewsRepository @Inject constructor(
    private val postgrest: Postgrest
) : NewsRepository {

    override suspend fun getNoticias(): List<NewsModel> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("noticias")
                .select() {
                    order("fecha_publicacion", order = Order.DESCENDING)
                }
                .decodeList<NewsModel>()
        } catch (e: Exception) {
            Timber.e(e, "Error fetching noticias from Repository")
            emptyList()
        }
    }
}
