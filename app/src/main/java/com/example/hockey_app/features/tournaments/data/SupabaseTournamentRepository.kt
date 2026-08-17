package com.example.hockey_app.features.tournaments.data

import com.example.hockey_app.data.models.*
import com.example.hockey_app.features.tournaments.data.remote.TournamentRemoteDataSource
import com.example.hockey_app.features.tournaments.domain.repositories.TournamentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseTournamentRepository @Inject constructor(
    private val remoteDataSource: TournamentRemoteDataSource
) : TournamentRepository {

    override suspend fun getTorneos(): List<TorneoResumen> = remoteDataSource.getTorneos()

    override suspend fun getPosiciones(torneoId: String): List<PosicionAHBA> = 
        remoteDataSource.getPosiciones(torneoId)

    override suspend fun getPartidos(torneoId: String): List<PartidoAHBA> = 
        remoteDataSource.getPartidos(torneoId)

    override suspend fun getGoleadores(torneoId: String): List<GoleadorAHBA> = 
        remoteDataSource.getGoleadores(torneoId)

    override suspend fun getPartido(partidoId: String): PartidoAHBA? {
        // Simple logic for single match could also be moved to data source if needed
        return remoteDataSource.getPartido(partidoId) 
        // Note: The above is a temporary simplification, a real implementation would use a direct call.
    }
}
