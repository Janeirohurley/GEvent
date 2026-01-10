package com.janeirohurley.gevent.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage


@Composable
fun AvatarGroup(
    images: List<Any>, // Peut contenir Int (ressources locales) ou String (URLs)
    avatarSize: Dp = 32.dp,
    overlap: Dp = 10.dp,
    maxVisible: Int? = null // null = afficher tous, sinon limiter à ce nombre
) {
    // Déterminer si on doit afficher le badge "+N"
    val shouldShowBadge = maxVisible != null && images.size > maxVisible

    // Nombre d'avatars restants (non affichés, incluant celui caché par le badge)
    val remainingCount = if (shouldShowBadge) {
        images.size - maxVisible!!
    } else {
        0
    }

    // Prendre les images à afficher (tous les maxVisible avatars)
    val displayImages = if (maxVisible != null) {
        images.take(maxVisible)
    } else {
        images
    }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Afficher les avatars inversés pour que le suivant chevauche le précédent
            displayImages.reversed().forEachIndexed { index, imageRes ->
                val isLastAvatar = index == displayImages.size-1  && shouldShowBadge

                // Support des images locales et réseau
                when (imageRes) {
                    is Int -> {
                        Image(
                            painter = painterResource(imageRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(avatarSize)
                                .offset(x = (-overlap * index))
                                .zIndex(index.toFloat())
                                .clip(CircleShape)
                                .background(Color.White, CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        )
                    }
                    is String -> {
                        AsyncImage(
                            model = imageRes,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(avatarSize)
                                .offset(x = (-overlap * index))
                                .zIndex(index.toFloat())
                                .clip(CircleShape)
                                .background(Color.White, CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        )
                    }
                }
                // Afficher le badge "+N" SUPERPOSÉ sur le dernier avatar
                if (shouldShowBadge && isLastAvatar) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(avatarSize)
                            .offset(x = (-overlap * (index+1)))
                            .zIndex(index.toFloat())
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f), CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                    ) {
                        Text(
                            text = "+$remainingCount",
                            color = Color.White,
                            fontSize = (avatarSize.value / 2.8f).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }


    }
}
