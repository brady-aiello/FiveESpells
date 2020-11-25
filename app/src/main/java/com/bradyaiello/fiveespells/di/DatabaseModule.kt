package com.bradyaiello.fiveespells.di

import android.content.Context
import com.bradyaiello.fiveespells.Database
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAndroidDriver(@ApplicationContext applicationContext: Context): AndroidSqliteDriver {
        return AndroidSqliteDriver(
            schema = Database.Schema,
            context = applicationContext,
            name = "Spells.db"
        )
    }

    @Singleton
    @Provides
    fun provideSQLDelightDatabase(driver: AndroidSqliteDriver): Database {
        return Database(driver)
    }
}