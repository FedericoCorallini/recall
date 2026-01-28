package com.fcorallini.recall.home.data.datasource

import android.util.Log
import com.fcorallini.recall.core.model.Question
import com.fcorallini.recall.core.model.QuestionStats
import com.fcorallini.recall.core.model.QuestionType
import com.fcorallini.recall.home.data.network.*
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of QuestionGenerationRemoteDataSource using OpenAI API
 */
class OpenAiQuestionGenerationRemoteDataSource @Inject constructor(
    private val openAiService: OpenAiService,
    private val json: Json
) : QuestionGenerationRemoteDataSource {

    companion object {
        private const val TAG = "OpenAiQuestionGen"
        private const val MODEL = "gpt-4o-mini"
        private const val FILE_PURPOSE = "assistants"

        private const val SYSTEM_PROMPT = """
            You generate multiple-choice quiz questions grounded strictly in the provided PDF.
            
            Hard rules:
            - Use ONLY information explicitly present in the PDF. Do not use outside knowledge.
            - If the PDF does not contain enough information for a question, replace it with another question that IS supported.
            - No hallucinations. No assumptions. No invented details.
            - Output must follow the provided JSON schema exactly (no markdown, no extra fields).
            
            Requirements:
            - Generate exactly 6 MULTIPLE_CHOICE questions.
            - Each question must have exactly 4 options.
            - Exactly 1 option is correct.
            - The "answer" must match one of the options exactly.
            - Keep prompts and options short and unambiguous.
            """
    }

    override suspend fun generateQuestionsFromPdf(
        pdfBytes: ByteArray,
        filename: String,
        sourceId: String
    ): List<Question> {
        try {
            Log.d(TAG, "Starting PDF question generation for: $filename")
            
            // Step 1: Upload PDF to OpenAI Files API
            val fileId = uploadPdfFile(pdfBytes, filename)
            Log.d(TAG, "Uploaded PDF with fileId: $fileId")
            
            // Step 2: Generate questions using Chat Completions API with the file
            val generatedQuestions = generateQuestionsWithFile(fileId)
            Log.d(TAG, "Generated ${generatedQuestions.questions.size} questions")
            
            // Step 3: Convert to domain models and validate
            val domainQuestions = generatedQuestions.questions.mapIndexed { index, genQ ->
                validateAndConvertToDomain(genQ, sourceId, index)
            }
            
            Log.d(TAG, "Successfully converted all questions to domain models")
            return domainQuestions
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate questions from PDF", e)
            throw Exception("Failed to generate questions: ${e.message}", e)
        }
    }

    private suspend fun uploadPdfFile(pdfBytes: ByteArray, filename: String): String {
        // Create a temporary file for the upload
        val tempFile = File.createTempFile("upload_", ".pdf")
        try {
            tempFile.writeBytes(pdfBytes)
            
            val requestBody = tempFile.asRequestBody("application/pdf".toMediaType())
            val filePart = MultipartBody.Part.createFormData("file", filename, requestBody)
            val purposeBody = FILE_PURPOSE.toRequestBody("text/plain".toMediaType())
            
//            val response = openAiService.uploadFile(filePart, purposeBody)
//            return response.id
            return "file-13UX6qHJRSZqxPdsUy8x5p"
        } finally {
            tempFile.delete()
        }
    }

    private fun buildQuestionSchema(): Schema {
        val questionObject = PropertyDefinition(
            type = "object",
            properties = mapOf(
                "prompt" to PropertyDefinition(type = "string"),
                "options" to PropertyDefinition(
                    type = "array",
                    items = PropertyDefinition(type = "string"),
                    minItems = 4,
                    maxItems = 4
                ),
                "answer" to PropertyDefinition(type = "string")
            ),
            required = listOf("prompt", "options", "answer"),
            additionalProperties = false
        )

        return Schema(
            type = "object",
            properties = mapOf(
                "questions" to PropertyDefinition(
                    type = "array",
                    items = questionObject,
                    minItems = 6,
                    maxItems = 6
                )
            ),
            required = listOf("questions"),
            additionalProperties = false
        )
    }


    private fun validateAndConvertToDomain(
        generatedQuestion: GeneratedQuestion,
        sourceId: String,
        index: Int
    ): Question {
        if (generatedQuestion.options.size != 4) {
            throw IllegalArgumentException(
                "Question $index: must have exactly 4 options, got ${generatedQuestion.options.size}"
            )
        }
        if (!generatedQuestion.options.contains(generatedQuestion.answer)) {
            throw IllegalArgumentException(
                "Question $index: Answer '${generatedQuestion.answer}' is not in options"
            )
        }

        return Question(
            id = UUID.randomUUID().toString(),
            sourceId = sourceId,
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = generatedQuestion.prompt,
            options = generatedQuestion.options,
            answer = generatedQuestion.answer,
            stats = QuestionStats()
        )
    }


    private suspend fun generateQuestionsWithFile(fileId: String): GeneratedQuestionsResponse {
        val schema = buildQuestionSchema()

        val request = ResponsesRequest(
            model = MODEL,
            input = listOf(
                InputItem(
                    role = "system",
                    content = listOf(ContentPart.InputText(SYSTEM_PROMPT))
                ),
                InputItem(
                    role = "user",
                    content = listOf(
                        ContentPart.InputFile(fileId),
                        ContentPart.InputText("Generate the questions now in Spanish.")
                    )
                )
            ),
            text = TextConfig(
                format = TextFormat(
                    type = "json_schema",
                    name = "quiz_questions",
                    strict = true,
                    schema = schema
                )
            )
        )

        val response = openAiService.createResponse(request)

        val jsonText = requireNotNull(response.firstOutputTextOrNull()) {
            "No output_text found in response.output[]. Log the response to inspect structure."
        }

        return json.decodeFromString(GeneratedQuestionsResponse.serializer(), jsonText)
    }



}

