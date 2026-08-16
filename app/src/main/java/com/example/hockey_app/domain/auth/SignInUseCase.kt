package com.example.hockey_app.domain.auth

import javax.inject.Inject

/** Authenticates a user with email and password. */
class SignInUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        repository.signInWithEmail(email, password)
}
