package dev.zt64.compose.pdf.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

@PublishedApi
internal object Downloader {
    /**
     * @param url Url of the file you wish to download
     * @param dest Desired location of the downloaded file
     */
    suspend fun download(url: URL, dest: File) = withContext(Dispatchers.IO) {
        val conn = (url.openConnection() as HttpURLConnection).apply {
            addRequestProperty("User-Agent", "compose-pdf (https://github.com/zt64/compose-pdf)")
        }

        val ok = conn.responseCode in 200..<300
        if (!ok) throw HttpException(conn.responseCode, conn.responseMessage)

        saveToFile(conn, dest)
    }

    /**
     * Save the body of a request to a file
     * @param conn The request
     * @param dest The file to save to
     */
    private suspend fun saveToFile(conn: HttpURLConnection, dest: File) = withContext(Dispatchers.IO) {
        if (dest.exists()) {
            when {
                !dest.canWrite() -> throw IOException("Cannot write to file: ${dest.absolutePath}")
                dest.isDirectory -> throw IOException(
                    "Path already exists and is directory: ${dest.absolutePath}"
                )
            }
        }

        if (dest.parentFile == null) {
            throw IOException("Only absolute paths are supported")
        }

        val iStream = conn.inputStream
        dest.outputStream().use { os ->
            iStream.use { it.copyTo(os, 16384) }
        }
    }
}