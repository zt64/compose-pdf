package dev.zt64.compose.pdf.util

import android.content.res.Resources

internal val density: Float
    get() = Resources.getSystem().displayMetrics.density

internal val Int.dp: Int
    get() = (this * density + 0.5f).toInt()