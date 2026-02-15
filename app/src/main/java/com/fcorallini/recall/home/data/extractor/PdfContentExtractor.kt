package com.fcorallini.recall.home.data.extractor

import android.content.Context
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.fcorallini.recall.core.common.DispatchersProvider
import com.fcorallini.recall.core.common.readBytesFromUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PdfContentExtractor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider
) {
    suspend fun extractBytes(uriString: String): ByteArray = withContext(dispatchers.io) {
        context.readBytesFromUri(uriString)
    }

    suspend fun extractDisplayName(uriString: String): String = withContext(dispatchers.io) {
        return@withContext try {
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
