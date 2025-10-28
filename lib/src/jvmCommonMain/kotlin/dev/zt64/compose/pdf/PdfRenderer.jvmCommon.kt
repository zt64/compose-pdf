package dev.zt64.compose.pdf

import java.io.File
import java.net.URL

@PublishedApi
internal expect inline fun PdfRenderer(file: File): PdfRenderer

@PublishedApi
internal expect suspend inline fun PdfRenderer(url: URL): PdfRenderer