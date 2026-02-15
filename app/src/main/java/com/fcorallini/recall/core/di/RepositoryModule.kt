package com.fcorallini.recall.core.di

import com.fcorallini.recall.home.data.extractor.PdfContentExtractorImpl
import com.fcorallini.recall.core.data.repository.PdfSourceRepositoryImpl
import com.fcorallini.recall.core.data.repository.QuestionRepositoryImpl
import com.fcorallini.recall.home.domain.extractor.PdfContentExtractor
import com.fcorallini.recall.core.domain.repository.PdfSourceRepository
import com.fcorallini.recall.core.domain.repository.QuestionRepository
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
    abstract fun bindPdfSourceRepository(
        impl: PdfSourceRepositoryImpl
    ): PdfSourceRepository

    @Binds
    @Singleton
    abstract fun bindQuestionRepository(
        impl: QuestionRepositoryImpl
    ): QuestionRepository

    @Binds
    @Singleton
    abstract fun bindExtractor(
        impl: PdfContentExtractorImpl
    ): PdfContentExtractor
}
