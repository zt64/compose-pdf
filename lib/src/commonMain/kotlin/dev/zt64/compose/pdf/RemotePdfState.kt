package dev.zt64.compose.pdf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import java.net.URL

@Stable
public expect class RemotePdfState public constructor(
    url: URL,
    errorPainter: Painter,
    loadingPainter: Painter
) : PdfState {
    override var pageCount: Int
        private set

    /**
     * Current loading state for this Pdf
     */
    public var loadState: LoadState
        private set

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

public object RemotePdfDefaults {
    public val BlankIcon: ImageVector = ImageVector
        .Builder(
            name = "BLANK_ICON",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24F,
            viewportHeight = 24F
        ).build()
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
    errorPainter: Painter = rememberVectorPainter(RemotePdfDefaults.BlankIcon),
    loadingPainter: Painter = rememberVectorPainter(RemotePdfDefaults.BlankIcon)
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
    errorPainter: Painter = rememberVectorPainter(RemotePdfDefaults.BlankIcon),
    loadingPainter: Painter = rememberVectorPainter(RemotePdfDefaults.BlankIcon)
): RemotePdfState