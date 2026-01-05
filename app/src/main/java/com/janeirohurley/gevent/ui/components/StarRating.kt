package com.janeirohurley.gevent.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.janeirohurley.gevent.R


@Composable
fun StarRating(
    rating: Int,
    maxRating: Int = 5,
    onRatingSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (i in 1..maxRating) {
            Icon(
                painter = painterResource(R.drawable.fi_rr_star),
                contentDescription = "Star $i",
                tint = if (i <= rating) activeColor else inactiveColor,
                modifier = Modifier
                    .size(size)
                    .clickable { onRatingSelected(i) }
            )
        }
    }
}
