package dev.zt64.compose.pdf.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pdf.PdfLoadState
import dev.zt64.compose.pdf.PdfState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

public object PdfDefaults {
    public val PageHorizontalSpacing: Dp = 16.dp
    public val PageVerticalSpacing: Dp = 16.dp

    /**
     * PDF page component that displays a single page of a PDF document.
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
        contentScale: ContentScale = ContentScale.FillWidth
    ) {
        var pageLoadState by remember { mutableStateOf<PdfLoadState>(PdfLoadState.Loading) }
        var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(index) {
            try {
                scope.launch(Dispatchers.IO) {
                    pageLoadState = PdfLoadState.Loading
                    bitmap = state.loadPage(index)
                    pageLoadState = PdfLoadState.Loaded
                }
            } catch (e: Exception) {
                pageLoadState = PdfLoadState.Error(e)
            }
        }

        val pageSize = with(LocalDensity.current) {
            state.getPageSize(index).let {
                DpSize(it.width.toDp(), it.height.toDp())
            }
        }

        when (pageLoadState) {
            PdfLoadState.Loaded -> {
                Image(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxSize(),
                    bitmap = bitmap!!,
                    contentDescription = "Page $index",
                    contentScale = contentScale
                )
            }
            is PdfLoadState.Error -> {
                Box(
                    modifier = modifier.size(pageSize),
                    contentAlignment = Alignment.Center
                ) {
                    errorIndicator()
                }
            }
            PdfLoadState.Loading -> {
                Box(
                    modifier = modifier.size(pageSize),
                    contentAlignment = Alignment.Center
                ) {
                    loadingIndicator()
                }
            }
        }
    }
}