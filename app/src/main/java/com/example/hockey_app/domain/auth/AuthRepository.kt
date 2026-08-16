package com.example.hockey_app.domain.auth

import com.example.hockey_app.data.models.UserModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow

/** Boundary between authentication use cases and the Supabase implementation. */
interface AuthRepository {
    val sessionStatus: StateFlow<SessionStatus>
    val isUserLoggedIn: StateFlow<Boolean>

    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun signInWithGoogle(): Result<Unit>
    suspend fun signInWithGoogleNative(idToken: String): Result<Unit>

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        user: UserModel,
    ): Result<Unit>

    suspend fun uploadProfilePhoto(jpegBytes: ByteArray): Result<String>
    suspend fun updateProfile(user: UserModel): Result<Unit>
    suspend fun getCurrentUser(): UserModel?
    suspend fun isLoggedIn(): Boolean
    suspend fun logout()
    fun getLocalUser(): UserModel?
}
