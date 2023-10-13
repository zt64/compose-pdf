package dev.zt64.compose.pdf.util

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter

internal fun PdfRenderer.renderPage(index: Int): BitmapPainter {
    require(index in 0 until pageCount) { "Page index out of bounds" }
    val bmp: Bitmap

    openPage(index).use { page ->
        bmp = Bitmap.createBitmap(page.width.dp, page.height.dp, Bitmap.Config.ARGB_8888)
        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
    }

    return BitmapPainter(bmp.asImageBitmap())
}