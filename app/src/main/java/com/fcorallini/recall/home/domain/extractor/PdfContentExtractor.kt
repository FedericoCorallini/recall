package com.fcorallini.recall.home.domain.extractor

interface PdfContentExtractor {
    suspend fun extractBytes(uriString: String): ByteArray
    suspend fun extractDisplayName(uriString: String): String
}
