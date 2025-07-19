@file:Suppress("NOTHING_TO_INLINE")

package dev.zt64.compose.pdf

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.IntSize
import org.icepdf.core.pobjects.Document
import org.icepdf.core.pobjects.Page
import org.icepdf.core.util.GraphicsRenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.net.URI
import java.net.URL

internal actual typealias PdfRenderer = Document

// -- Constructors --
internal actual inline fun PdfRenderer(file: File): PdfRenderer {
    return PdfRenderer().apply {
        setFile(file.absolutePath)
    }
}

internal actual suspend inline fun PdfRenderer(url: URL): PdfRenderer {
    return PdfRenderer().apply {
        setUrl(url)
    }
}

internal inline fun PdfRenderer(uri: URI): PdfRenderer {
    return PdfRenderer().apply {
        if (uri.scheme == "file") {
            setFile(File(uri).absolutePath)
        } else {
            setUrl(uri.toURL())
        }
    }
}

// -- Properties --
internal actual inline val PdfRenderer.pageCount: Int
    get() = numberOfPages

// -- Functions --
internal actual suspend inline fun PdfRenderer.renderPage(
    pageIndex: Int,
    zoom: Float
): ImageBitmap {
    val image = getPageImage(
        // pageNumber =
        pageIndex,
        // renderHintType =
        GraphicsRenderingHints.SCREEN,
        // pageBoundary =
        Page.BOUNDARY_CROPBOX,
        // userRotation =
        0f,
        // userZoom =
        zoom
    ) as BufferedImage

    val bmp = image.toComposeImageBitmap()

    image.flush()

    return bmp
}

internal actual inline fun PdfRenderer.getPageSize(
    pageIndex: Int,
    zoom: Float
): IntSize {
    return getPageDimension(pageIndex, zoom).let { size ->
        IntSize(
            width = size.width.toInt(),
            height = size.height.toInt()
        )
    }
}

internal actual inline fun PdfRenderer.close() = dispose()