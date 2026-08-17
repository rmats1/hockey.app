package com.example.hockey_app.features.community.data

import com.example.hockey_app.data.models.*
import com.example.hockey_app.data.remote.CommunityRemoteDataSource
import com.example.hockey_app.features.community.domain.repositories.CommunityRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseCommunityRepository @Inject constructor(
    private val remoteDataSource: CommunityRemoteDataSource
) : CommunityRepository {

    override suspend fun getJugadoresPorClub(clubId: String): List<UserModel> = 
        remoteDataSource.getJugadoresPorClub(clubId)

    override suspend fun buscarJugadores(query: String): List<UserModel> = 
        remoteDataSource.buscarJugadores(query)

    override suspend fun getComentarios(partidoId: String): List<ComentarioModel> = 
        remoteDataSource.getComentarios(partidoId)

    override suspend fun postComentario(comentario: ComentarioModel): Boolean = 
        remoteDataSource.postComentario(comentario)

    override suspend fun getPredicciones(partidoId: String): List<PrediccionModel> = 
        remoteDataSource.getPredicciones(partidoId)

    override suspend fun postPrediccion(prediccion: PrediccionModel): Boolean = 
        remoteDataSource.postPrediccion(prediccion)

    override suspend fun getFavoritos(userId: String): List<FavoritoModel> = 
        remoteDataSource.getFavoritos(userId)

    override suspend fun postFavorito(favorito: FavoritoModel): Boolean = 
        remoteDataSource.postFavorito(favorito)

    override suspend fun deleteFavorito(userId: String, clubId: String): Boolean = 
        remoteDataSource.deleteFavorito(userId, clubId)
}
