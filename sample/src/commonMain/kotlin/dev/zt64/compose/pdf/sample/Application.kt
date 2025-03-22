package dev.zt64.compose.pdf.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pdf.component.PdfColumn
import dev.zt64.compose.pdf.rememberPdfState
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import java.net.URI

sealed interface Destination {
    data object Home : Destination

    data class Pdf(val pdf: URI) : Destination
}

@Composable
fun Application() {
    Theme {
        var destination by remember {
            mutableStateOf<Destination>(Destination.Home)
        }

        when (val dest = destination) {
            Destination.Home -> {
                HomeScreen(
                    onSelectPdf = { destination = Destination.Pdf(it) }
                )
            }

            is Destination.Pdf -> {
                PdfScreen(
                    uri = dest.pdf,
                    onClickBack = { destination = Destination.Home }
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(onSelectPdf: (URI) -> Unit) {
    var text by rememberSaveable {
        mutableStateOf("https://pdfobject.com/pdf/sample.pdf")
    }
    var uri by rememberSaveable {
        mutableStateOf<URI?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        val scope = rememberCoroutineScope()

        val pickerLauncher = rememberFilePickerLauncher(
            type = PickerType.File(listOf("pdf")),
            mode = PickerMode.Single,
            title = "Pick a PDF file"
        ) { platformFile ->
            if (platformFile == null) return@rememberFilePickerLauncher

            scope.launch {
                onSelectPdf(platformFile.file.toURI())
            }
        }

        Button(onClick = pickerLauncher::launch) {
            Text("Select PDF file")
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        val isValidUrl by remember {
            derivedStateOf {
                if (text.isEmpty()) return@derivedStateOf false
                try {
                    uri = URI(text)
                    uri!!.toURL()
                    true
                } catch (_: Exception) {
                    false
                }
            }
        }

        OutlinedTextField(
            value = text,
            onValueChange = { value ->
                text = value.filterNot { it.isWhitespace() }
            },
            label = {
                Text("URL")
            },
            supportingText = if (!isValidUrl && text.isNotEmpty()) {
                { Text("Invalid URL", color = MaterialTheme.colorScheme.error) }
            } else {
                null
            },
            isError = !isValidUrl && text.isNotEmpty(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done
            )
        )

        Button(
            enabled = isValidUrl,
            onClick = {
                onSelectPdf(uri!!)
            }
        ) {
            Text("Load from url")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PdfScreen(
    uri: URI,
    onClickBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    val state = rememberPdfState(uri)
    val lazyListState = rememberLazyListState()
    val currentPage by rememberSaveable(lazyListState.firstVisibleItemScrollOffset) {
        derivedStateOf { lazyListState.firstVisibleItemIndex + 1 }
    }

    val zoom = rememberZoomableState()
    val scope = rememberCoroutineScope()

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
                    IconButton(
                        onClick = {
                            scope.launch {
                                zoom.zoomBy(0.9f)
                            }
                        }
                    ) {
                        Icon(Icons.Default.ZoomOut, contentDescription = null)
                    }

                    IconButton(
                        onClick = {
                            scope.launch {
                                zoom.zoomBy(1.1f)
                            }
                        }
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomAppBar {
                Text("Page $currentPage of ${state.pageCount}")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        // TODO: Implement Ctrl + Scroll to zoom
        PdfColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .zoomable(zoom),
            state = state,
            lazyListState = lazyListState
        )
    }
}