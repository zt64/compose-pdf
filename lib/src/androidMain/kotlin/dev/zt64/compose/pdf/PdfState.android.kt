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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.net.URL

@Stable
public actual class PdfState(private val pfd: ParcelFileDescriptor) : AutoCloseable {
    private val renderer = PdfRenderer(pfd)

    public actual val pageCount: Int = renderer.pageCount

    public actual fun renderPage(index: Int): BitmapPainter {
        val bmp: Bitmap

        renderer.use {
            val page = renderer.openPage(index)

            bmp = ImageBitmap(page.width, page.height).asAndroidBitmap()

            page.use {
                page.render(bmp, null, null, Page.RENDER_MODE_FOR_DISPLAY)
            }
        }

        return BitmapPainter(bmp.asImageBitmap())
    }

    override fun close() {
        renderer.close()
    }

    public companion object {
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
    val state = rememberSaveable(
        pfd,
        saver = PdfState.Saver
    ) { PdfState(pfd) }

    DisposableEffect(pfd) {
        onDispose {
            pfd.close()
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
    val context = LocalContext.current

    return rememberPdfState(context.contentResolver.openFileDescriptor(Uri.parse(url.toString()), "r")!!)
}

/**
 * Remembers a [PdfState] for the given [file].
 *
 * @param file
 * @return [PdfState]
 */
@Composable
public actual fun rememberPdfState(file: File): PdfState {
    return rememberPdfState(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
}