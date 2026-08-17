package com.example.hockey_app.features.community.di

import com.example.hockey_app.features.community.data.SupabaseCommunityRepository
import com.example.hockey_app.features.community.domain.repositories.CommunityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommunityFeatureModule {

    @Binds
    @Singleton
    abstract fun bindCommunityRepository(
        impl: SupabaseCommunityRepository
    ): CommunityRepository
}
