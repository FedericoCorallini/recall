package com.fcorallini.recall.core.db.converter

import androidx.room.TypeConverter
import com.fcorallini.recall.core.model.QuestionType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
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
