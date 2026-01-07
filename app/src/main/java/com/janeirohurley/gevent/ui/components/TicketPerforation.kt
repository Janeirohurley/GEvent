package com.janeirohurley.gevent.ui.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun TicketPerforation(
    cardWidth: Dp,
    cardHeight: Dp
) {
    val circleRadius = 25.dp // demi-cercle = rayon
    val y = cardHeight / 2 + 137.dp
    val perforationColor = MaterialTheme.colorScheme.background

    Canvas(modifier = Modifier.fillMaxSize()) {
        val radiusPx = circleRadius.toPx()
        val yPx = y.toPx()

        // Demi-cercle gauche inversé
        drawArc(
            color = perforationColor,
            startAngle = 270f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(-radiusPx, yPx - radiusPx),
            size = Size(radiusPx * 2, radiusPx * 2)
        )

        // Demi-cercle droite inversé
        drawArc(
            color = perforationColor,
            startAngle = 90f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cardWidth.toPx() - radiusPx, yPx - radiusPx),
            size = Size(radiusPx * 2, radiusPx * 2)
        )
    }
}

