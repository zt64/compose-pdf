package dev.zt64.compose.pdf.component

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pdf.PdfState

/**
 * Displays a horizontally scrollable pager for PDF pages.
 *
 * @param state The [PdfState] managing the PDF document and its state.
 * @param pagerState The [PagerState] controlling the pager's scroll position.
 * @param modifier The [Modifier] applied to the inner [HorizontalPager].
 * @param page Composable lambda that renders a single page.
 * @param contentPadding Padding values applied around the content.
 * @param pageSize The [PageSize] of each page in the pager.
 * @param beyondViewportPageCount Number of pages to keep composed beyond the viewport.
 * @param pageSpacing Spacing between pages.
 * @param verticalAlignment Vertical alignment of pages.
 * @param flingBehavior Fling behavior for scroll gestures.
 * @param userScrollEnabled Whether user-initiated scrolling is enabled.
 * @param reverseLayout Whether to reverse the layout direction.
 * @param pageNestedScrollConnection Nested scroll connection for the pager.
 */
@Composable
public fun PdfHorizontalPager(
    state: PdfState,
    pagerState: PagerState = rememberPagerState { state.pageCount },
    modifier: Modifier = Modifier,
    page: @Composable (index: Int) -> Unit = {
        PdfDefaults.PdfPage(
            state = state,
            index = it
        )
    },
    contentPadding: PaddingValues = PaddingValues.Zero,
    pageSize: PageSize = PageSize.Fill,
    beyondViewportPageCount: Int = 0,
    pageSpacing: Dp = 0.dp,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    flingBehavior: TargetedFlingBehavior = PagerDefaults.flingBehavior(state = pagerState),
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    pageNestedScrollConnection: NestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
        state = pagerState,
        orientation = Orientation.Horizontal
    )
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        contentPadding = contentPadding,
        pageSize = pageSize,
        beyondViewportPageCount = beyondViewportPageCount,
        pageSpacing = pageSpacing,
        verticalAlignment = verticalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        pageNestedScrollConnection = pageNestedScrollConnection
    ) { i ->
        page(i)
    }
}