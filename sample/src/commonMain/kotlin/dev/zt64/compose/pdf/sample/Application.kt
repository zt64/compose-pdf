package dev.zt64.compose.pdf.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import dev.zt64.compose.pdf.Pdf
import dev.zt64.compose.pdf.PdfState
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable

@Composable
fun Application() {
    Theme {
        var pdf by remember { mutableStateOf<PdfState?>(null, referentialEqualityPolicy()) }

        if (pdf == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                var showFilePicker by rememberSaveable { mutableStateOf(false) }

                PdfPicker(
                    show = showFilePicker,
                    {
                        pdf = it
                        showFilePicker = false
                    }
                )

                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = { showFilePicker = true }
                ) {
                    Text("Select PDF source")
                }
            }
        } else {
            PdfScreen(
                pdf = pdf!!,
                onClickBack = { pdf = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun PdfScreen(
    pdf: PdfState,
    onClickBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    var scale by rememberSaveable { mutableStateOf(1f) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Compose PDF viewer") },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { scale -= 0.1f }) {
                        Icon(Icons.Default.ZoomOut, contentDescription = null)
                    }

                    IconButton(onClick = { scale += 0.1f }) {
                        Icon(Icons.Default.ZoomIn, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .zoomable(rememberZoomableState())
        ) {
            val lazyListState = rememberLazyListState()

            Pdf(
                modifier = Modifier.scale(scale),
                state = pdf,
                lazyListState = lazyListState,
            )

            val currentPage by remember {
                derivedStateOf { lazyListState.firstVisibleItemIndex + 1 }
            }

            Text(
                modifier = Modifier.align(Alignment.BottomStart),
                text = "Page $currentPage of ${pdf.pageCount}"
            )

            VerticalScrollbar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                lazyListState = lazyListState,
            )
        }
    }
}

@Composable
expect fun PdfPicker(
    show: Boolean,
    onSelectFile: (PdfState) -> Unit,
    fileExtensions: List<String> = listOf("pdf")
)

@Composable
expect fun VerticalScrollbar(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState
)