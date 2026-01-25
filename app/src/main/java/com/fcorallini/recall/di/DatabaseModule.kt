package com.fcorallini.recall.di

import android.content.Context
import androidx.room.Room
import com.fcorallini.recall.core.db.RecallDatabase
import com.fcorallini.recall.core.db.dao.PdfSourceDao
import com.fcorallini.recall.core.db.dao.QuestionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRecallDatabase(
        @ApplicationContext context: Context
    ): RecallDatabase {
        return Room.databaseBuilder(
            context,
            RecallDatabase::class.java,
            "recall_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePdfSourceDao(database: RecallDatabase): PdfSourceDao {
        return database.pdfSourceDao()
    }

    @Provides
    @Singleton
    fun provideQuestionDao(database: RecallDatabase): QuestionDao {
        return database.questionDao()
    }
}
