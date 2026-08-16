package com.example.hockey_app.di

import com.example.hockey_app.data.repositories.DefaultAuthRepository
import com.example.hockey_app.domain.auth.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {
    @Binds
    abstract fun bindAuthRepository(
        implementation: DefaultAuthRepository,
    ): AuthRepository
}
