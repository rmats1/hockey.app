package com.example.hockey_app.data.repositories

import com.example.hockey_app.data.models.*
import com.example.hockey_app.data.services.SupabaseService
import com.example.hockey_app.domain.competition.CompetitionRepository
import com.example.hockey_app.features.tournaments.domain.repositories.TournamentRepository
import com.example.hockey_app.features.news.domain.repositories.NewsRepository
import com.example.hockey_app.features.community.domain.repositories.CommunityRepository
import com.example.hockey_app.features.training.domain.repositories.TrainingRepository
import javax.inject.Inject

/** 
 * Supabase-backed implementation of competition data access.
 * Delegating to specialized feature repositories for scalability.
 */
class DefaultCompetitionRepository @Inject constructor(
    private val tournamentRepository: TournamentRepository,
    private val newsRepository: NewsRepository,
    private val communityRepository: CommunityRepository,
    private val trainingRepository: TrainingRepository,
    private val supabaseService: SupabaseService
) : CompetitionRepository {
    override suspend fun getTorneos() = tournamentRepository.getTorneos()
    override suspend fun getPosiciones(torneoId: String) = tournamentRepository.getPosiciones(torneoId)
    override suspend fun getPartidos(torneoId: String) = tournamentRepository.getPartidos(torneoId)
    override suspend fun getGoleadores(torneoId: String) = tournamentRepository.getGoleadores(torneoId)
    override suspend fun getPartido(partidoId: String) = tournamentRepository.getPartido(partidoId)
    
    override suspend fun getNoticias() = newsRepository.getNoticias()
    
    override suspend fun getJugadoresPorClub(clubId: String) = communityRepository.getJugadoresPorClub(clubId)
    override suspend fun buscarJugadores(query: String) = communityRepository.buscarJugadores(query)
    override suspend fun getComentarios(partidoId: String) = communityRepository.getComentarios(partidoId)
    override suspend fun postComentario(comentario: ComentarioModel) = communityRepository.postComentario(comentario)
    override suspend fun getPredicciones(partidoId: String) = communityRepository.getPredicciones(partidoId)
    override suspend fun postPrediccion(prediccion: PrediccionModel) = communityRepository.postPrediccion(prediccion)
    override suspend fun getFavoritos(userId: String) = communityRepository.getFavoritos(userId)
    override suspend fun postFavorito(favorito: FavoritoModel) = communityRepository.postFavorito(favorito)
    override suspend fun deleteFavorito(userId: String, clubId: String) = communityRepository.deleteFavorito(userId, clubId)
    
    override suspend fun getLatestTrainingPlan(clubId: String, categoria: String, division: String?) =
        trainingRepository.getLatestTrainingPlan(clubId, categoria, division)
    override suspend fun getUserCallUp(userId: String) = trainingRepository.getUserCallUp(userId)
    override suspend fun postCallUps(callUps: List<CallUpModel>) = trainingRepository.postCallUps(callUps)
    override suspend fun postTrainingPlan(plan: TrainingPlanModel) = trainingRepository.postTrainingPlan(plan)

    // Fixed: Now properly delegates to SupabaseService
    override suspend fun getClubesFromSupabase(): List<ClubModel> =
        supabaseService.getClubesFromSupabase()
}
