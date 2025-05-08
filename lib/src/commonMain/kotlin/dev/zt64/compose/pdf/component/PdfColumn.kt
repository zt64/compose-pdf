package dev.zt64.compose.pdf.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pdf.PdfState
import dev.zt64.compose.pdf.collectAsLazyPdfPages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Pdf column
 *
 * @param state
 * @param modifier
 * @param page
 * @param lazyListState
 * @param contentPadding
 * @param reverseLayout
 * @param verticalArrangement
 * @param horizontalAlignment
 * @param flingBehavior
 * @param userScrollEnabled
 */
@Composable
public fun PdfColumn(
    state: PdfState,
    modifier: Modifier = Modifier,
    zoom: Float = 1f,
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
            key = { i -> pages[i]?.id ?: i }
        ) { i ->
            val page = pages[i]

            if (page == null) {
                BasicText(
                    text = "Loading page... $i",
                    color = { Color.White }
                )
            } else {
                val scope = rememberCoroutineScope { Dispatchers.IO }
                var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

                LaunchedEffect(Unit) {
                    scope.launch {
                        imageBitmap = state.loadPage(page.index, zoom)
                    }
                }

                if (imageBitmap != null) {
                    Canvas(
                        modifier = Modifier
                            .width(imageBitmap!!.width.dp)
                            .height(imageBitmap!!.height.dp)
                    ) {
                        drawImage(imageBitmap!!)
                    }
                } else {
                    BasicText(
                        text = "Loading... ${page.index}",
                        color = { Color.White }
                    )
                }
            }
        }
    }
}