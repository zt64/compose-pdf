package dev.zt64.compose.pdf.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pdf.LoadState
import dev.zt64.compose.pdf.PdfState
import dev.zt64.compose.pdf.RemotePdfState

@Composable
public fun PdfPage(
    state: PdfState,
    index: Int,
    modifier: Modifier = Modifier,
    loadingIconTint: Color = Color.Gray,
    errorIconTint: Color = Color.Red,
    iconSize: Dp = 40.dp,
    loadingIndicator: @Composable () -> Unit = {
        Image(
            painter = state.renderPage(index),
            contentDescription = null,
            colorFilter = if (loadingIconTint ==
                Color.Unspecified
            ) {
                null
            } else {
                ColorFilter.tint(loadingIconTint)
            },
            modifier = Modifier.size(iconSize)
        )
    },
    errorIndicator: @Composable () -> Unit = {
        Image(
            painter = state.renderPage(index),
            contentDescription = null,
            colorFilter = if (errorIconTint ==
                Color.Unspecified
            ) {
                null
            } else {
                ColorFilter.tint(errorIconTint)
            },
            modifier = Modifier.size(iconSize)
        )
    },
    contentScale: ContentScale = ContentScale.Fit
) {
    val loadState = if (state is RemotePdfState) state.loadState else LoadState.Success

    when (loadState) {
        LoadState.Success -> Image(
            modifier = modifier.background(Color.White),
            painter = state.renderPage(index),
            contentDescription = null,
            contentScale = contentScale
        )

        LoadState.Loading -> loadingIndicator()

        LoadState.Error -> errorIndicator()
    }
}