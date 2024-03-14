package dev.zt64.compose.pdf.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pdf.PdfState

@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun PdfVerticalPager(
    state: PdfState,
    pagerState: PagerState = rememberPagerState { state.pageCount },
    modifier: Modifier = Modifier,
    page: @Composable (index: Int) -> Unit = {
        PdfPage(
            state = state,
            index = it
        )
    },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    pageSize: PageSize = PageSize.Fill,
    beyondBoundsPageCount: Int = 0,
    pageSpacing: Dp = 0.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    flingBehavior: SnapFlingBehavior = PagerDefaults.flingBehavior(pagerState),
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    pageNestedScrollConnection: NestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
        state = pagerState,
        orientation = Orientation.Vertical
    )
) {
    VerticalPager(
        state = pagerState,
        modifier = modifier,
        contentPadding = contentPadding,
        pageSize = pageSize,
        beyondBoundsPageCount = beyondBoundsPageCount,
        pageSpacing = pageSpacing,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        pageNestedScrollConnection = pageNestedScrollConnection
    ) { i ->
        page(i)
    }
}

public object PdfDefaults {
    public val PageHorizontalSpacing: Dp = 16.dp
    public val PageVerticalSpacing: Dp = 16.dp
}