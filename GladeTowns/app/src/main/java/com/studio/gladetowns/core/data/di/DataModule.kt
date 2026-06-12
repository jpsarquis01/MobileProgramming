package com.studio.gladetowns.core.data.di

import android.content.Context
import androidx.room.Room
import com.studio.gladetowns.core.data.db.GladeDatabase
import com.studio.gladetowns.core.data.db.dao.DioramaDao
import com.studio.gladetowns.core.data.db.dao.SettingsDao
import com.studio.gladetowns.core.data.db.dao.TownBlobDao
import com.studio.gladetowns.core.data.repo.DioramaRepositoryImpl
import com.studio.gladetowns.core.data.repo.SettingsRepositoryImpl
import com.studio.gladetowns.core.data.repo.TownRepositoryImpl
import com.studio.gladetowns.core.domain.repository.DioramaRepository
import com.studio.gladetowns.core.domain.repository.SettingsRepository
import com.studio.gladetowns.core.domain.repository.TownRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GladeDatabase =
        Room.databaseBuilder(context, GladeDatabase::class.java, GladeDatabase.NAME)
            // WAL is Room's default journal on modern Android; stated for intent.
            .build()

    @Provides fun dioramaDao(db: GladeDatabase): DioramaDao = db.dioramaDao()
    @Provides fun townBlobDao(db: GladeDatabase): TownBlobDao = db.townBlobDao()
    @Provides fun settingsDao(db: GladeDatabase): SettingsDao = db.settingsDao()

    @Provides
    @Named("filesDir")
    fun filesDir(@ApplicationContext context: Context): File = context.filesDir

    @Provides
    @Named("io")
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun dioramaRepository(impl: DioramaRepositoryImpl): DioramaRepository
    @Binds abstract fun townRepository(impl: TownRepositoryImpl): TownRepository
    @Binds abstract fun settingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
