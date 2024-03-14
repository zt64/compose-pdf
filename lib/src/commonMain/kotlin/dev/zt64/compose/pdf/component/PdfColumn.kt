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

@Composable
public fun PdfColumn(
    state: PdfState,
    modifier: Modifier = Modifier,
    page: @Composable (index: Int) -> Unit = {
        PdfPage(
            state = state,
            index = it
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
            count = state.pageCount,
            key = { index -> "$index" }
        ) { i ->
            page(i)
        }
    }
}