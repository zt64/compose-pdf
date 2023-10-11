package dev.zt64.compose.pdf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.icepdf.core.pobjects.Document
import org.icepdf.core.pobjects.Page
import org.icepdf.core.util.GraphicsRenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import java.net.URL

private const val SCALE = 1.0f
private const val ROTATION = 0f

@Stable
public actual class PdfState(private val document: Document) : AutoCloseable {
    public actual val pageCount: Int = document.numberOfPages

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

    public actual fun renderPage(index: Int): BitmapPainter {
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
 * Remembers a [PdfState] for the given [inputStream].
 *
 * @param inputStream
 * @return [PdfState]
 */
@Composable
public fun rememberPdfState(inputStream: InputStream): PdfState {
    return remember { PdfState(inputStream) }
}

/**
 * Remembers a [PdfState] for the given [url].
 *
 * @param url
 * @return [PdfState]
 */
@Composable
public actual fun rememberPdfState(url: URL): PdfState {
    return remember { PdfState(url) }
}

/**
 * Remembers a [PdfState] for the given [file].
 *
 * @param file
 * @return [PdfState]
 */
@Composable
public actual fun rememberPdfState(file: File): PdfState {
    return remember { PdfState(file) }
}