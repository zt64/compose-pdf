package dev.zt64.compose.pdf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.BitmapPainter
import java.io.File
import java.net.URL

/** Represents a PDF file. */
@Stable
public expect class PdfState(file: File) : AutoCloseable {
    /** Total number of pages in the PDF file. */
    public val pageCount: Int

    /**
     * @param index the page number to render
     * @return [BitmapPainter]
     */
    public fun renderPage(index: Int): BitmapPainter
}

/**
 * Remembers a [PdfState] for the given [url].
 *
 * @param url
 * @return [PdfState]
 */
@Composable
public expect fun rememberPdfState(url: URL): PdfState

/**
 * Remembers a [PdfState] for the given [file].
 *
 * @param file
 * @return [PdfState]
 */
@Composable
public expect fun rememberPdfState(file: File): PdfState