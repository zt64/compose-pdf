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

/**
 * Create a [PdfState] from a [ParcelFileDescriptor].
 */
public fun PdfState(parcelFileDescriptor: ParcelFileDescriptor): PdfState {
    return PdfState { PdfRenderer(parcelFileDescriptor) }
}

/**
 * Create a [PdfState] from a [Uri].
 */
public fun PdfState(uri: Uri): PdfState {
    return PdfState { PdfRenderer(uri) }
}

/**
 * Remember a [PdfState] for the given [ParcelFileDescriptor].
 */
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

/**
 * Remember a [PdfState] for the given [file].
 */
@Composable
public actual fun rememberPdfState(file: File): PdfState {
    require(file.exists()) { "File does not exist" }
    require(file.isFile) { "File is not a file" }

    return rememberPdfState(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
}

/**
 * Remember a [PdfState] for the given [uri]. Disposes the state when leaving composition.
 */
@Composable
public actual fun rememberPdfState(uri: URI): PdfState {
    val uriString = rememberSaveable { uri.toString() }

    val state = remember(uriString) {
        PdfState(uriString.toUri())
    }

    DisposableEffect(state) {
        onDispose { state.close() }
    }

    return state
}