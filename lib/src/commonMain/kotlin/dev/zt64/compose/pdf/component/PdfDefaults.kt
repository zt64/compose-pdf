package dev.zt64.compose.pdf.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pdf.LoadState
import dev.zt64.compose.pdf.PdfState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

public object PdfDefaults {
    public val PageHorizontalSpacing: Dp = 16.dp
    public val PageVerticalSpacing: Dp = 16.dp

    /**
     * PDF page
     *
     * @param state The PDF state managing the PDF document
     * @param index The page index to display (0-based)
     * @param modifier Modifier to apply to the page
     * @param loadingIndicator Indicator to show while the page is loading
     * @param errorIndicator Indicator to show if an error occurs while loading the page
     * @param contentScale The scale to apply to the content of the page
     */
    @Composable
    public fun PdfPage(
        state: PdfState,
        index: Int,
        modifier: Modifier = Modifier,
        loadingIndicator: @Composable () -> Unit = {},
        errorIndicator: @Composable () -> Unit = {},
        contentScale: ContentScale = ContentScale.FillHeight
    ) {
        var pageLoadState by remember { mutableStateOf<LoadState>(LoadState.Loading) }
        var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(index) {
            pageLoadState = LoadState.Loading
            try {
                scope.launch(Dispatchers.IO) {
                    bitmap = state.loadPage(index)
                    pageLoadState = LoadState.Loaded
                }
            } catch (e: Exception) {
                pageLoadState = LoadState.Error(e)
            }
        }

        val pageSize: DpSize = with(LocalDensity.current) {
            state.getPageSize(index).let {
                DpSize(it.width.toDp(), it.height.toDp())
            }
        }

        Box(
            modifier = modifier.then(Modifier.size(pageSize.width, pageSize.height)),
            contentAlignment = Alignment.Center
        ) {
            when (pageLoadState) {
                LoadState.Loading -> {
                    loadingIndicator()
                }

                LoadState.Loaded -> {
                    Image(
                        bitmap = bitmap!!,
                        contentDescription = "Page $index",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale
                    )
                }

                is LoadState.Error -> {
                    errorIndicator()
                }
            }
        }
    }
}