package dev.zt64.compose.pdf

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

/**
 * Pdf state
 *
 * @constructor
 */
@Stable
public class PdfState internal constructor() : AutoCloseable {
    private lateinit var renderer: PdfRenderer
    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    private val _loadState = MutableStateFlow<LoadState>(LoadState.Loading)
    public val loadState: StateFlow<LoadState> get() = _loadState

    public var pageCount: Int by mutableIntStateOf(0)
        private set

    public val pages: Flow<PdfPage> = channelFlow {
        val job = coroutineScope.launch {
            loadState.collectLatest { state ->
                if (state == LoadState.Loaded) {
                    for (index in 0 until pageCount) {
                        send(PdfPage(index, index)) // TODO: use id
                    }
                }
            }
        }
        awaitClose { job.cancel() }
    }

    internal constructor(
        initBlock: suspend () -> PdfRenderer
    ) : this() {
        init(initBlock)
    }

    /**
     * @param index the page number to render
     */
    public suspend fun loadPage(
        index: Int,
        zoom: Float = 1f
    ): ImageBitmap {
        return withContext(Dispatchers.IO) {
            renderer.renderPage(index, zoom)
        }
    }

    public fun getPageSize(index: Int): IntSize {
        return renderer.getPageSize(pageIndex = index, zoom = 1f)
    }

    public override fun close() {
        renderer.close()
        coroutineScope.cancel()
    }

    internal fun init(initBlock: suspend () -> PdfRenderer) {
        coroutineScope.launch {
            try {
                _loadState.emit(LoadState.Loading)
                renderer = initBlock()
                pageCount = renderer.pageCount
                _loadState.emit(LoadState.Loaded)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e))
            }
        }
    }
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