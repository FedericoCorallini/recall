package com.fcorallini.recall.core.data.common

import android.content.Context
import androidx.core.net.toUri
import java.io.IOException

fun Context.readBytesFromUri(uriString: String): ByteArray {
    val uri = uriString.toUri()
    return contentResolver.openInputStream(uri)?.use { inputStream ->
        inputStream.readBytes()
    } ?: throw IOException("Failed to open input stream for URI: $uriString")
}
