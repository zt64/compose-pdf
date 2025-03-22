package dev.zt64.compose.pdf.util

import dev.zt64.compose.pdf.remote.Downloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL

internal inline fun CoroutineScope.downloadPdf(
    url: URL,
    crossinline onError: () -> Unit,
    crossinline onComplete: (File) -> Unit
) {
    launch {
        try {
            val name = url.path.split("/").lastOrNull()?.removeSuffix(".pdf") ?: "PdfFile"
            val pdfFile = File.createTempFile(name, ".pdf")

            if (!pdfFile.exists()) Downloader.download(url, pdfFile)

            onComplete(pdfFile)
        } catch (e: Throwable) {
            e.printStackTrace()
            onError()
        }
    }
}