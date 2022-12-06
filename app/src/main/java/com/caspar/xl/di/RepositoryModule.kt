package com.caspar.xl.di

import com.caspar.xl.di.domain.TranslateRepository
import com.caspar.xl.di.domain.impl.TranslateRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: TranslateRepositoryImpl
    ): TranslateRepository
}