package com.bradyaiello.fiveespells.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SerializationModule {

    @Singleton
    @Provides
    fun provideSerializer(): Moshi {
        return Moshi.Builder()
            .build();
    }
}