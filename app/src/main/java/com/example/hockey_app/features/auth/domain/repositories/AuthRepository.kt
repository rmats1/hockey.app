package com.example.hockey_app.features.auth.domain.repositories

import com.example.hockey_app.data.models.UserModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface unificada para la gestión de autenticación.
 * Basada en los estándares de la auditoría técnica.
 */
interface AuthRepository {
    val sessionStatus: StateFlow<SessionStatus>
    val isUserLoggedIn: StateFlow<Boolean>

    suspend fun signInWithEmail(email: String, pass: String): Result<Unit>
    suspend fun signUpWithEmail(email: String, pass: String, userMetadata: UserModel): Result<Unit>
    suspend fun signInWithGoogle(): Result<Unit>
    suspend fun signInWithGoogleNative(idToken: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): UserModel?
    suspend fun updateProfile(user: UserModel): Result<Unit>
    suspend fun uploadProfilePhoto(jpegBytes: ByteArray): Result<String>
    
    // Métodos de utilidad local
    fun getLocalUser(): UserModel?
}
