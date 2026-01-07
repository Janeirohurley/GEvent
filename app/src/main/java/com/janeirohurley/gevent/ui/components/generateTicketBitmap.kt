package com.janeirohurley.gevent.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import com.janeirohurley.gevent.model.TicketModel

@Composable
fun generateTicketBitmap(context: Context, ticket: TicketModel): Bitmap {
    val density = LocalDensity.current
    var bitmap: Bitmap? = null

    val composeView = ComposeView(context).apply {
        setContent {
            // On réutilise ton TicketCard
            TicketCard(ticket = ticket)
        }
    }

    composeView.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

    bitmap = Bitmap.createBitmap(composeView.width, composeView.height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    composeView.draw(canvas)

    return bitmap
}
