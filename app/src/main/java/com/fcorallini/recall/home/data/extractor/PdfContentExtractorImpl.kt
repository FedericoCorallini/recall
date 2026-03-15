package com.fcorallini.recall.home.data.extractor

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.fcorallini.recall.core.data.common.DispatchersProvider
import com.fcorallini.recall.core.data.common.readBytesFromUri
import com.fcorallini.recall.home.domain.extractor.PdfContentExtractor
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min

class PdfContentExtractorImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider
) : PdfContentExtractor {

    private val WHITESPACE_REGEX = Regex("\\s+")

    init {
        // Initialize PDFBox resource loader
        PDFBoxResourceLoader.init(context)
    }

    override suspend fun extractBytes(uriString: String): ByteArray = withContext(dispatchers.io) {
        context.readBytesFromUri(uriString)
    }

    override suspend fun extractDisplayName(uriString: String): String = withContext(dispatchers.io) {
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

    override suspend fun getPageCount(uriString: String): Int = withContext(dispatchers.io) {
        val uri = uriString.toUri()
        var renderer: PdfRenderer? = null
        var pfd: ParcelFileDescriptor? = null

        try {
            pfd = context.contentResolver.openFileDescriptor(uri, "r")
                ?: throw IOException("Could not open PDF: $uriString")

            renderer = PdfRenderer(pfd)
            renderer.pageCount
        } catch (e: Exception) {
            throw IOException("Failed to get page count: ${e.message}", e)
        } finally {
            renderer?.close()
            pfd?.close()
        }
    }

    override suspend fun contWords(uriString: String): Int = withContext(dispatchers.io) {
        val uri = uriString.toUri()

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                PDDocument.load(inputStream).use { document ->

                    val stripper = PDFTextStripper().apply {
                        sortByPosition = true
                        addMoreFormatting = false
                        startPage = 1
                        endPage = min(5, document.numberOfPages)
                    }

                    val extractedText = stripper.getText(document)

                    extractedText
                        .replace(WHITESPACE_REGEX, " ")
                        .trim()
                        .take(20000)

                    val cleaned = extractedText.replace(WHITESPACE_REGEX, " ").trim()
                    Regex("\\S+").findAll(cleaned).count()
                }
            } ?: throw IOException("Could not open PDF input stream: $uriString")

        } catch (e: Exception) {
            throw IOException("Failed to extract text from PDF: ${e.message}", e)
        }
    }
}
