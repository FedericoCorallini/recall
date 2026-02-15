package com.fcorallini.recall.di

import com.fcorallini.recall.core.data.common.DefaultDispatchersProvider
import com.fcorallini.recall.core.data.common.DefaultTimeProvider
import com.fcorallini.recall.core.data.common.DispatchersProvider
import com.fcorallini.recall.core.data.common.TimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDispatchersProvider(): DispatchersProvider {
        return DefaultDispatchersProvider()
    }

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider {
        return DefaultTimeProvider()
    }
}
