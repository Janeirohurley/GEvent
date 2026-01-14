package com.janeirohurley.gevent.ui.components

import android.content.Context
import android.content.Intent

fun shareText(
    context: Context,
    message: String?,
    chooserTitle: String = "Partager"
) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }

    context.startActivity(
        Intent.createChooser(intent, chooserTitle)
    )
}
