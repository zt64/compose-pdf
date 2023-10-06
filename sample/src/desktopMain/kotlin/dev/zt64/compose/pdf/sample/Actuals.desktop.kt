package dev.zt64.compose.pdf.sample

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.darkrockstudios.libraries.mpfilepicker.JvmFile
import dev.zt64.compose.pdf.PdfState

@Composable
actual fun ProvideScrollbarStyle(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalScrollbarStyle provides ScrollbarStyle(
            minimalHeight = 16.dp,
            thickness = 8.dp,
            shape = RoundedCornerShape(4.dp),
            hoverDurationMillis = 300,
            unhoverColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
            hoverColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.60f)
        ),
        content = content,
    )
}

@Composable
actual fun PdfPicker(show: Boolean, onSelectFile: (PdfState) -> Unit, fileExtensions: List<String>) {
    FilePicker(
        show = show,
        fileExtensions = fileExtensions,
    ) {
        it as JvmFile? ?: return@FilePicker

        onSelectFile(PdfState(it!!.platformFile))
    }
}

@Composable
actual fun VerticalScrollbar(
    modifier: Modifier,
    lazyListState: LazyListState
) {
    androidx.compose.foundation.VerticalScrollbar(
        modifier = modifier,
        adapter = rememberScrollbarAdapter(lazyListState),
    )
}