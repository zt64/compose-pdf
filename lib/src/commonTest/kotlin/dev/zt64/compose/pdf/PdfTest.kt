package dev.zt64.compose.pdf

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import java.net.URL

private const val PDF_URL = "https://opensource.adobe.com/dc-acrobat-sdk-docs/pdfstandards/PDF32000_2008.pdf"

class PdfTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `should load from file`() {
        compose.setContent {
            val state = this::class.java.getResource("/dummy.pdf")?.let {
                rememberPdfState(it)
            } ?: error("dummy.pdf not found")

            PdfColumn(state)
        }
    }

    @Test
    fun `should load from url`() {
        compose.setContent {
            val state = rememberPdfState(URL(PDF_URL))

            PdfColumn(state)
        }

        compose.waitForIdle()
    }
}