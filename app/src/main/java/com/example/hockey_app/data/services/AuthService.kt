package com.example.hockey_app.data.services

import android.content.Context
import androidx.core.content.edit
import com.example.hockey_app.data.models.UserModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest,
    @param:ApplicationContext private val context: Context,
) {
    private val prefs = context.getSharedPreferences("hockey_auth", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun signInWithEmail(email: String, pass: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
            // Guardar perfil localmente para biometría/acceso rápido
            getCurrentUser()?.let { saveUserLocally(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signInWith(Google)
            getCurrentUser()?.let { saveUserLocally(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String, userMetadata: UserModel): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = auth.signUpWith(Email) {
                this.email = email
                this.password = pass
            }
            
            val userId = response?.id ?: throw Exception("Error al obtener ID de usuario")
            
            // Insertar perfil en la tabla 'profiles'
            val completeUser = userMetadata.copy(id = userId)
            postgrest.from("profiles").insert(completeUser)
            saveUserLocally(completeUser)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeProfile(user: UserModel): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("profiles").update(user) {
                filter {
                    eq("id", user.id)
                }
            }
            saveUserLocally(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): UserModel? = withContext(Dispatchers.IO) {
        try {
            val session = auth.currentSessionOrNull() ?: return@withContext null
            val userId = session.user?.id ?: return@withContext null
            
            postgrest.from("profiles")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<UserModel>()
        } catch (ignore: Exception) {
            null
        }
    }

    suspend fun isLoggedIn(): Boolean = withContext(Dispatchers.IO) {
        auth.currentSessionOrNull() != null
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        auth.signOut()
        clearLocalUser()
    }

    private fun saveUserLocally(user: UserModel) {
        prefs.edit(commit = true) {
            putString("last_logged_user", json.encodeToString(user))
        }
    }

    fun getLocalUser(): UserModel? {
        val data = prefs.getString("last_logged_user", null) ?: return null
        return try { json.decodeFromString<UserModel>(data) } catch (_: Exception) { null }
    }

    private fun clearLocalUser() {
        prefs.edit(commit = true) {
            remove("last_logged_user")
        }
    }
}
