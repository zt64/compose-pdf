@file:Suppress("NOTHING_TO_INLINE")

package dev.zt64.compose.pdf

import android.graphics.Color
import android.graphics.pdf.PdfRenderer.Page
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import dev.zt64.compose.pdf.remote.Downloader
import java.io.File
import java.net.URL

internal actual typealias PdfRenderer = android.graphics.pdf.PdfRenderer

@PublishedApi
internal actual inline fun PdfRenderer(file: File): PdfRenderer {
    return PdfRenderer(
        ParcelFileDescriptor.open(
            file,
            ParcelFileDescriptor.MODE_READ_ONLY
        )
    )
}

@PublishedApi
internal actual suspend inline fun PdfRenderer(url: URL): PdfRenderer {
    val name = url.path.split("/").lastOrNull()?.removeSuffix(".pdf") ?: "PdfFile"
    val pdfFile = File.createTempFile(name, ".pdf")
    if (!pdfFile.exists()) Downloader.download(url, pdfFile)
    return PdfRenderer(
        ParcelFileDescriptor.open(
            pdfFile,
            ParcelFileDescriptor.MODE_READ_ONLY
        )
    )
}

internal suspend fun PdfRenderer(uri: Uri): PdfRenderer {
    val pdfFile = when (uri.scheme) {
        "file" -> {
            File(uri.path!!)
        }
        "http", "https" -> {
            val name = uri.lastPathSegment?.removeSuffix(".pdf") ?: "PdfFile"
            val tempFile = File.createTempFile(name, ".pdf")
            Downloader.download(URL(uri.toString()), tempFile)
            tempFile
        }
        else -> {
            throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}")
        }
    }

    val pfd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
    return PdfRenderer(pfd)
}

internal actual inline val PdfRenderer.pageCount: Int
    get() = this.pageCount

internal actual suspend inline fun PdfRenderer.renderPage(pageIndex: Int, zoom: Float): ImageBitmap {
    val page = openPage(pageIndex)
    val width = (page.width * zoom).toInt()
    val height = (page.height * zoom).toInt()
    val bitmap = createBitmap(width, height).applyCanvas {
        drawColor(Color.WHITE)
    }
    page.render(bitmap, null, null, Page.RENDER_MODE_FOR_DISPLAY)
    page.close()
    return bitmap.asImageBitmap()
}

internal actual inline fun PdfRenderer.getPageSize(pageIndex: Int, zoom: Float): IntSize {
    val page = openPage(pageIndex)
    val width = (page.width * zoom).toInt()
    val height = (page.height * zoom).toInt()
    page.close()
    return IntSize(width, height)
}

internal actual inline fun PdfRenderer.close() = this.close()