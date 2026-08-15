package com.example.hockey_app.features.auth.domain.usecases

import com.example.hockey_app.data.models.UserModel
import com.example.hockey_app.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): UserModel? {
        return repository.getCurrentUser()
    }
}
