package dev.zt64.compose.pdf

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.graphics.pdf.PdfRenderer.Page
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.net.URL

@Stable
public actual class PdfState(private val pfd: ParcelFileDescriptor) : AutoCloseable {
    private val renderer = PdfRenderer(pfd)

    public actual val pageCount: Int = renderer.pageCount

    public actual constructor(file: File) : this(
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    )

    public actual fun renderPage(index: Int): BitmapPainter {
        require(index in 0 until pageCount) { "Page index out of bounds" }

        val bmp: Bitmap

        renderer.openPage(index).use { page ->
            bmp = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bmp, null, null, Page.RENDER_MODE_FOR_DISPLAY)
        }

        return BitmapPainter(bmp.asImageBitmap())
    }

    override fun close() {
        renderer.close()
    }

    public companion object {
        /**
         * [Saver] implementation for [PdfState].
         */
        public val Saver: Saver<PdfState, *> = listSaver(
            save = {
                listOf(it.pfd)
            },
            restore = {
                PdfState(it[0])
            }
        )
    }
}

/**
 * Remembers a [PdfState] for the given [pfd].
 *
 * @param pfd
 * @return [PdfState]
 */
@Composable
public fun rememberPdfState(pfd: ParcelFileDescriptor): PdfState {
    val state = rememberSaveable(pfd, saver = PdfState.Saver) {
        PdfState(pfd)
    }

    DisposableEffect(state) {
        onDispose {
            state.close()
        }
    }

    return state
}

/**
 * Remembers a [PdfState] for the given [uri].
 *
 * @param uri
 * @return [PdfState]
 */
@Composable
public fun rememberPdfState(uri: Uri): PdfState {
    require(uri.scheme == "content") { "URI is not a content URI" }

    val context = LocalContext.current

    return rememberPdfState(context.contentResolver.openFileDescriptor(uri, "r")!!)
}

/**
 * Remembers a [PdfState] for the given [url].
 *
 * @param url
 * @return [PdfState]
 */
@Composable
public actual fun rememberPdfState(url: URL): PdfState {
    require(url.file.isNotEmpty()) { "URL does not have a file" }
    require(url.file.endsWith(".pdf")) { "URL does not point to a PDF" }

    TODO()
}

/**
 * Remembers a [PdfState] for the given [file].
 *
 * @param file
 * @return [PdfState]
 */
@Composable
public actual fun rememberPdfState(file: File): PdfState {
    require(file.exists()) { "File does not exist" }
    require(file.isFile) { "File is not a file" }

    return rememberPdfState(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
}