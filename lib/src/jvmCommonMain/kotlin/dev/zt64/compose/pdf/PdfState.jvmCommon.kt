@file:Suppress("NOTHING_TO_INLINE")

package dev.zt64.compose.pdf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import java.io.File
import java.net.URI
import java.net.URL

/**
 * Create a [PdfState] from a [file].
 */
public inline fun PdfState(file: File): PdfState {
    return PdfState { PdfRenderer(file) }
}

/**
 * Create a [PdfState] from a [url].
 */
public inline fun PdfState(url: URL): PdfState {
    return PdfState { PdfRenderer(url) }
}

/**
 * Remember a [PdfState] for the given [file].
 */
@Composable
public expect fun rememberPdfState(file: File): PdfState

/**
 * Remember a [PdfState] for the given [url]. Disposes the state when leaving composition.
 */
@Composable
public fun rememberPdfState(url: URL): PdfState {
    val state = rememberSaveable(
        url,
        saver = listSaver(
            save = { listOf(url.toString()) },
            restore = { PdfState(URL(it[0])) }
        )
    ) {
        PdfState(url)
    }

    DisposableEffect(state) {
        onDispose { state.close() }
    }

    return state
}

/**
 * Remember a [PdfState] for the given [uri].
 */
@Composable
public expect fun rememberPdfState(uri: URI): PdfState