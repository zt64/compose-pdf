package dev.zt64.compose.pdf

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public class PdfPage(
    public val index: Int,
    public val id: Int
    // public val imageBitmap: ImageBitmap
)

public class LazyPdfPages(private val flow: Flow<PdfPage>) {
    private val mainDispatcher = Dispatchers.Main
    private val coroutineScope = CoroutineScope(mainDispatcher + Job())

    public var itemSnapshotList: SnapshotStateList<PdfPage> = mutableStateListOf()
        private set

    public val itemCount: Int
        get() = itemSnapshotList.size

    public operator fun get(index: Int): PdfPage? {
        return itemSnapshotList.getOrNull(index)
    }

    public fun peek(index: Int): PdfPage? {
        return itemSnapshotList.getOrNull(index)
    }

    public fun retry() {
        // Implement retry logic if needed
    }

    internal suspend fun collectPagingData() {
        flow.collectLatest { page ->
            itemSnapshotList.add(page)
        }
    }

    internal suspend fun collectLoadState() {
        // Implement load state collection if needed
    }
}

@Composable
public fun Flow<PdfPage>.collectAsLazyPdfPages(
    context: CoroutineContext = EmptyCoroutineContext
): LazyPdfPages {
    val lazyPdfPages = remember(this) { LazyPdfPages(this) }

    LaunchedEffect(lazyPdfPages) {
        if (context == EmptyCoroutineContext) {
            lazyPdfPages.collectPagingData()
        } else {
            withContext(context) { lazyPdfPages.collectPagingData() }
        }
    }

    return lazyPdfPages
}