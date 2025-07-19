package dev.zt64.compose.pdf

import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.net.toUri
import java.io.File
import java.net.URI

public fun PdfState(parcelFileDescriptor: ParcelFileDescriptor): PdfState {
    return PdfState { PdfRenderer(parcelFileDescriptor) }
}

public fun PdfState(uri: Uri): PdfState {
    return PdfState { PdfRenderer(uri) }
}

@Composable
public fun rememberPdfState(pfd: ParcelFileDescriptor): PdfState {
    val state = rememberSaveable(
        pfd,
        saver = listSaver(
            save = { listOf(pfd.detachFd()) },
            restore = { PdfState(ParcelFileDescriptor.adoptFd(it[0])) }
        )
    ) {
        PdfState(pfd)
    }

    DisposableEffect(state) {
        onDispose { state.close() }
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
    // Save just the URI string
    val uriString = rememberSaveable { uri.toString() }

    // Create the PdfState object using the saved URI string
    val state = remember(uriString) {
        PdfState(uriString.toUri())
    }

    // Clean up when disposed
    DisposableEffect(state) {
        onDispose { state.close() }
    }

    return state
}