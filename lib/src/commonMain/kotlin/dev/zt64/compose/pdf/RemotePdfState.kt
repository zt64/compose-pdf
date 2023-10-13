package dev.zt64.compose.pdf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import dev.zt64.compose.pdf.icons.Error
import dev.zt64.compose.pdf.icons.Icons
import dev.zt64.compose.pdf.icons.Loading
import java.net.URL

@Stable
public expect class RemotePdfState public constructor(
    url: URL,
    errorPainter: Painter,
    loadingPainter: Painter
) : PdfState {
    override var pageCount: Int

    /**
     * Current loading state for this Pdf
     */
    public var loadState: LoadState

    override fun renderPage(index: Int): Painter

    /**
     * Loads the Pdf from the remote URL
     */
    public fun loadPdf()
}

/**
 * The current loading state for a [RemotePdfState]
 */
public enum class LoadState {
    Loading,
    Error,
    Success
}

/**
 * Returns a [RemotePdfState] for the given [url]
 *
 * @param url The http url to load from
 * @param errorPainter [Painter] to use when the Pdf fails to load
 * @param loadingPainter [Painter] to use while the Pdf is loading
 * @return [RemotePdfState]
 */
@Composable
public expect fun rememberRemotePdfState(
    url: URL,
    errorPainter: Painter = rememberVectorPainter(Icons.Error),
    loadingPainter: Painter = rememberVectorPainter(Icons.Loading)
): RemotePdfState

/**
 * Returns a [RemotePdfState] for the given [url]
 *
 * @param url The http url to load from
 * @param errorPainter [Painter] to use when the Pdf fails to load
 * @param loadingPainter [Painter] to use while the Pdf is loading
 * @return [RemotePdfState]
 */
@Composable
public expect fun rememberRemotePdfState(
    url: String,
    errorPainter: Painter = rememberVectorPainter(Icons.Error),
    loadingPainter: Painter = rememberVectorPainter(Icons.Loading)
): RemotePdfState