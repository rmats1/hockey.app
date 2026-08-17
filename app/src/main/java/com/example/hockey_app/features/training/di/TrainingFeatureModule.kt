package com.example.hockey_app.features.training.di

import com.example.hockey_app.features.training.data.SupabaseTrainingRepository
import com.example.hockey_app.features.training.domain.repositories.TrainingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrainingFeatureModule {

    @Binds
    @Singleton
    abstract fun bindTrainingRepository(
        impl: SupabaseTrainingRepository
    ): TrainingRepository
}
