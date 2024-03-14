package dev.zt64.compose.pdf

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter

/** Represents a PDF file. */
@Stable
public interface PdfState : AutoCloseable {
    /** Total number of pages in the PDF file. */
    public val pageCount: Int

    /**
     * @param index the page number to render
     * @return [Painter]
     */
    public fun renderPage(index: Int): Painter
}