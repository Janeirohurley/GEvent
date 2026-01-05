package com.janeirohurley.gevent.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun Chip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: Painter? = null,
    enabled: Boolean = true,
    customColor: Color? = null,
    customContentColor: Color? = null,
    onClick: ((Boolean) -> Unit)? = null
) {
    val shape = RoundedCornerShape(7.dp)

    val backgroundColor by animateColorAsState(
        targetValue =
            when {
                !enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                selected -> customColor ?: MaterialTheme.colorScheme.primary
                else -> customColor ?: MaterialTheme.colorScheme.surface
            },
        label = "chipBackground"
    )

    val contentColor by animateColorAsState(
        targetValue =
            when {
                !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                selected -> customContentColor?: Color.White
                else -> customContentColor ?: MaterialTheme.colorScheme.primary
            },
        label = "chipContent"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .shadow(
                elevation = if (selected) 3.dp else 0.dp,
                shape = shape
            )
            .background(backgroundColor, shape)
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onClick(!selected)
                    }
                } else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {

        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}