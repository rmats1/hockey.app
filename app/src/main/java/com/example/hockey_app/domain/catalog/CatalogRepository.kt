package com.example.hockey_app.domain.catalog

import com.example.hockey_app.data.models.ClubModel
import com.example.hockey_app.data.models.TorneoResumen

/** Boundary for bundled catalog data. */
interface CatalogRepository {
    suspend fun getClubes(): List<ClubModel>
    suspend fun getTorneosResumen(): List<TorneoResumen>
}
