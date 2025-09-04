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
 * Displays a vertical, scrollable list of PDF pages.
 *
 * @param state The [PdfState] containing the PDF document and its pages.
 * @param modifier Modifier for styling and layout.
 * @param zoom The zoom factor for rendering pages.
 * @param page
 * @param lazyListState State object for controlling and observing the scroll position.
 * @param contentPadding Padding around the content.
 * @param reverseLayout If true, reverses the order of pages.
 * @param verticalArrangement Arrangement of items vertically.
 * @param horizontalAlignment Alignment of items horizontally.
 * @param flingBehavior Customizes the fling (scroll) behavior.
 * @param userScrollEnabled Enables or disables user scrolling.
 */
@Composable
public fun PdfColumn(
    state: PdfState,
    modifier: Modifier = Modifier,
    zoom: Float = 1f,
    page: @Composable (index: Int) -> Unit = { index ->
        PdfDefaults.PdfPage(
            state = state,
            index = index
        )
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