package dev.zt64.compose.pdf

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.Flow

/**
 * Represents a single page within a PDF document.
 *
 * This class holds metadata about a page, which can be used for rendering
 * and identification within a lazy layout.
 *
 * @property index The zero-based index of the page in the PDF document.
 * @property id A unique identifier for this page instance, suitable for use as a key.
 */
public class PdfPage(public val index: Int, public val id: Int)

/**
 * A stable wrapper around a mutable state list of [PdfPage] items for use in lazy layouts.
 *
 * Exposes the observable [pages] list and convenience helpers such as [itemCount] and the
 * index operator.
 *
 * @property pages The underlying [SnapshotStateList] of [PdfPage] items observed by Compose.
 * @property itemCount The current number of pages.
 */
@Stable
public class LazyPdfPages(public val pages: SnapshotStateList<PdfPage>) {
    public val itemCount: Int
        get() = pages.size

    public operator fun get(index: Int): PdfPage = pages[index]
}

/**
 * Collects a [Flow] of [PdfPage]s and exposes them as a [LazyPdfPages] instance.
 *
 * @receiver Flow emitting [PdfPage]s to be collected.
 * @return [LazyPdfPages] containing the collected pages.
 */
@Composable
public fun Flow<PdfPage>.collectAsLazyPdfPages(): LazyPdfPages {
    val pages = remember { mutableStateListOf<PdfPage>() }
    val lazyPdfPages = remember { LazyPdfPages(pages) }

    LaunchedEffect(this) {
        collect { page -> pages += page }
    }

    return lazyPdfPages
}