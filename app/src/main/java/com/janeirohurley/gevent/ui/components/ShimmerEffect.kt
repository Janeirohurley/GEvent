package com.janeirohurley.gevent.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Effet shimmer pour les skeletons de chargement
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.5f),
        Color.LightGray.copy(alpha = 0.3f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim.value - 1000f, translateAnim.value - 1000f),
        end = Offset(translateAnim.value, translateAnim.value)
    )

    background(brush)
}

/**
 * Skeleton pour EventCard
 */
@Composable
fun EventCardSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Image skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .shimmerEffect()
            )

            // Content skeleton
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Title
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(16.dp)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatars
                    Row {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .shimmerEffect()
                                    .background(Color.Gray, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }

                    // Price chip
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(32.dp)
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

/**
 * Skeleton pour UpcomingEventCard
 */
@Composable
fun UpcomingEventCardSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(280.dp)
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shimmerEffect()
        )
    }
}

/**
 * Skeleton pour RecommendationCard
 */
@Composable
fun RecommendationCardSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Image skeleton
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Title
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(18.dp)
                            .shimmerEffect()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Date
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(14.dp)
                            .shimmerEffect()
                    )
                }

                // Price
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(28.dp)
                        .shimmerEffect()
                )
            }
        }
    }
}
