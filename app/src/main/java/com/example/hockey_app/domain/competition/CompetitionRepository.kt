package com.example.hockey_app.domain.competition

import com.example.hockey_app.data.models.*

/** Boundary for remote competition, team and social data. */
interface CompetitionRepository {
    suspend fun getTorneos(): List<TorneoResumen>
    suspend fun getClubesFromSupabase(): List<ClubModel>
    suspend fun getPosiciones(torneoId: String): List<PosicionAHBA>
    suspend fun getPartidos(torneoId: String): List<PartidoAHBA>
    suspend fun getGoleadores(torneoId: String): List<GoleadorAHBA>
    suspend fun getNoticias(): List<NewsModel>
    suspend fun getJugadoresPorClub(clubId: String): List<UserModel>
    suspend fun buscarJugadores(query: String): List<UserModel>
    suspend fun getComentarios(partidoId: String): List<ComentarioModel>
    suspend fun postComentario(comentario: ComentarioModel): Boolean
    suspend fun getPredicciones(partidoId: String): List<PrediccionModel>
    suspend fun postPrediccion(prediccion: PrediccionModel): Boolean
    suspend fun getLatestTrainingPlan(clubId: String, categoria: String, division: String?): TrainingPlanModel?
    suspend fun getUserCallUp(userId: String): CallUpModel?
    suspend fun postCallUps(callUps: List<CallUpModel>): Boolean
    suspend fun getPartido(partidoId: String): PartidoAHBA?
    suspend fun getFavoritos(userId: String): List<FavoritoModel>
    suspend fun postFavorito(favorito: FavoritoModel): Boolean
    suspend fun deleteFavorito(userId: String, clubId: String): Boolean
    suspend fun postTrainingPlan(plan: TrainingPlanModel): Boolean
}
