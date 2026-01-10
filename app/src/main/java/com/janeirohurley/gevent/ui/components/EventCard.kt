package com.janeirohurley.gevent.ui.components


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.janeirohurley.gevent.R



@Composable
fun EventCard(
    title: String,
    date: String,
    imageRes: Any, // Peut être Int (ressource locale) ou String (URL)
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onClick: () -> Unit,
    onFavoriteClick: (() -> Unit)? = null,
    creatorImageRes: Any, // Peut être Int (ressource locale) ou String (URL)
    creatorName: String,
    joinedAvatars: List<Any>, // Peut contenir Int ou String
    isFree: Boolean = true,
    price: String? = null, // Prix si payant (ex: "5000 BIF")
    categorie_name: String
) {
    // Optimisation: Créer l'interactionSource une seule fois
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Optimisation: Animation plus rapide et légère
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = tween(durationMillis = 100), // Plus rapide
        label = "cardAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // 🖼 Image
            Box {
                // Support des images locales et réseau
                when (imageRes) {
                    is Int -> {
                        Image(
                            painter = painterResource(imageRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                    is String -> {
                        AsyncImage(
                            model = imageRes,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }

                // 🏷️ Category chip (top-left)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Chip(
                        text = categorie_name,
                        customColor = MaterialTheme.colorScheme.background.copy(0.2f),
                        customContentColor = MaterialTheme.colorScheme.surface
                    )
                }


                // ❤️ Favorite icon (optionnel)
                if (onFavoriteClick != null) {
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                if (isFavorite)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else
                                    MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp)
                            )

                            .size(35.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isFavorite)
                                    R.drawable.fi_rr_heart
                                else
                                    R.drawable.fi_rr_heart
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = if (isFavorite)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface

                        )

                    }
                }


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)

                ) {
                    // Support des images locales et réseau pour le créateur
                    when (creatorImageRes) {
                        is Int -> {
                            Image(
                                painter = painterResource(creatorImageRes),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(40.dp)
                                    .shadow(1.dp, CircleShape)
                            )
                        }
                        is String -> {
                            AsyncImage(
                                model = creatorImageRes,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(40.dp)
                                    .shadow(1.dp, CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = creatorName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFFFFFFFF),
                    )
                }




            }

            // 📄 Content
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // N'afficher les avatars que s'ils existent
                    if (joinedAvatars.isNotEmpty()) {
                        AvatarGroup(
                            images = joinedAvatars,
                            maxVisible = 5
                        )
                    } else {
                        // Espace vide pour maintenir l'alignement
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    // Chip prix
                    Chip(
                        text = if (isFree) "Gratuit" else price ?: "Payant",
                        customColor = if (isFree)
                            Color(0xFF4CAF50).copy(alpha = 0.9f)
                        else
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        customContentColor = MaterialTheme.colorScheme.background
                    )

                }
            }
        }
    }
}
