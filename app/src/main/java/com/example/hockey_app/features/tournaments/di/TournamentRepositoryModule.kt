package com.example.hockey_app.features.tournaments.di

import com.example.hockey_app.features.tournaments.data.SupabaseTournamentRepository
import com.example.hockey_app.features.tournaments.domain.repositories.TournamentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TournamentRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTournamentRepository(
        impl: SupabaseTournamentRepository
    ): TournamentRepository
}
