package com.example.hockey_app.data.repositories

import com.example.hockey_app.data.models.UserModel
import com.example.hockey_app.data.services.AuthService
import com.example.hockey_app.domain.auth.AuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** Supabase-backed implementation of the authentication repository. */
class DefaultAuthRepository @Inject constructor(
    private val authService: AuthService,
) : AuthRepository {
    override val sessionStatus: StateFlow<SessionStatus>
        get() = authService.sessionStatus

    override val isUserLoggedIn: StateFlow<Boolean>
        get() = authService.isUserLoggedIn

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        authService.signInWithEmail(email, password)

    override suspend fun signInWithGoogle(): Result<Unit> = authService.signInWithGoogle()

    override suspend fun signInWithGoogleNative(idToken: String): Result<Unit> =
        authService.signInWithGoogleNative(idToken)

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        user: UserModel,
    ): Result<Unit> = authService.signUpWithEmail(email, password, user)

    override suspend fun uploadProfilePhoto(jpegBytes: ByteArray): Result<String> =
        authService.uploadProfilePhoto(jpegBytes)

    override suspend fun updateProfile(user: UserModel): Result<Unit> =
        authService.updateProfile(user)

    override suspend fun getCurrentUser(): UserModel? = authService.getCurrentUser()

    override suspend fun isLoggedIn(): Boolean = authService.isLoggedIn()

    override suspend fun logout() = authService.logout()

    override fun getLocalUser(): UserModel? = authService.getLocalUser()
}
