package dev.zt64.compose.pdf.sample

import androidx.compose.ui.window.singleWindowApplication
import org.jetbrains.skiko.setSystemLookAndFeel

fun main() {
    setSystemLookAndFeel()

    singleWindowApplication(title = "Compose PDF Sample") {
        Application()
    }
}
