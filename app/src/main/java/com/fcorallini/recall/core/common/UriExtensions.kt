package com.fcorallini.recall.core.common

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import java.io.IOException

/**
 * Reads bytes from a content:// URI using ContentResolver
 * @param uriString The URI string to read from
 * @return ByteArray of the file contents
 * @throws IOException if reading fails
 */
fun Context.readBytesFromUri(uriString: String): ByteArray {
    val uri = uriString.toUri()
    return contentResolver.openInputStream(uri)?.use { inputStream ->
        inputStream.readBytes()
    } ?: throw IOException("Failed to open input stream for URI: $uriString")
}
