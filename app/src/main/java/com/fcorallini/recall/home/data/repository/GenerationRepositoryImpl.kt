package com.fcorallini.recall.home.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.fcorallini.recall.core.common.DispatchersProvider
import com.fcorallini.recall.core.common.Result
import com.fcorallini.recall.core.common.TimeProvider
import com.fcorallini.recall.core.common.readBytesFromUri
import com.fcorallini.recall.core.db.dao.PdfSourceDao
import com.fcorallini.recall.core.db.dao.QuestionDao
import com.fcorallini.recall.core.db.entity.toDomain
import com.fcorallini.recall.core.db.entity.toEntity
import com.fcorallini.recall.core.model.PdfSource
import com.fcorallini.recall.home.data.datasource.QuestionGenerationRemoteDataSource
import com.fcorallini.recall.home.domain.repository.GenerationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import androidx.core.net.toUri

class GenerationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfSourceDao: PdfSourceDao,
    private val questionDao: QuestionDao,
    private val questionGenerationRemoteDataSource: QuestionGenerationRemoteDataSource,
    private val timeProvider: TimeProvider,
    private val dispatchers: DispatchersProvider
) : GenerationRepository {

    companion object {
        private const val TAG = "GenerationRepository"
    }

    override suspend fun generateFromPdf(uriString: String): Result<String> = withContext(dispatchers.io) {
        try {
            Log.d(TAG, "Starting PDF generation for URI: $uriString")
            
            val sourceId = UUID.randomUUID().toString()
            val displayName = extractDisplayName(uriString)
            
            // Create PdfSource
            val pdfSource = PdfSource(
                id = sourceId,
                displayName = displayName,
                uriString = uriString,
                createdAtEpochMs = timeProvider.currentTimeMillis()
            )
            
            Log.d(TAG, "Reading PDF bytes from URI...")
            // Read PDF bytes from URI
            val pdfBytes = context.readBytesFromUri(uriString)
            Log.d(TAG, "Read ${pdfBytes.size} bytes from PDF")
            
            // Generate questions using OpenAI
            Log.d(TAG, "Calling OpenAI API to generate questions...")
            val questions = questionGenerationRemoteDataSource.generateQuestionsFromPdf(
                pdfBytes = pdfBytes,
                filename = displayName,
                sourceId = sourceId
            )
            Log.d(TAG, "Generated ${questions.size} questions")
            
            // Persist to database
            pdfSourceDao.insert(pdfSource.toEntity())
            questionDao.insertAll(questions.map { it.toEntity() })
            Log.d(TAG, "Successfully persisted PDF source and questions to database")
            
            Result.Success(sourceId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate questions from PDF", e)
            Result.Error(e)
        }
    }

    override fun observeAllPdfSources(): Flow<List<PdfSource>> {
        return pdfSourceDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun extractDisplayName(uriString: String): String {
        return try {
            val uri = uriString.toUri()
            var displayName: String? = null
            
            // Query the content resolver for the display name
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        displayName = cursor.getString(nameIndex)
                    }
                }
            }
            
            // Fallback to last path segment if query fails
            displayName ?: uri.lastPathSegment ?: "Unknown PDF"
        } catch (e: Exception) {
            "Unknown PDF"
        }
    }
}
