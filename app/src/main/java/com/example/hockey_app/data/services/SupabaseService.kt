package com.example.hockey_app.data.services

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
class SupabaseService @Inject constructor(
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

    suspend fun getNoticias(): List<NewsModel> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("noticias")
                .select() {
                    order("fecha_publicacion", order = Order.DESCENDING)
                }
                .decodeList<NewsModel>()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getJugadoresPorClub(clubId: String): List<UserModel> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("profiles")
                .select() {
                    filter {
                        eq("club_id", clubId)
                    }
                }
                .decodeList<UserModel>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun buscarJugadores(query: String): List<UserModel> = withContext(Dispatchers.IO) {
        try {
            if (query.isBlank()) {
                postgrest.from("profiles")
                    .select()
                    .decodeList<UserModel>()
            } else {
                postgrest.from("profiles")
                    .select() {
                        filter {
                            ilike("nombre", "%$query%")
                        }
                    }
                    .decodeList<UserModel>()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getComentarios(partidoId: String): List<ComentarioModel> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("comentarios")
                .select() {
                    filter { eq("partido_id", partidoId) }
                    order("fecha", order = Order.DESCENDING)
                }
                .decodeList<ComentarioModel>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun postComentario(comentario: ComentarioModel): Boolean = withContext(Dispatchers.IO) {
        try {
            postgrest.from("comentarios").insert(comentario)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getPredicciones(partidoId: String): List<PrediccionModel> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("predicciones")
                .select() {
                    if (partidoId != "global") {
                        filter { eq("partido_id", partidoId) }
                    }
                    order("fecha", order = Order.DESCENDING)
                }
                .decodeList<PrediccionModel>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun postPrediccion(prediccion: PrediccionModel): Boolean = withContext(Dispatchers.IO) {
        try {
            postgrest.from("predicciones").insert(prediccion)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getLatestTrainingPlan(clubId: String, categoria: String, division: String?): TrainingPlanModel? = withContext(Dispatchers.IO) {
        try {
            postgrest.from("training_plans")
                .select() {
                    filter {
                        eq("club_id", clubId)
                        eq("categoria", categoria)
                        if (division != null) eq("division", division)
                        eq("activo", true)
                    }
                    order("fecha_creacion", order = Order.DESCENDING)
                    limit(1)
                }
                .decodeSingleOrNull<TrainingPlanModel>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserCallUp(userId: String): CallUpModel? = withContext(Dispatchers.IO) {
        try {
            postgrest.from("citaciones")
                .select() {
                    filter {
                        eq("user_id", userId)
                    }
                    order("fecha_partido", order = Order.DESCENDING)
                    limit(1)
                }
                .decodeSingleOrNull<CallUpModel>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun postCallUps(callUps: List<CallUpModel>): Boolean = withContext(Dispatchers.IO) {
        try {
            postgrest.from("citaciones").insert(callUps)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getPartido(partidoId: String): PartidoAHBA? = withContext(Dispatchers.IO) {
        try {
            postgrest.from("partidos")
                .select() {
                    filter {
                        eq("id", partidoId)
                    }
                }
                .decodeSingleOrNull<PartidoAHBA>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getFavoritos(userId: String): List<FavoritoModel> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("favoritos")
                .select() {
                    filter { eq("user_id", userId) }
                }
                .decodeList<FavoritoModel>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun postFavorito(favorito: FavoritoModel): Boolean = withContext(Dispatchers.IO) {
        try {
            postgrest.from("favoritos").insert(favorito)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteFavorito(userId: String, clubId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            postgrest.from("favoritos").delete {
                filter {
                    eq("user_id", userId)
                    eq("club_id", clubId)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun postTrainingPlan(plan: TrainingPlanModel): Boolean = withContext(Dispatchers.IO) {
        try {
            postgrest.from("training_plans").insert(plan)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getClubesFromSupabase(): List<ClubModel> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("clubes")
                .select() {
                    order("nombre", order = Order.ASCENDING)
                }
                .decodeList<ClubModel>()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
