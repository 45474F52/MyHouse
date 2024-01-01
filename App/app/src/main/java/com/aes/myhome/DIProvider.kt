package com.aes.myhome

import android.content.Context
import androidx.room.Room
import com.aes.myhome.storage.database.AppDatabase
import com.aes.myhome.storage.json.JsonDataSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DIProvider {

    @Singleton
    @Provides
    fun provideSerializer(@ApplicationContext context: Context) =
        JsonDataSerializer(context, JsonDataSerializer.StorageType.INTERNAL)

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) =
        Room
            .databaseBuilder(context, AppDatabase::class.java, "MyHomeDB")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideFoodDAO(db: AppDatabase) = db.foodDAO()

    @Singleton
    @Provides
    fun provideRecipeDAO(db: AppDatabase) = db.recipeDAO()

    @Singleton
    @Provides
    fun provideScheduleEventDAO(db: AppDatabase) = db.scheduleEventDAO()
}