package com.fcorallini.recall.di

import com.fcorallini.recall.home.data.repository.GenerationRepositoryImpl
import com.fcorallini.recall.home.domain.repository.GenerationRepository
import com.fcorallini.recall.quiz.data.repository.QuizRepositoryImpl
import com.fcorallini.recall.quiz.domain.repository.QuizRepository
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
    abstract fun bindGenerationRepository(
        impl: GenerationRepositoryImpl
    ): GenerationRepository

    @Binds
    @Singleton
    abstract fun bindQuizRepository(
        impl: QuizRepositoryImpl
    ): QuizRepository
}
