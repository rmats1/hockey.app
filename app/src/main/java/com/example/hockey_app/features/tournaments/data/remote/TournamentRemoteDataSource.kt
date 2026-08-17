package com.example.hockey_app.features.tournaments.data.remote

import com.example.hockey_app.data.models.*
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TournamentRemoteDataSource @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getTorneos(): List<TorneoResumen> = withContext(Dispatchers.IO) {
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

    suspend fun getPosiciones(torneoId: String): List<PosicionAHBA> = withContext(Dispatchers.IO) {
        try {
            val rows = postgrest.from("posiciones")
                .select {
                    filter { eq("torneo_id", torneoId) }
                    order("posicion", order = Order.ASCENDING)
                }
                .decodeList<JsonObject>()

            rows.mapNotNull { row ->
                val club = row["equipo"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                PosicionAHBA(
                    puesto = row["posicion"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0,
                    clubNombre = club,
                    escudoUrl = row["escudo"]?.jsonPrimitive?.contentOrNull,
                    puntos = row["puntos"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0,
                    partidosJugados = row["pj"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0,
                    partidosGanados = row["pg"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0,
                    partidosEmpatados = row["pe"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0,
                    partidosPerdidos = row["pp"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0,
                    golesAFavor = row["gf"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0,
                    golesEnContra = row["gc"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching posiciones")
            emptyList()
        }
    }

    suspend fun getPartidos(torneoId: String): List<PartidoAHBA> = withContext(Dispatchers.IO) {
        try {
            val rows = postgrest.from("partidos")
                .select {
                    filter { eq("torneo_id", torneoId) }
                    order("fecha", order = Order.ASCENDING)
                }
                .decodeList<JsonObject>()

            rows.mapNotNull { row ->
                val id = row["id"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                val local = row["equipo_local"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                val visitante = row["equipo_visita"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                PartidoAHBA(
                    id = id,
                    torneoId = torneoId,
                    nombreLocal = local,
                    nombreVisitante = visitante,
                    escudoLocal = row["escudo_local"]?.jsonPrimitive?.contentOrNull,
                    escudoVisitante = row["escudo_visita"]?.jsonPrimitive?.contentOrNull,
                    golesLocal = row["goles_local"]?.jsonPrimitive?.contentOrNull?.toIntOrNull(),
                    golesVisitante = row["goles_visita"]?.jsonPrimitive?.contentOrNull?.toIntOrNull(),
                    fechaHora = row["fecha"]?.jsonPrimitive?.contentOrNull,
                    numeroFecha = row["numero_fecha"]?.jsonPrimitive?.contentOrNull ?: "",
                    jugado = row["jugado"]?.jsonPrimitive?.booleanOrNull ?: false
                )
            }.sortedWith(
                compareBy<PartidoAHBA> { chronologicalKey(it.fechaHora) }
                    .thenBy { it.numeroFecha.toIntOrNull() ?: Int.MAX_VALUE }
            )
        } catch (e: Exception) {
            Timber.e(e, "Error fetching partidos")
            emptyList()
        }
    }

    private fun chronologicalKey(value: String?): String {
        if (value.isNullOrBlank()) return "9999-99-99 99:99:99"
        val shortDate = Regex("""(\d{1,2})/(\d{1,2})/(\d{4})""").find(value)
        if (shortDate != null) {
            val (day, month, year) = shortDate.destructured
            return "$year-${month.padStart(2, '0')}-${day.padStart(2, '0')} ${value.substringAfter(shortDate.value, "").trim()}"
        }
        return value
    }

    suspend fun getGoleadores(torneoId: String): List<GoleadorAHBA> = withContext(Dispatchers.IO) {
        try {
            val rows = postgrest.from("goleadores")
                .select {
                    filter { eq("torneo_id", torneoId) }
                    order("goles", order = Order.DESCENDING)
                }
                .decodeList<JsonObject>()

            rows.mapNotNull { row ->
                val nombre = row["jugador_nombre"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
                GoleadorAHBA(
                    torneoId = torneoId,
                    nombreCompleto = nombre,
                    clubNombre = row["club_nombre"]?.jsonPrimitive?.contentOrNull ?: "",
                    fotoUrl = row["foto_url"]?.jsonPrimitive?.contentOrNull,
                    goles = row["goles"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: 0
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching goleadores")
            emptyList()
        }
    }

    suspend fun getPartido(partidoId: String): PartidoAHBA? = withContext(Dispatchers.IO) {
        try {
            postgrest.from("partidos")
                .select() {
                    filter { eq("id", partidoId) }
                }
                .decodeSingleOrNull<PartidoAHBA>()
        } catch (e: Exception) {
            Timber.e(e, "Error fetching partido $partidoId")
            null
        }
    }
}
