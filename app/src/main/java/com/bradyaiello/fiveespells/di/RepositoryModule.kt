package com.bradyaiello.fiveespells.di

import android.content.Context
import com.bradyaiello.fiveespells.Database
import com.bradyaiello.fiveespells.repository.SpellRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideSpellRepository(
        @ApplicationContext context: Context,
        spellDatabase: Database,
    ): SpellRepository = SpellRepository(
        context, spellDatabase
    )
}