package com.example.hockey_app.di

import com.example.hockey_app.data.repositories.DefaultCompetitionRepository
import com.example.hockey_app.domain.competition.CompetitionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CompetitionRepositoryModule {
    @Binds
    abstract fun bindCompetitionRepository(
        implementation: DefaultCompetitionRepository,
    ): CompetitionRepository
}
