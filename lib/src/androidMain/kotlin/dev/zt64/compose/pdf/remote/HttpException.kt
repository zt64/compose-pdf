package dev.zt64.compose.pdf.remote

import java.io.IOException

internal class HttpException(code: Int, message: String) : IOException() {
    override val message: String = "$code: $message"
}