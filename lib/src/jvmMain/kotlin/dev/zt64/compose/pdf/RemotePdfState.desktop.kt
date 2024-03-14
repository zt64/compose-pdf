package dev.zt64.compose.pdf

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import dev.zt64.compose.pdf.util.downloadPdf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.icepdf.core.pobjects.Document
import org.icepdf.core.pobjects.Page
import org.icepdf.core.util.GraphicsRenderingHints
import java.awt.image.BufferedImage
import java.net.URL

@Stable
public actual class RemotePdfState actual constructor(
    private val url: URL,
    public var errorPainter: Painter,
    public var loadingPainter: Painter
) : PdfState {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var document: Document? = null

    public actual override var pageCount: Int by mutableIntStateOf(1)
        private set
    public actual var loadState: LoadState by mutableStateOf(LoadState.Loading)
        private set

    init {
        loadPdf()
    }

    actual override fun renderPage(index: Int): Painter {
        return when (loadState) {
            LoadState.Loading -> loadingPainter

            LoadState.Error -> errorPainter

            LoadState.Success -> {
                val image = document?.getPageImage(
                    // pageNumber =
                    index,
                    // renderHintType =
                    GraphicsRenderingHints.SCREEN,
                    // pageBoundary =
                    Page.BOUNDARY_CROPBOX,
                    // userRotation =
                    ROTATION,
                    // userZoom =
                    SCALE
                ) as BufferedImage? ?: return errorPainter

                val bmp = image.toComposeImageBitmap()

                image.flush()

                BitmapPainter(bmp)
            }
        }
    }

    public actual fun loadPdf() {
        coroutineScope.downloadPdf(
            url = url,
            onError = {
                loadState = LoadState.Error
                pageCount = 1
            }
        ) { pdf ->
            document = Document().apply {
                setFile(pdf.absolutePath)
                pageCount = numberOfPages
                loadState = LoadState.Success
            }
        }
    }

    override fun close() {
        coroutineScope.cancel("State was closed")
        document?.dispose()
    }
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
public actual fun rememberRemotePdfState(
    url: URL,
    errorPainter: Painter,
    loadingPainter: Painter
): RemotePdfState {
    return remember(url, errorPainter, loadingPainter) {
        RemotePdfState(url, errorPainter, loadingPainter)
    }
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
public actual fun rememberRemotePdfState(
    url: String,
    errorPainter: Painter,
    loadingPainter: Painter
): RemotePdfState {
    return rememberRemotePdfState(URL(url), errorPainter, loadingPainter)
}