package com.example.hockey_app.features.auth.data

import android.content.Context
import androidx.core.content.edit
import com.example.hockey_app.data.models.UserModel
import com.example.hockey_app.features.auth.domain.repositories.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseAuthRepository @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val storage: Storage,
    @param:ApplicationContext private val context: Context,
) : AuthRepository {
    private val prefs = context.getSharedPreferences("hockey_auth", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override val sessionStatus = auth.sessionStatus
        .stateIn(serviceScope, SharingStarted.Eagerly, SessionStatus.Initializing)

    override val isUserLoggedIn: StateFlow<Boolean> = sessionStatus
        .map { it is SessionStatus.Authenticated }
        .stateIn(serviceScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        serviceScope.launch {
            auth.sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated) {
                    getCurrentUser()?.let { saveUserLocally(it) }
                }
            }
        }
    }

    override suspend fun signInWithEmail(email: String, pass: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Timber.d("Repository: Attempting login for $email")
            auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
            Timber.d("Repository: Login successful. Session: ${auth.currentSessionOrNull() != null}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Repository: Login failed")
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(email: String, pass: String, userMetadata: UserModel): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = auth.signUpWith(Email) {
                this.email = email
                this.password = pass
            }
            val userId = response?.id ?: throw Exception("ID de usuario nulo")
            val completeUser = userMetadata.copy(id = userId)
            
            if (auth.currentSessionOrNull() != null) {
                try {
                    postgrest.from("profiles").insert(completeUser)
                } catch (e: Exception) {
                    Timber.w("Perfil se sincronizará en el próximo login")
                }
            }
            saveUserLocally(completeUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signInWith(Google, redirectUrl = "hockeyapp://login-callback")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogleNative(idToken: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signInWith(IDToken) {
                this.idToken = idToken
                this.provider = Google
            }
            getCurrentUser()?.let { saveUserLocally(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signOut()
            clearLocalUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): UserModel? = withContext(Dispatchers.IO) {
        try {
            val session = auth.currentSessionOrNull() ?: return@withContext getLocalUser()
            val userId = session.user?.id ?: return@withContext getLocalUser()
            
            val profile = postgrest.from("profiles")
                .select(columns = Columns.ALL) {
                    filter { eq("id", userId) }
                }
                .decodeSingleOrNull<UserModel>()

            if (profile == null) {
                val user = session.user ?: return@withContext null
                val googleName = user.userMetadata?.get("full_name")?.toString()?.trim('"') ?: "Usuario"
                val googlePhoto = user.userMetadata?.get("avatar_url")?.toString()?.trim('"')

                val newUser = UserModel(id = userId, email = user.email ?: "", nombre = googleName, foto_url = googlePhoto)
                try {
                    postgrest.from("profiles").insert(newUser)
                    saveUserLocally(newUser)
                    return@withContext newUser
                } catch (e: Exception) {
                    return@withContext newUser
                }
            }
            
            saveUserLocally(profile)
            profile
        } catch (e: Exception) {
            getLocalUser()
        }
    }

    override suspend fun updateProfile(user: UserModel): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("profiles").update(user) {
                filter { eq("id", user.id) }
            }
            saveUserLocally(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfilePhoto(jpegBytes: ByteArray): Result<String> = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentSessionOrNull()?.user?.id ?: throw Exception("Sin sesión")
            val path = "$userId/profile.jpg"
            storage.from("avatars").upload(path, jpegBytes) { upsert = true }
            val publicUrl = storage.from("avatars").publicUrl(path)
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveUserLocally(user: UserModel) {
        prefs.edit(commit = true) {
            putString("last_logged_user", json.encodeToString(user))
        }
    }

    override fun getLocalUser(): UserModel? {
        val data = prefs.getString("last_logged_user", null) ?: return null
        return try { json.decodeFromString<UserModel>(data) } catch (_: Exception) { null }
    }

    private fun clearLocalUser() {
        prefs.edit(commit = true) { remove("last_logged_user") }
    }
}
