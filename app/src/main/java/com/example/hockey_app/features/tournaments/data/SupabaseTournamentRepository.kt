package com.example.hockey_app.features.tournaments.data

import com.example.hockey_app.data.models.*
import com.example.hockey_app.features.tournaments.domain.repositories.TournamentRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseTournamentRepository @Inject constructor(
    private val postgrest: Postgrest
) : TournamentRepository {

    override suspend fun getTorneos(): List<TorneoResumen> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("torneos")
                .select() {
                    order("nombre", order = Order.ASCENDING)
                }
                .decodeList<TorneoResumen>()
        } catch (e: Exception) {
            Timber.e(e, "Error fetching torneos")
            emptyList()
        }
    }

    override suspend fun getPosiciones(torneoId: String): List<PosicionAHBA> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("posiciones")
                .select {
                    filter { eq("torneo_id", torneoId) }
                    order("puesto", order = Order.ASCENDING)
                }
                .decodeList<PosicionAHBA>()
        } catch (e: Exception) {
            Timber.e(e, "Error fetching posiciones for $torneoId")
            emptyList()
        }
    }

    override suspend fun getPartidos(torneoId: String): List<PartidoAHBA> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("partidos")
                .select {
                    filter { eq("torneo_id", torneoId) }
                    order("numero_fecha", order = Order.ASCENDING)
                }
                .decodeList<PartidoAHBA>()
        } catch (e: Exception) {
            Timber.e(e, "Error fetching partidos for $torneoId")
            emptyList()
        }
    }

    override suspend fun getGoleadores(torneoId: String): List<GoleadorAHBA> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("goleadores")
                .select {
                    filter { eq("torneo_id", torneoId) }
                    order("goles", order = Order.DESCENDING)
                }
                .decodeList<GoleadorAHBA>()
        } catch (e: Exception) {
            Timber.e(e, "Error fetching goleadores for $torneoId")
            emptyList()
        }
    }

    override suspend fun getPartido(partidoId: String): PartidoAHBA? = withContext(Dispatchers.IO) {
        try {
            postgrest.from("partidos")
                .select() {
                    filter { eq("id", partidoId) }
                }
                .decodeSingleOrNull<PartidoAHBA>()
        } catch (e: Exception) {
            null
        }
    }
}
