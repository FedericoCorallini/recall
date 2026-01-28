package com.fcorallini.recall.core.common

import com.fcorallini.recall.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyProvider @Inject constructor() {
    fun getOpenAiApiKey(): String {
        val key = BuildConfig.OPENAI_API_KEY
        if (key.isBlank()) {
            throw IllegalStateException(
                "OPENAI_API_KEY is not set. Please add it to local.properties"
            )
        }
        return key
    }
}
