package com.fcorallini.recall.home.domain.extractor

interface PdfContentExtractor {
    suspend fun extractBytes(uriString: String): ByteArray
    suspend fun extractDisplayName(uriString: String): String
    suspend fun getPageCount(uriString: String): Int
    suspend fun contWords(uriString: String): Int
}
