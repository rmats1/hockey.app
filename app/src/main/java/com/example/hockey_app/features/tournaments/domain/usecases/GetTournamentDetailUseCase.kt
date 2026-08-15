package com.example.hockey_app.features.tournaments.domain.usecases

import com.example.hockey_app.data.models.GoleadorAHBA
import com.example.hockey_app.data.models.PartidoAHBA
import com.example.hockey_app.data.models.PosicionAHBA
import com.example.hockey_app.features.tournaments.domain.repositories.TournamentRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

data class TournamentDetail(
    val posiciones: List<PosicionAHBA>,
    val partidos: List<PartidoAHBA>,
    val goleadores: List<GoleadorAHBA>
)

class GetTournamentDetailUseCase @Inject constructor(
    private val repository: TournamentRepository
) {
    suspend operator fun invoke(torneoId: String): Result<TournamentDetail> = coroutineScope {
        try {
            val posicionesDef = async { repository.getPosiciones(torneoId) }
            val partidosDef = async { repository.getPartidos(torneoId) }
            val goleadoresDef = async { repository.getGoleadores(torneoId) }

            Result.success(
                TournamentDetail(
                    posiciones = posicionesDef.await(),
                    partidos = partidosDef.await(),
                    goleadores = goleadoresDef.await()
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
