package dev.zt64.compose.pdf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.icepdf.core.pobjects.Document
import org.icepdf.core.pobjects.Page
import org.icepdf.core.util.GraphicsRenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import java.net.URL

internal const val SCALE = 1.0f
internal const val ROTATION = 0f

@Stable
public actual class LocalPdfState(private val document: Document) : PdfState {
    public actual override var pageCount: Int = document.numberOfPages

    public constructor(inputStream: InputStream) : this(
        document = Document().apply {
            setInputStream(inputStream, null)
        }
    )

    public actual constructor(file: File) : this(
        document = Document().apply {
            setFile(file.absolutePath)
        }
    )

    public constructor(url: URL) : this(
        document = Document().apply {
            setUrl(url)
        }
    )

    public actual override fun renderPage(index: Int): Painter {
        val image = document.getPageImage(
            /* pageNumber = */ index,
            /* renderHintType = */ GraphicsRenderingHints.SCREEN,
            /* pageBoundary = */ Page.BOUNDARY_CROPBOX,
            /* userRotation = */ ROTATION,
            /* userZoom = */ SCALE
        ) as BufferedImage

        val bmp = image.toComposeImageBitmap()

        image.flush()

        return BitmapPainter(bmp)
    }

    override fun close() {
        document.dispose()
    }
}

/**
 * Remembers a [LocalPdfState] for the given [inputStream].
 *
 * @param inputStream
 * @return [LocalPdfState]
 */
@Composable
public fun rememberLocalPdfState(inputStream: InputStream): LocalPdfState {
    return remember { LocalPdfState(inputStream) }
}

/**
 * Remembers a [LocalPdfState] for the given [url].
 *
 * @param url
 * @return [LocalPdfState]
 */
@Composable
public actual fun rememberLocalPdfState(url: URL): LocalPdfState {
    return remember { LocalPdfState(url) }
}

/**
 * Remembers a [LocalPdfState] for the given [file].
 *
 * @param file
 * @return [LocalPdfState]
 */
@Composable
public actual fun rememberLocalPdfState(file: File): LocalPdfState {
    return remember { LocalPdfState(file) }
}