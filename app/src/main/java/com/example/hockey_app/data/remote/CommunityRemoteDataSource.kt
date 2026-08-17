package com.example.hockey_app.data.remote

import com.example.hockey_app.data.models.*
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityRemoteDataSource @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getJugadoresPorClub(clubId: String): List<UserModel> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("profiles")
                .select() {
                    filter { eq("club_id", clubId) }
                }
                .decodeList<UserModel>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun buscarJugadores(query: String): List<UserModel> = withContext(Dispatchers.IO) {
        try {
            if (query.isBlank()) {
                postgrest.from("profiles").select().decodeList<UserModel>()
            } else {
                postgrest.from("profiles")
                    .select() {
                        filter { ilike("nombre", "%$query%") }
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
}
