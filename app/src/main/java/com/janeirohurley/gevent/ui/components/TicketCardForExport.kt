package com.janeirohurley.gevent.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.janeirohurley.gevent.model.TicketModel

@Composable
fun TicketCardForExport(
    ticket: TicketModel,
    onBitmapReady: (Bitmap) -> Unit
) {
    var composeView: ComposeView? by remember { mutableStateOf(null) }

    AndroidView(
        factory = { ctx ->
            ComposeView(ctx).also { view ->
                composeView = view
                view.setContent {
                    TicketCard(ticket = ticket)
                }
            }
        },
        modifier = Modifier
            .wrapContentSize()
            .onGloballyPositioned { coords ->
                val view = composeView ?: return@onGloballyPositioned

                val bitmap = Bitmap.createBitmap(
                    view.width,
                    view.height,
                    Bitmap.Config.ARGB_8888
                )

                val canvas = android.graphics.Canvas(bitmap)
                view.draw(canvas)

                onBitmapReady(bitmap)
            }
    )
}
