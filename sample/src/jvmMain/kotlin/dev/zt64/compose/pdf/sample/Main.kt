package dev.zt64.compose.pdf.sample

import androidx.compose.ui.window.singleWindowApplication
import java.awt.Dimension

fun main() {
    singleWindowApplication(title = "Compose PDF Sample") {
        window.minimumSize = Dimension(800, 600)
        Application()
    }
}