package dev.zt64.compose.pdf

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

/**
 * Manages the state and lifecycle of a PDF document in a Compose application
 */
@Stable
public class PdfState internal constructor() : AutoCloseable {
    private lateinit var renderer: PdfRenderer
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _loadState = MutableStateFlow<PdfLoadState>(PdfLoadState.Loading)

    /**
     * The current loading state of the entire PDF document.
     */
    public val loadState: StateFlow<PdfLoadState> = _loadState.asStateFlow()

    /**
     * The number of pages in the PDF. Returns 0 until a pdf is loaded as indicated by the [loadState].
     */
    public var pageCount: Int by mutableIntStateOf(0)
        private set

    /**
     * A flow that emits a [PdfPage] object for each page in the PDF document
     */
    public val pages: Flow<PdfPage> = channelFlow {
        val job = coroutineScope.launch {
            loadState.collectLatest { state ->
                if (state == PdfLoadState.Loaded) {
                    for (index in 0 until pageCount) {
                        send(PdfPage(index, index)) // TODO: use id
                    }
                }
            }
        }
        awaitClose { job.cancel() }
    }

    @PublishedApi
    internal constructor(initBlock: suspend () -> PdfRenderer) : this() {
        coroutineScope.launch {
            try {
                _loadState.emit(PdfLoadState.Loading)
                renderer = initBlock()
                pageCount = renderer.pageCount
                _loadState.emit(PdfLoadState.Loaded)
            } catch (e: Exception) {
                _loadState.emit(PdfLoadState.Error(e))
            }
        }
    }

    /**
     * Renders a specific page as an [ImageBitmap].
     *
     * @param index the zero-based page number
     * @param zoom the amount of zoom to apply as a multiplier (1.0f = 1x zoom)
     */
    public suspend fun loadPage(index: Int, zoom: Float = 1f): ImageBitmap {
        return withContext(coroutineScope.coroutineContext) {
            renderer.renderPage(index, zoom)
        }
    }

    /**
     * Gets the dimensions of a page without rendering it.
     *
     * @param index The zero-based page number
     */
    public fun getPageSize(index: Int): IntSize {
        return renderer.getPageSize(pageIndex = index, zoom = 1f)
    }

    /**
     * Releases all resources. Call when done with the PDF.
     * Automatically called when using provided PDF composables.
     */
    public override fun close() {
        renderer.close()
        coroutineScope.cancel()
    }
}

/**
 * Represents the loading lifecycle of a PDF document
 *
 * Consumers should check for [Loaded] before calling renderer-dependent APIs such as [PdfState.loadPage] or
 * [PdfState.getPageSize].
 */
public sealed interface PdfLoadState {
    /**
     * The document is currently being opened/parsed and the renderer is not yet ready.
     */
    public data object Loading : PdfLoadState

    /**
     * The document was opened successfully and the renderer is ready to use.
     */
    public data object Loaded : PdfLoadState

    /**
     * The document failed to open/parse throwing an exception.
     *
     * @param exception The captured exception
     */
    public data class Error(public val exception: Exception) : PdfLoadState
}