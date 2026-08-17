package com.example.hockey_app.features.community.domain.repositories

import com.example.hockey_app.data.models.*

interface CommunityRepository {
    suspend fun getJugadoresPorClub(clubId: String): List<UserModel>
    suspend fun buscarJugadores(query: String): List<UserModel>
    suspend fun getComentarios(partidoId: String): List<ComentarioModel>
    suspend fun postComentario(comentario: ComentarioModel): Boolean
    suspend fun getPredicciones(partidoId: String): List<PrediccionModel>
    suspend fun postPrediccion(prediccion: PrediccionModel): Boolean
    suspend fun getFavoritos(userId: String): List<FavoritoModel>
    suspend fun postFavorito(favorito: FavoritoModel): Boolean
    suspend fun deleteFavorito(userId: String, clubId: String): Boolean
}
