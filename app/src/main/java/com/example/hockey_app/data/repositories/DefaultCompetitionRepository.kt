package com.example.hockey_app.data.repositories

import com.example.hockey_app.data.models.*
import com.example.hockey_app.data.services.SupabaseService
import com.example.hockey_app.domain.competition.CompetitionRepository
import javax.inject.Inject

/** Supabase-backed implementation of competition data access. */
class DefaultCompetitionRepository @Inject constructor(
    private val supabaseService: SupabaseService,
) : CompetitionRepository {
    override suspend fun getTorneos() = supabaseService.getTorneos()
    override suspend fun getClubesFromSupabase() = supabaseService.getClubesFromSupabase()
    override suspend fun getPosiciones(torneoId: String) = supabaseService.getPosiciones(torneoId)
    override suspend fun getPartidos(torneoId: String) = supabaseService.getPartidos(torneoId)
    override suspend fun getGoleadores(torneoId: String) = supabaseService.getGoleadores(torneoId)
    override suspend fun getNoticias() = supabaseService.getNoticias()
    override suspend fun getJugadoresPorClub(clubId: String) = supabaseService.getJugadoresPorClub(clubId)
    override suspend fun buscarJugadores(query: String) = supabaseService.buscarJugadores(query)
    override suspend fun getComentarios(partidoId: String) = supabaseService.getComentarios(partidoId)
    override suspend fun postComentario(comentario: ComentarioModel) = supabaseService.postComentario(comentario)
    override suspend fun getPredicciones(partidoId: String) = supabaseService.getPredicciones(partidoId)
    override suspend fun postPrediccion(prediccion: PrediccionModel) = supabaseService.postPrediccion(prediccion)
    override suspend fun getLatestTrainingPlan(clubId: String, categoria: String, division: String?) =
        supabaseService.getLatestTrainingPlan(clubId, categoria, division)
    override suspend fun getUserCallUp(userId: String) = supabaseService.getUserCallUp(userId)
    override suspend fun postCallUps(callUps: List<CallUpModel>) = supabaseService.postCallUps(callUps)
    override suspend fun getPartido(partidoId: String) = supabaseService.getPartido(partidoId)
    override suspend fun getFavoritos(userId: String) = supabaseService.getFavoritos(userId)
    override suspend fun postFavorito(favorito: FavoritoModel) = supabaseService.postFavorito(favorito)
    override suspend fun deleteFavorito(userId: String, clubId: String) = supabaseService.deleteFavorito(userId, clubId)
    override suspend fun postTrainingPlan(plan: TrainingPlanModel) = supabaseService.postTrainingPlan(plan)
}
