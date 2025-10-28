@file:Suppress("NOTHING_TO_INLINE")

package dev.zt64.compose.pdf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import java.io.File
import java.net.URI

/**
 * Create a [PdfState] instance from a [URI]
 */
public inline fun PdfState(uri: URI): PdfState {
    return PdfState { PdfRenderer(uri) }
}

@Composable
public actual fun rememberPdfState(file: File): PdfState {
    val state = rememberSaveable(
        file,
        saver = listSaver(
            save = { listOf(file.absolutePath) },
            restore = { PdfState(File(it[0])) }
        )
    ) {
        PdfState(file)
    }

    DisposableEffect(state) {
        onDispose { state.close() }
    }

    return state
}

@Composable
public actual fun rememberPdfState(uri: URI): PdfState {
    val state = rememberSaveable(
        uri,
        saver = listSaver(
            save = { listOf(uri.toString()) },
            restore = { PdfState(URI(it[0])) }
        )
    ) {
        PdfState(uri)
    }

    DisposableEffect(state) {
        onDispose { state.close() }
    }

    return state
}