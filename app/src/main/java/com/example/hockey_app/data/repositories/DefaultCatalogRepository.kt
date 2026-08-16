package com.example.hockey_app.data.repositories

import com.example.hockey_app.data.models.ClubModel
import com.example.hockey_app.data.models.TorneoResumen
import com.example.hockey_app.data.services.DataService
import com.example.hockey_app.domain.catalog.CatalogRepository
import javax.inject.Inject

/** Asset-backed implementation of the catalog repository. */
class DefaultCatalogRepository @Inject constructor(
    private val dataService: DataService,
) : CatalogRepository {
    override suspend fun getClubes(): List<ClubModel> = dataService.getClubes()
    override suspend fun getTorneosResumen(): List<TorneoResumen> = dataService.getTorneosResumen()
}
