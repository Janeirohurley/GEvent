
package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.ui.components.AvatarGroup
import com.janeirohurley.gevent.ui.components.shareText
import com.janeirohurley.gevent.viewmodel.EventUiModel

@Composable
fun EventDetailsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    event: EventUiModel
) {

    val context = LocalContext.current
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Image de couverture
            item {
                when (event.imageRes) {
                    is Int -> {
                        Image(
                            painter = painterResource(event.imageRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
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
                        )
                    }
                }
            }

            // Card avec les informations principales
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = event.title.uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(R.drawable.fi_rr_marker),
                                        contentDescription = "Lieu",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = event.location ?: "Lieu non spécifié",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(R.drawable.fi_rr_calendar),
                                        contentDescription = "Date",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = event.date,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp
                                    )
                                }


                                Spacer(Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(R.drawable.git_pull_request),
                                        contentDescription = "Date",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = event.categorieName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (event.joinedAvatars.isNotEmpty()) {
                                AvatarGroup(
                                    images = event.joinedAvatars,
                                    avatarSize = 30.dp,
                                    overlap = 14.dp,
                                    maxVisible = 5
                                )
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            if (event.isFree) {
                                Text(
                                    text = "Gratuit",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            } else {
                                Text(
                                    text = (event.price  + event.currency),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                val shareText = "🎉 ${event.title}\n\n" +
                                        "📅 ${event.date}\n" +
                                        "📍 ${event.location ?: "Lieu non spécifié"}\n" +
                                        (if (event.isFree) "🎁 Gratuit" else "💵 ${event.price} ${event.currency}") + "\n\n" +
                                        (event.description ?: "")
                                shareText(context, shareText)
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.fi_rr_paper_plane),
                                contentDescription = "Partager",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Partager",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable(
                                enabled = event.creatorPhone != null
                            ) {
                                event.creatorPhone?.let { phone ->
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                        data = android.net.Uri.parse("https://wa.me/$phone")
                                    }
                                    context.startActivity(intent)
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.fi_rr_comment),
                                contentDescription = "Contact",
                                tint = if (event.creatorPhone != null) MaterialTheme.colorScheme.primary 
                                       else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Contact",
                                fontSize = 10.sp,
                                color = if (event.creatorPhone != null) MaterialTheme.colorScheme.onSurfaceVariant
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                try {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_INSERT).apply {
                                        data = android.provider.CalendarContract.Events.CONTENT_URI
                                        putExtra(android.provider.CalendarContract.Events.TITLE, event.title)
                                        putExtra(android.provider.CalendarContract.Events.DESCRIPTION, event.description)
                                        putExtra(android.provider.CalendarContract.Events.EVENT_LOCATION, event.location)
                                        
                                        // Parser la date
                                        try {
                                            val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.FRENCH)
                                            val date = dateFormat.parse(event.date)
                                            date?.let {
                                                putExtra(android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME, it.time)
                                                putExtra(android.provider.CalendarContract.EXTRA_EVENT_END_TIME, it.time + 3600000) // +1h
                                            }
                                        } catch (e: Exception) {
                                            // Si le parsing échoue, utiliser la date actuelle
                                        }
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Gérer l'erreur si l'app calendrier n'est pas disponible
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.fi_rr_calendar),
                                contentDescription = "Calendrier",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Calendrier",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

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
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = event.description ?: "Aucune description disponible pour cet événement.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                }
                item {
                    // Section Organizers
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Organisateur",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(6.dp))
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
                                fontSize = 14.sp,
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
            // Bouton en bas de la liste
            item {
                Button(
                    onClick = { navController.navigate("order/${event.id}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Acheter un ticket", style = MaterialTheme.typography.titleMedium, fontSize = 13.sp)
                }
            }
        }
    }
}

