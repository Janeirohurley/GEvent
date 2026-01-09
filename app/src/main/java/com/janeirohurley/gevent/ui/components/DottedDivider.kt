package com.janeirohurley.gevent.ui.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DottedDivider(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val dash = 8.dp.toPx()
        val gap = 6.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawLine(
                color = Color.LightGray,
                start = Offset(x, 0f),
                end = Offset(x + dash, 0f),
                strokeWidth = size.height
            )
            x += dash + gap
        }
    }
}

