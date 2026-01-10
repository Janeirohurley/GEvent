
package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.ui.components.AvatarGroup
import com.janeirohurley.gevent.viewmodel.EventUiModel

@Composable
fun EventDetailsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    event: EventUiModel
) {
    var cardHeightPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    Box(modifier = modifier.fillMaxSize()) {
        // Image de couverture edge-to-edge sous la status bar - Support images locales et réseau
        when (event.imageRes) {
            is Int -> {
                Image(
                    painter = painterResource(event.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .align(Alignment.TopCenter)
                )
            }
            is String -> {
                AsyncImage(
                    model = event.imageRes,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .align(Alignment.TopCenter)
                )
            }
        }

        // Card flottante
        Card(
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = 240.dp)
                .onGloballyPositioned { coordinates ->
                    cardHeightPx = coordinates.size.height
                }
                .shadow(0.dp, RoundedCornerShape(15.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Titre & prix
                Row(
                    modifier = Modifier.fillMaxWidth(),

                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Column {
                        // Lieu
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.fi_rr_marker),
                                contentDescription = "Lieu",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = event.location ?: "Lieu non spécifié",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(Modifier.height(8.dp))

                        // Date/heure
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.fi_rr_calendar) ,
                                contentDescription = "Date",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = event.date,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Avatars participants
                          }

                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    // N'afficher les avatars que s'ils existent
                    if (event.joinedAvatars.isNotEmpty()) {
                        AvatarGroup(
                            images = event.joinedAvatars,
                            avatarSize = 30.dp,
                            overlap = 14.dp,
                            maxVisible = 5
                        )
                    } else {
                        // Espace vide pour maintenir l'alignement
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (event.isFree) {
                        Text(
                            text = "Gratuit",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = event.price ?: "",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            }
        }

        // LazyColumn scrollable sous la card flottante
        val cardHeightDp = with(density) { cardHeightPx.toDp() }
        // Utilisation d'une Box pour superposer la LazyColumn et le bouton
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(top = 240.dp + cardHeightDp, bottom = 80.dp) // Laisser de la place pour le bouton
            ) {
                item {
                    // Section About
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "À propos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = event.description ?: "Aucune description disponible pour cet événement.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }
                }
                item {
                    // Section Organizers and Attendees
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar organisateur - Support images locales et réseau
                            when (event.creatorImageRes) {
                                is Int -> {
                                    Image(
                                        painter = painterResource(event.creatorImageRes),
                                        contentDescription = "Organisateur",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                }
                                is String -> {
                                    AsyncImage(
                                        model = event.creatorImageRes,
                                        contentDescription = "Organisateur",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = event.creatorName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { /* TODO: action chat */ }) {
                                Icon(
                                    painter = painterResource(R.drawable.fi_rr_comment) ,
                                    contentDescription = "Message",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                // ... Ajoutez d'autres sections si besoin ...
            }
            // Bouton fixé en bas
            Button(
                onClick = { navController.navigate("order/${event.id}") },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Acheter un ticket", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

