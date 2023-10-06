package dev.zt64.compose.pdf.sample

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.darkrockstudios.libraries.mpfilepicker.AndroidFile
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import dev.zt64.compose.pdf.PdfState

@Composable
actual fun PdfPicker(
    show: Boolean,
    onSelectFile: (PdfState) -> Unit,
    fileExtensions: List<String>
) {
    val context = LocalContext.current

    FilePicker(
        show = show,
        fileExtensions = fileExtensions
    ) {
        it as AndroidFile? ?: return@FilePicker

        onSelectFile(PdfState(context.contentResolver.openFileDescriptor(it!!.platformFile, "r")!!))
    }
}

@Composable
actual fun ProvideScrollbarStyle(content: @Composable () -> Unit) = content() // no-op

@Composable
actual fun VerticalScrollbar(
    modifier: Modifier,
    lazyListState: LazyListState
) {
    // no-op
}