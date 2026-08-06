package com.example.hockey_app.data.services

import com.example.hockey_app.data.models.*
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseService @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getPosiciones(torneoId: String): List<PosicionAHBA> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("posiciones")
                .select() {
                    filter {
                        eq("torneo_id", torneoId)
                    }
                    order("puesto", order = Order.ASCENDING)
                }
                .decodeList<PosicionAHBA>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPartidos(torneoId: String): List<PartidoAHBA> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("partidos")
                .select() {
                    filter {
                        eq("torneo_id", torneoId)
                    }
                    order("numero_fecha", order = Order.ASCENDING)
                }
                .decodeList<PartidoAHBA>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getGoleadores(torneoId: String): List<GoleadorAHBA> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("goleadores")
                .select() {
                    filter {
                        eq("torneo_id", torneoId)
                    }
                    order("goles", order = Order.DESCENDING)
                }
                .decodeList<GoleadorAHBA>()
        } catch (e: Exception) {
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
}
