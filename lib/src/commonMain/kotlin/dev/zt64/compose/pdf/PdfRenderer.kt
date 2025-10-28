package dev.zt64.compose.pdf

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize

// The functions here serve as bindings to the platforms pdf renderer
// In the future this may be replaced with a cross-platform renderer

internal expect class PdfRenderer

internal expect inline val PdfRenderer.pageCount: Int

internal expect suspend inline fun PdfRenderer.renderPage(pageIndex: Int, zoom: Float = 1f): ImageBitmap

internal expect inline fun PdfRenderer.getPageSize(pageIndex: Int, zoom: Float = 1f): IntSize

internal expect inline fun PdfRenderer.close()