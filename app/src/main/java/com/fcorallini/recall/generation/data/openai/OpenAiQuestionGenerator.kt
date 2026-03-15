package com.fcorallini.recall.generation.data.openai

import android.util.Log
import com.fcorallini.recall.core.domain.model.Question
import com.fcorallini.recall.core.domain.model.QuestionStats
import com.fcorallini.recall.core.domain.model.QuestionType
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.UUID
import javax.inject.Inject

class OpenAiQuestionGenerator @Inject constructor(
    private val openAiService: OpenAiService,
    private val json: Json
) {
    companion object {
        private const val TAG = "OpenAiQuestionGen"
        private const val MODEL = "gpt-4o-mini"
        private const val FILE_PURPOSE = "assistants"

        /**
         * System prompt template with {questionCount} placeholder.
         */
        private const val SYSTEM_PROMPT_TEMPLATE = """
            You generate high-quality multiple-choice quiz questions grounded strictly in the provided PDF.
        
            Hard rules:
            - Use ONLY information explicitly present in the PDF.
            - Do NOT use outside knowledge.
            - Do NOT invent facts, explanations, examples, or details that are not present in the PDF.
            - If a possible question is not clearly supported by the PDF, discard it and create another one.
            - Output must follow the provided JSON schema exactly (no markdown, no extra fields).
        
            Question requirements:
            - Generate exactly {questionCount} MULTIPLE_CHOICE questions.
            - Generate the questions in the same language as the PDF content.
            - Each question must have exactly 4 options.
            - Exactly 1 option must be correct.
            - The "answer" must match one of the options exactly.
            - Prioritize important concepts, definitions, relationships, steps, and examples from the document.
            - Avoid questions that are too trivial, overly obvious, or based only on headings/titles unless necessary.
            - Keep options consistent in style and type, and avoid making the correct option obviously different in length or wording.
            - Avoid creating two questions that test the same fact.
            - If the document is repetitive, prioritize the most central and useful ideas.
            - Prefer questions that require understanding of the content, not only shallow word matching.
            - Incorrect options should be plausible, but clearly wrong according to the PDF.
            - Avoid ambiguity whenever possible.
            - Prefer medium-difficulty questions that require understanding of the content rather than simple word matching.
        
            Style requirements:
            - Keep prompts concise and clear.
            - Keep options concise.
            - Make the 4 options belong to the same category/type when possible.
        """
    }

    suspend fun generateQuestionsFromPdf(
        pdfBytes: ByteArray,
        filename: String,
        sourceId: String,
        questionCount: Int
    ): List<Question> {
        try {
            Log.d(TAG, "Starting PDF question generation for: $filename ($questionCount questions)")

            // Step 1: Upload PDF to OpenAI Files API
            val fileId = uploadPdfFile(pdfBytes, filename)
            Log.d(TAG, "Uploaded PDF with fileId: $fileId")

            // Step 2: Generate questions using Chat Completions API with the file
            val generatedQuestions = generateQuestionsWithFile(fileId, questionCount)
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

            val response = openAiService.uploadFile(filePart, purposeBody)
            return response.id
        } finally {
            tempFile.delete()
        }
    }

    private suspend fun generateQuestionsWithFile(fileId: String, questionCount: Int): GeneratedQuestionsResponse {
        val schema = buildQuestionSchema(questionCount)
        val systemPrompt = SYSTEM_PROMPT_TEMPLATE.replace("{questionCount}", questionCount.toString())

        val request = ResponsesRequest(
            model = MODEL,
            input = listOf(
                InputItem(
                    role = "system",
                    content = listOf(ContentPart.InputText(systemPrompt))
                ),
                InputItem(
                    role = "user",
                    content = listOf(
                        ContentPart.InputFile(fileId),
                        ContentPart.InputText("Generate the questions in the same language as the PDF content.")
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

    private fun buildQuestionSchema(questionCount: Int): Schema {
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
                    minItems = questionCount,
                    maxItems = questionCount
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
        val normalizedOptions = generatedQuestion.options.map { it.trim() }
        val normalizedAnswer = generatedQuestion.answer.trim()
        if (normalizedOptions.size != 4) {
            throw IllegalArgumentException(
                "Question $index: must have exactly 4 options, got ${normalizedOptions.size}"
            )
        }
        if (!normalizedOptions.contains(normalizedAnswer)) {
            throw IllegalArgumentException(
                "Question $index: Answer '$normalizedAnswer' is not in options"
            )
        }

        return Question(
            id = UUID.randomUUID().toString(),
            sourceId = sourceId,
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = generatedQuestion.prompt,
            options = normalizedOptions,
            answer = normalizedAnswer,
            stats = QuestionStats()
        )
    }

}