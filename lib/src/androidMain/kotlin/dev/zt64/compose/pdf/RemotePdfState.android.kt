package dev.zt64.compose.pdf

import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import dev.zt64.compose.pdf.util.downloadPdf
import dev.zt64.compose.pdf.util.renderPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import java.net.URL

@Stable
public actual class RemotePdfState actual constructor(
    private val url: URL,
    public var errorPainter: Painter,
    public var loadingPainter: Painter
) : PdfState {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var pfd: ParcelFileDescriptor? = null
    private var renderer: PdfRenderer? = null

    actual override var pageCount: Int by mutableIntStateOf(1)
    public actual var loadState: LoadState by mutableStateOf(LoadState.Loading)

    init {
        loadPdf()
    }

    actual override fun renderPage(index: Int): Painter = when (loadState) {
        LoadState.Loading -> loadingPainter
        LoadState.Error -> errorPainter
        LoadState.Success -> renderer!!.renderPage(index)
    }

    public actual fun loadPdf() {
        coroutineScope.downloadPdf(
            url = url,
            onError = {
                loadState = LoadState.Error
                pageCount = 1
            }
        ) { pdf ->
            pfd = ParcelFileDescriptor.open(pdf, ParcelFileDescriptor.MODE_READ_ONLY).also {
                renderer = PdfRenderer(it!!)
                pageCount = renderer!!.pageCount
                loadState = LoadState.Success
            }
        }
    }

    override fun close() {
        coroutineScope.cancel("State was closed")
        renderer?.close()
        pfd?.close()
    }

    public companion object {
        /**
         * [Saver] implementation for [RemotePdfState].
         */
        public val Saver: Saver<RemotePdfState, *> = listSaver(
            save = {
                listOf(it.url, it.errorPainter, it.loadingPainter)
            },
            restore = {
                RemotePdfState(it[0] as URL, it[1] as Painter, it[2] as Painter)
            }
        )
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
public actual fun rememberRemotePdfState(url: URL, errorPainter: Painter, loadingPainter: Painter): RemotePdfState {
    val state = rememberSaveable(url, errorPainter, loadingPainter, saver = RemotePdfState.Saver) {
        RemotePdfState(url, errorPainter, loadingPainter)
    }

    DisposableEffect(state) {
        onDispose {
            state.close()
        }
    }

    return state
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
public actual fun rememberRemotePdfState(url: String, errorPainter: Painter, loadingPainter: Painter): RemotePdfState =
    rememberRemotePdfState(URL(url), errorPainter, loadingPainter)

/**
 * Returns a [RemotePdfState] for the given [uri]
 *
 * @param uri The http uri to load from
 * @param errorPainter [Painter] to use when the Pdf fails to load
 * @param loadingPainter [Painter] to use while the Pdf is loading
 * @return [RemotePdfState]
 */
@Composable
public fun rememberRemotePdfState(
    uri: Uri,
    errorPainter: Painter = rememberVectorPainter(RemotePdfDefaults.BlankIcon),
    loadingPainter: Painter = rememberVectorPainter(RemotePdfDefaults.BlankIcon)
): RemotePdfState = rememberRemotePdfState(URL(uri.toString()), errorPainter, loadingPainter)