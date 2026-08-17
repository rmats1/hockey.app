package com.example.hockey_app.data.repositories

import com.example.hockey_app.data.models.UserModel
import com.example.hockey_app.domain.auth.AuthRepository
import com.example.hockey_app.features.auth.domain.repositories.AuthRepository as FeatureAuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** 
 * Bridge between legacy boundary and new Feature implementation.
 */
class DefaultAuthRepository @Inject constructor(
    private val featureRepository: FeatureAuthRepository,
) : AuthRepository {
    override val sessionStatus: StateFlow<SessionStatus>
        get() = featureRepository.sessionStatus

    override val isUserLoggedIn: StateFlow<Boolean>
        get() = featureRepository.isUserLoggedIn

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        featureRepository.signInWithEmail(email, password)

    override suspend fun signInWithGoogle(): Result<Unit> = featureRepository.signInWithGoogle()

    override suspend fun signInWithGoogleNative(idToken: String): Result<Unit> =
        featureRepository.signInWithGoogleNative(idToken)

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        user: UserModel,
    ): Result<Unit> = featureRepository.signUpWithEmail(email, password, user)

    override suspend fun uploadProfilePhoto(jpegBytes: ByteArray): Result<String> =
        featureRepository.uploadProfilePhoto(jpegBytes)

    override suspend fun updateProfile(user: UserModel): Result<Unit> =
        featureRepository.updateProfile(user)

    override suspend fun getCurrentUser(): UserModel? = featureRepository.getCurrentUser()

    override suspend fun isLoggedIn(): Boolean = featureRepository.isUserLoggedIn.value

    override suspend fun logout() { featureRepository.logout() }

    override fun getLocalUser(): UserModel? = featureRepository.getLocalUser()
}
