package com.fcorallini.recall.home.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class FileUploadResponse(
    @SerialName("id") val id: String,
    @SerialName("object") val objectType: String,
    @SerialName("bytes") val bytes: Int,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("filename") val filename: String,
    @SerialName("purpose") val purpose: String
)
@Serializable
data class ResponsesRequest(
    val model: String,
    val input: List<InputItem>,
    val text: TextConfig? = null
)

@Serializable
data class InputItem(
    val role: String,
    val content: List<ContentPart>
)

@Serializable
sealed class ContentPart {
    @Serializable
    @SerialName("input_text")
    data class InputText(val text: String) : ContentPart()

    @Serializable
    @SerialName("input_file")
    data class InputFile(@SerialName("file_id") val fileId: String) : ContentPart()
}

@Serializable
data class TextConfig(
    val format: TextFormat? = null
)

@Serializable
data class TextFormat(
    val type: String,              // "json_schema" o "text"
    val name: String? = null,      // requerido cuando type=json_schema
    val strict: Boolean? = null,   // recomendado cuando type=json_schema
    val schema: Schema? = null     // <-- requerido cuando type=json_schema
)

@Serializable
data class JsonSchema(
    val name: String,
    val strict: Boolean = true,
    val schema: Schema
)

@Serializable
data class Schema(
    val type: String = "object",
    val properties: Map<String, PropertyDefinition>,
    val required: List<String>,
    @SerialName("additionalProperties") val additionalProperties: Boolean = false
)

@Serializable
data class PropertyDefinition(
    val type: String? = null,
    val items: PropertyDefinition? = null,
    val properties: Map<String, PropertyDefinition>? = null,
    val required: List<String>? = null,
    val enum: List<String>? = null,
    @SerialName("additionalProperties") val additionalProperties: Boolean? = null,
    val minItems: Int? = null,
    val maxItems: Int? = null
)

@Serializable
data class ResponsesResponse(
    val id: String? = null,
    val output: List<OutputItem> = emptyList()
) {
    fun firstOutputTextOrNull(): String? =
        output.asSequence()
            .flatMap { it.content.asSequence() }
            .mapNotNull { it.text }
            .firstOrNull()
}

@Serializable
data class OutputItem(
    val type: String? = null,
    val role: String? = null,
    val content: List<OutputContent> = emptyList()
)

@Serializable
data class OutputContent(
    val type: String? = null,
    val text: String? = null
)

@Serializable
data class GeneratedQuestionsResponse(
    val questions: List<GeneratedQuestion>
)

@Serializable
data class GeneratedQuestion(
    val prompt: String,
    val options: List<String>,
    val answer: String
)
