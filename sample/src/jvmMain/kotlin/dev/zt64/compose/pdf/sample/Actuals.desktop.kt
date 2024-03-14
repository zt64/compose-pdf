package dev.zt64.compose.pdf.sample

import androidx.compose.runtime.Composable
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.darkrockstudios.libraries.mpfilepicker.JvmFile
import dev.zt64.compose.pdf.LocalPdfState
import dev.zt64.compose.pdf.PdfState

@Composable
actual fun PdfPicker(
    show: Boolean,
    onSelectFile: (PdfState) -> Unit,
    fileExtensions: List<String>
) {
    FilePicker(
        show = show,
        fileExtensions = fileExtensions
    ) {
        it as JvmFile? ?: return@FilePicker

        onSelectFile(LocalPdfState(it!!.platformFile))
    }
}