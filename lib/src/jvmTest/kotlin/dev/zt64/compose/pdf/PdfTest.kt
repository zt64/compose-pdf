package dev.zt64.compose.pdf

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pdf.component.PdfColumn
import org.junit.Rule
import org.junit.Test
import java.net.URI
import kotlin.test.assertTrue

private const val PDF_URL = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"

class PdfTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `should load from file`() {
        lateinit var state: PdfState
        compose.setContent {
            state = rememberPdfState(this::class.java.getResource("/dummy.pdf")!!)

            Box(modifier = Modifier.size(width = 400.dp, height = 800.dp)) {
                PdfColumn(state)
            }
        }

        compose.waitUntil(10000) {
            state.loadState.value == PdfLoadState.Loaded
        }

        compose.runOnIdle {
            assertTrue(state.pageCount > 0)
        }
    }

    @Test
    fun `should load from url`() {
        lateinit var state: PdfState

        compose.setContent {
            state = rememberPdfState(URI(PDF_URL))
            Box(modifier = Modifier.size(width = 400.dp, height = 800.dp)) {
                PdfColumn(state)
            }
        }

        compose.waitUntil(10000) {
            state.loadState.value == PdfLoadState.Loaded
        }

        compose.runOnIdle {
            assert(state.pageCount > 0)
        }
    }
}