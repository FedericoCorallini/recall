package com.fcorallini.recall.core.model

data class PdfSource(
    val id: String,
    val displayName: String,
    val uriString: String,
    val createdAtEpochMs: Long
)
