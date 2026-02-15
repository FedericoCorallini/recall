package com.fcorallini.recall.core.data.db.converter

import androidx.room.TypeConverter
import com.fcorallini.recall.core.domain.model.QuestionType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun fromQuestionType(value: QuestionType): String {
        return value.name
    }

    @TypeConverter
    fun toQuestionType(value: String): QuestionType {
        return QuestionType.valueOf(value)
    }
}
