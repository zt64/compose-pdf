package dev.zt64.compose.pdf.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pdf.LoadState
import dev.zt64.compose.pdf.PdfState

/**
 * Pdf page
 *
 * @param state
 * @param index
 * @param modifier
 * @param loadingIndicator
 * @param errorIndicator
 * @param contentScale
 */
@Composable
public fun PdfPage(
    state: PdfState,
    index: Int,
    modifier: Modifier = Modifier,
    loadingIndicator: @Composable () -> Unit = {},
    errorIndicator: @Composable () -> Unit = {},
    contentScale: ContentScale = ContentScale.Fit
) {
    val scope = rememberCoroutineScope()

    val loadState by state.loadState.collectAsState()
    when (loadState) {
        LoadState.Loading -> loadingIndicator()

        LoadState.Loaded -> {
            val b = state.loadPage(index)
            Canvas(
                modifier = modifier
                    .width(b.width.dp)
                    .height(b.height.dp)
            ) {
                drawImage(b)
            }
        }

        is LoadState.Error -> errorIndicator()
    }
}