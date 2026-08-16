package com.example.hockey_app.di

import com.example.hockey_app.data.repositories.DefaultCatalogRepository
import com.example.hockey_app.domain.catalog.CatalogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogRepositoryModule {
    @Binds
    abstract fun bindCatalogRepository(
        implementation: DefaultCatalogRepository,
    ): CatalogRepository
}
