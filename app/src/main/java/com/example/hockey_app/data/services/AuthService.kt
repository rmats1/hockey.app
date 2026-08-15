package com.example.hockey_app.data.services

import android.content.Context
import androidx.core.content.edit
import com.example.hockey_app.data.models.UserModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val storage: Storage,
    @param:ApplicationContext private val context: Context,
) {
    private val prefs = context.getSharedPreferences("hockey_auth", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val sessionStatus: StateFlow<SessionStatus> = auth.sessionStatus
        .stateIn(serviceScope, SharingStarted.Eagerly, SessionStatus.Initializing)

    val isUserLoggedIn: StateFlow<Boolean> = sessionStatus
        .map { it is SessionStatus.Authenticated }
        .stateIn(serviceScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        // Observar cambios de sesión para guardar perfil local
        serviceScope.launch {
            auth.sessionStatus.collect { status ->
                Timber.d("Supabase Auth Status Change: ${status::class.simpleName}")
                if (status is SessionStatus.Authenticated) {
                    Timber.d("User authenticated, updating local profile...")
                    getCurrentUser()?.let { saveUserLocally(it) }
                }
            }
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Timber.d("Attempting login for: $email")
            auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
            Timber.d("Login successful!")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Login failed")
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signInWith(Google, redirectUrl = "hockeyapp://login-callback")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogleNative(idToken: String): Result<Unit> = withContext(Dispatchers.IO) {
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

    suspend fun signUpWithEmail(email: String, pass: String, userMetadata: UserModel): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Timber.d("Attempting register for: $email")
            val response = auth.signUpWith(Email) {
                this.email = email
                this.password = pass
            }            
            val userId = response?.id ?: throw Exception("Error al obtener ID de usuario")
            val completeUser = userMetadata.copy(id = userId)

            if (auth.currentSessionOrNull() != null) {
                try {
                    postgrest.from("profiles").insert(completeUser)
                    Timber.d("Profile created in DB during signup")
                } catch (profileError: Exception) {
                    Timber.w(profileError, "Perfil pendiente de sincronización tras el alta")
                }
            } else {
                Timber.d("Signup success, but no session yet (pending confirmation?)")
            }
            saveUserLocally(completeUser)

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Signup failed")
            Result.failure(e)
        }
    }

    suspend fun uploadProfilePhoto(jpegBytes: ByteArray): Result<String> = withContext(Dispatchers.IO) {
        try {
            val sessionUser = auth.currentSessionOrNull()?.user
                ?: throw IllegalStateException("La sesión expiró. Iniciá sesión nuevamente.")
            val profile = getCurrentUser() ?: throw IllegalStateException("No se encontró el perfil")
            val path = "${sessionUser.id}/profile.jpg"
            storage.from("avatars").upload(path, jpegBytes) {
                upsert = true
            }
            val publicUrl = storage.from("avatars").publicUrl(path)
            val updated = profile.copy(foto_url = publicUrl)
            updateProfile(updated).getOrThrow()
            Result.success(publicUrl)
        } catch (e: Exception) {
            Timber.e(e, "Error al subir foto de perfil")
            Result.failure(e)
        }
    }

    suspend fun updateProfile(user: UserModel): Result<Unit> = withContext(Dispatchers.IO) {
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

    suspend fun getCurrentUser(): UserModel? = withContext(Dispatchers.IO) {
        try {
            val session = auth.currentSessionOrNull() ?: return@withContext getLocalUser()
            val user = session.user ?: return@withContext getLocalUser()
            val userId = user.id
            
            Timber.d("getCurrentUser: Fetching profile for ID $userId")
            
            val profile = postgrest.from("profiles")
                .select(columns = Columns.ALL) {
                    filter { eq("id", userId) }
                }
                .decodeSingleOrNull<UserModel>()

            if (profile != null) {
                saveUserLocally(profile)
                Timber.d("Profile fetched and saved locally")
                return@withContext profile
            } else {
                Timber.w("No profile found in DB for ID $userId")
                return@withContext getLocalUser()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in getCurrentUser")
            getLocalUser()
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
