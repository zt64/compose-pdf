package dev.zt64.compose.pdf

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.Flow

public class PdfPage(public val index: Int, public val id: Int)

@Stable
public class LazyPdfPages(public val pages: SnapshotStateList<PdfPage>) {
    public val itemCount: Int
        get() = pages.size

    public operator fun get(index: Int): PdfPage = pages[index]
}

@Composable
public fun Flow<PdfPage>.collectAsLazyPdfPages(): LazyPdfPages {
    val pages = remember { mutableStateListOf<PdfPage>() }
    val lazyPdfPages = remember { LazyPdfPages(pages) }

    LaunchedEffect(this) {
        collect { page ->
            // This assumes the flow emits pages in order and doesn't clear the list.
            // If the flow can be re-collected, you might want to clear `pages` first.
            pages.add(page)
        }
    }

    return lazyPdfPages
}