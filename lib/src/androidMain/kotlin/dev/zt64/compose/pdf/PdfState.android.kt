package dev.zt64.compose.pdf

import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.ImageBitmap
import dev.zt64.compose.pdf.remote.Downloader
import dev.zt64.compose.pdf.util.renderPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI
import java.net.URL

@Stable
public actual class PdfState private constructor() : AutoCloseable {
    private lateinit var renderer: PdfRenderer
    public actual val pageCount: Int = renderer.pageCount
    public actual var loadState: LoadState by mutableStateOf(LoadState.Loaded)

    public constructor(pfd: ParcelFileDescriptor) : this() {
        renderer = PdfRenderer(pfd)
    }

    public constructor(uri: Uri) : this() {
        loadState = LoadState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            loadState = try {
                val pdfFile = when (uri.scheme) {
                    "file" -> File(uri.path!!)

                    "http", "https" -> {
                        val name = uri.lastPathSegment?.removeSuffix(".pdf") ?: "PdfFile"
                        val tempFile = File.createTempFile(name, ".pdf")
                        Downloader.download(URL(uri.toString()), tempFile)
                        tempFile
                    }

                    else -> throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}")
                }

                val pfd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                renderer = PdfRenderer(pfd)
                LoadState.Loaded
            } catch (e: Exception) {
                LoadState.Error(e)
            }
        }
    }

    public actual constructor(url: URL) : this() {
        loadState = LoadState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            val name = url.path.split("/").lastOrNull()?.removeSuffix(".pdf") ?: "PdfFile"
            val pdfFile = File.createTempFile(name, ".pdf")
            if (!pdfFile.exists()) Downloader.download(url, pdfFile)
            renderer = PdfRenderer(
                ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            )
            loadState = LoadState.Loaded
        }
    }

    public actual fun loadPage(
        index: Int,
        zoom: Float
    ): ImageBitmap {
        return renderer.renderPage(index)
    }

    override fun close() {
        renderer.close()
    }
}

@Composable
public fun rememberPdfState(pfd: ParcelFileDescriptor): PdfState {
    val state = rememberSaveable(
        pfd,
        saver = listSaver(
            save = {
                listOf(pfd.detachFd())
            },
            restore = {
                PdfState(ParcelFileDescriptor.adoptFd(it[0]))
            }
        )
    ) {
        PdfState(pfd)
    }

    DisposableEffect(state) {
        onDispose {
            state.close()
        }
    }

    return state
}

@Composable
public actual fun rememberPdfState(file: File): PdfState {
    require(file.exists()) { "File does not exist" }
    require(file.isFile) { "File is not a file" }

    return rememberPdfState(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
}

@Composable
public actual fun rememberPdfState(uri: URI): PdfState {
    TODO()
}