package com.example.hockey_app.features.news.di

import com.example.hockey_app.features.news.data.SupabaseNewsRepository
import com.example.hockey_app.features.news.domain.repositories.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NewsFeatureModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        impl: SupabaseNewsRepository
    ): NewsRepository
}
