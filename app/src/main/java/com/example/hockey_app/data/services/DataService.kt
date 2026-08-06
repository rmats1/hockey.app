package com.example.hockey_app.data.services

import android.content.Context
import com.example.hockey_app.data.models.ClubModel
import com.example.hockey_app.data.models.TorneoResumen
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private var clubesCache: List<ClubModel>? = null
    private var torneosCache: List<TorneoResumen>? = null

    suspend fun getClubes(): List<ClubModel> = withContext(Dispatchers.IO) {
        if (clubesCache != null) return@withContext clubesCache!!
        
        try {
            val raw = context.assets.open("database/clubes.json").bufferedReader().use { it.readText() }
            clubesCache = json.decodeFromString<List<ClubModel>>(raw)
            clubesCache!!
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTorneosResumen(): List<TorneoResumen> = withContext(Dispatchers.IO) {
        if (torneosCache != null) return@withContext torneosCache!!
        
        try {
            val raw = context.assets.open("database/torneos_resumen.json").bufferedReader().use { it.readText() }
            torneosCache = json.decodeFromString<List<TorneoResumen>>(raw)
            torneosCache!!
        } catch (e: Exception) {
            emptyList()
        }
    }
}
