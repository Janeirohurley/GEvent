// ui/component/FilterChip.kt
package com.janeirohurley.gevent.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun Filter(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    customColor: Color? = null,
    customContentColor: Color?= null
) {
    Chip(
        text = label,
        modifier = modifier,
        selected = isSelected,
        customColor = customColor,
        customContentColor = customContentColor,
        onClick = { _ -> onClick() }
    )
}
