package dev.zt64.compose.pdf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.icepdf.core.pobjects.Document
import org.icepdf.core.pobjects.Page
import org.icepdf.core.util.GraphicsRenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.URL

@Stable
public actual class PdfState private constructor() : AutoCloseable {
    private val document = Document()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    public constructor(inputStream: InputStream, pathOrUrl: String = "") : this() {
        init { document.setInputStream(inputStream, pathOrUrl) }
    }

    public constructor(file: File) : this() {
        init { document.setFile(file.absolutePath) }
    }

    public actual constructor(url: URL) : this() {
        init { document.setUrl(url) }
    }

    public constructor(uri: URI) : this() {
        init {
            if (uri.scheme == "file") {
                document.setFile(File(uri).absolutePath)
            } else {
                document.setUrl(uri.toURL())
            }
        }
    }

    public actual val pageCount: Int
        get() = if (document.pageTree != null) document.numberOfPages else 0

    private val _loadState = MutableStateFlow<LoadState>(LoadState.Loading)
    public actual val loadState: StateFlow<LoadState> get() = _loadState

    public actual fun loadPage(
        index: Int,
        zoom: Float
    ): ImageBitmap {
        val image = document.getPageImage(
            // pageNumber =
            index,
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

    public actual val pages: Flow<PdfPage> = channelFlow {
        val job = coroutineScope.launch {
            loadState.collectLatest { state ->
                if (state == LoadState.Loaded) {
                    for (index in 0 until pageCount) {
                        // val imageBitmap = loadPage(index, 1f)

                        send(PdfPage(index, document.pageTree.getPageReference(index).objectNumber))
                    }
                }
            }
        }
        awaitClose { job.cancel() }
    }

    override fun close() {
        document.dispose()
        coroutineScope.cancel()
    }

    private fun init(initBlock: suspend () -> Unit) {
        coroutineScope.launch {
            try {
                initBlock()
                _loadState.emit(LoadState.Loaded)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e))
            }
        }
    }
}

@Composable
public actual fun rememberPdfState(file: File): PdfState {
    val state = rememberSaveable(
        file,
        saver = listSaver(
            save = {
                listOf(file.absolutePath)
            },
            restore = {
                PdfState(File(it[0]))
            }
        )
    ) {
        PdfState(file)
    }

    DisposableEffect(state) {
        onDispose { state.close() }
    }

    return state
}

@Composable
public actual fun rememberPdfState(uri: URI): PdfState {
    val state = rememberSaveable(
        uri,
        saver = listSaver(
            save = {
                listOf(uri.toString())
            },
            restore = {
                PdfState(URI(it[0]))
            }
        )
    ) {
        PdfState(uri)
    }

    DisposableEffect(state) {
        onDispose { state.close() }
    }

    return state
}