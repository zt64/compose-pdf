package dev.zt64.compose.pdf

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.net.URL

/**
 * Pdf state
 *
 * @constructor
 *
 * @param url
 */
@Stable
public expect class PdfState(
    url: URL
) : AutoCloseable {
    /** Total number of pages in the PDF file. */
    public val pageCount: Int

    public val loadState: StateFlow<LoadState>

    /**
     * @param index the page number to render
     */
    public fun loadPage(
        index: Int,
        zoom: Float = 1f
    ): ImageBitmap

    public val pages: Flow<PdfPage>
}

/**
 * The state of loading a PDF file
 */
public sealed interface LoadState {
    public data object Loading : LoadState

    public data object Loaded : LoadState

    public data class Error(
        public val exception: Exception
    ) : LoadState
}