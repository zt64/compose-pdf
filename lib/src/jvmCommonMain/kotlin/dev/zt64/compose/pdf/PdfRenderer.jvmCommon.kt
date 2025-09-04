package dev.zt64.compose.pdf

import java.io.File
import java.net.URL

internal expect inline fun PdfRenderer(file: File): PdfRenderer

internal expect suspend inline fun PdfRenderer(url: URL): PdfRenderer