package dev.zt64.compose.pdf

import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import dev.zt64.compose.pdf.util.renderPage
import java.io.File
import java.net.URL

@Stable
public actual class LocalPdfState(private val pfd: ParcelFileDescriptor) : PdfState {
    private val renderer = PdfRenderer(pfd)

    public actual override val pageCount: Int = renderer.pageCount

    public actual constructor(file: File) : this(
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    )

    public actual override fun renderPage(index: Int): Painter = renderer.renderPage(index)

    override fun close() {
        renderer.close()
    }

    public companion object {
        /**
         * [Saver] implementation for [PdfState].
         */
        public val Saver: Saver<LocalPdfState, *> = listSaver(
            save = {
                listOf(it.pfd)
            },
            restore = {
                LocalPdfState(it[0])
            }
        )
    }
}

/**
 * Remembers a [LocalPdfState] for the given [pfd].
 *
 * @param pfd
 * @return [LocalPdfState]
 */
@Composable
public fun rememberLocalPdfState(pfd: ParcelFileDescriptor): LocalPdfState {
    val state = rememberSaveable(pfd, saver = LocalPdfState.Saver) {
        LocalPdfState(pfd)
    }

    DisposableEffect(state) {
        onDispose {
            state.close()
        }
    }

    return state
}

/**
 * Remembers a [LocalPdfState] for the given [uri].
 *
 * @param uri
 * @return [LocalPdfState]
 */
@Composable
public fun rememberLocalPdfState(uri: Uri): LocalPdfState {
    require(uri.scheme == "content") { "URI is not a content URI" }

    val context = LocalContext.current

    return rememberLocalPdfState(context.contentResolver.openFileDescriptor(uri, "r")!!)
}

/**
 * Remembers a [LocalPdfState] for the given [url].
 *
 * @param url
 * @return [LocalPdfState]
 */
@Composable
public actual fun rememberLocalPdfState(url: URL): LocalPdfState {
    require(url.file.isNotEmpty()) { "URL does not have a file" }
    require(url.file.endsWith(".pdf")) { "URL does not point to a PDF" }

    return rememberLocalPdfState(Uri.parse(url.toString()))
}

/**
 * Remembers a [LocalPdfState] for the given [file].
 *
 * @param file
 * @return [LocalPdfState]
 */
@Composable
public actual fun rememberLocalPdfState(file: File): LocalPdfState {
    require(file.exists()) { "File does not exist" }
    require(file.isFile) { "File is not a file" }

    return rememberLocalPdfState(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
}