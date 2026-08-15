package com.example.hockey_app.features.auth.domain.repositories

import com.example.hockey_app.data.models.UserModel
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val isUserLoggedIn: StateFlow<Boolean>
    suspend fun signInWithEmail(email: String, pass: String): Result<Unit>
    suspend fun signUpWithEmail(email: String, pass: String, userMetadata: UserModel): Result<Unit>
    suspend fun signInWithGoogleNative(idToken: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): UserModel?
    suspend fun updateProfile(user: UserModel): Result<Unit>
}
