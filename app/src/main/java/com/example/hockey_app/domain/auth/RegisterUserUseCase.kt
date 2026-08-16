package com.example.hockey_app.domain.auth

import com.example.hockey_app.data.models.UserModel
import javax.inject.Inject

/** Registers a user through the authentication boundary. */
class RegisterUserUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        user: UserModel,
    ): Result<Unit> = repository.signUpWithEmail(email, password, user)
}
