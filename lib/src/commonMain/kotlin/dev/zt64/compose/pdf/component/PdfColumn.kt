package dev.zt64.compose.pdf.component

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pdf.PdfState
import dev.zt64.compose.pdf.collectAsLazyPdfPages

/**
 * Displays a vertically scrollable list of PDF pages backed by a [PdfState].
 *
 * This composable collects pages from [PdfState] using [collectAsLazyPdfPages] and displays them
 * in a [LazyColumn] with stable item keys.
 *
 * @param state The [PdfState] providing document and page data.
 * @param modifier Modifier to be passed to the inner [LazyColumn] for layout and appearance.
 * @param zoom The scale factor applied when rendering each page.
 * @param page A composable lambda that renders a single page.
 * @param lazyListState A [LazyListState] to control and observe scrolling.
 * @param contentPadding Padding applied around the list content.
 * @param reverseLayout If true, items are laid out in reverse order.
 * @param verticalArrangement How items are arranged vertically (spacing/alignment).
 * @param horizontalAlignment How items are aligned horizontally within the column.
 * @param flingBehavior Custom [FlingBehavior] for scroll fling interactions.
 * @param userScrollEnabled Whether user-initiated scrolling is allowed.
 */
@Composable
public fun PdfColumn(
    state: PdfState,
    modifier: Modifier = Modifier,
    zoom: Float = 1f,
    page: @Composable (index: Int) -> Unit = { index ->
        PdfDefaults.PdfPage(state, index)
    },
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(
        space = 8.dp,
        alignment = if (!reverseLayout) Alignment.Top else Alignment.Bottom
    ),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true
) {
    val pages = state.pages.collectAsLazyPdfPages()

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled
    ) {
        items(
            count = pages.itemCount,
            key = { i -> pages[i].id }
        ) { i ->
            page(i)
        }
    }
}