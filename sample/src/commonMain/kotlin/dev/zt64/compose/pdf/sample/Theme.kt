package dev.zt64.compose.pdf.sample

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(), // easier to see the PDF on dark theme,
        content = content
    )
}