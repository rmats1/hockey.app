package com.example.hockey_app.features.auth.di

import com.example.hockey_app.features.auth.data.SupabaseAuthRepository
import com.example.hockey_app.features.auth.domain.repositories.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthFeatureModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: SupabaseAuthRepository
    ): AuthRepository
}
