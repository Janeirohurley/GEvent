package com.janeirohurley.gevent.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.data.model.OrganizerEvent
import com.janeirohurley.gevent.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventManageCard(
    event: OrganizerEvent,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onChangeStatus: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val statusText = when {
        event.isOngoing -> "En cours"
        event.isUpcoming -> "À venir"
        event.isCompleted -> "Terminé"
        event.isCancelled -> "Annulé"
        else -> event.status
    }

    val statusColor = when {
        event.isOngoing || event.isUpcoming -> MaterialTheme.colorScheme.primary
        event.isCompleted -> Color(0xFF4CAF50)
        event.isCancelled -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // En-tête avec image et statut
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = event.imageUrl ?: event.images?.firstOrNull()?.image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.fi_rr_calendar),
                    error = painterResource(R.drawable.fi_rr_calendar)
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
                
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor
                ) {
                    Text(
                        statusText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Text(
                    event.title,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp
                )
            }
            
            // Contenu principal
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Date et lieu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.fi_rr_calendar),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        DateUtils.formatDateFromString(
                            event.date,
                            inputPattern = "yyyy-MM-dd'T'HH:mm:ss",
                            outputPattern = "dd MMM yyyy, HH:mm"
                        ) ?: event.date,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
                
                if (!event.location.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            event.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp

                        )
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Statistiques
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard(
                        title = "Vendus",
                        value = "${event.ticketsSold}",
                        subtitle = "/${event.totalCapacity}",
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    StatCard(
                        title = "Taux",
                        value = "${event.percentageSold.toInt()}%",
                        subtitle = "de vente",
                        color = if (event.percentageSold >= 80) Color(0xFF4CAF50) else MaterialTheme.colorScheme.secondary
                    )
                    
                    StatCard(
                        title = "Prix",
                        value = if (event.isFree) "Gratuit" else "${event.priceWithTva ?: event.price}",
                        subtitle = if (!event.isFree) event.currency else "",
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Barre de progression
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Progression des ventes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            "${event.availableSeats} places restantes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = event.percentageSold / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = when {
                            event.percentageSold >= 90 -> Color(0xFF4CAF50)
                            event.percentageSold >= 70 -> Color(0xFFFF9800)
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Gestion du statut
                if (!event.isCancelled && !event.isDeleted) {
                    Column {
                        Text(
                            "Statut de l'événement",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Bouton À venir
                            FilterChip(
                                selected = event.status == "upcoming",
                                onClick = { if (event.status != "upcoming") onChangeStatus("upcoming") },
                                label = { Text("À venir", fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.fi_rr_calendar),
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                            
                            // Bouton En cours
                            FilterChip(
                                selected = event.status == "ongoing",
                                onClick = { if (event.status != "ongoing") onChangeStatus("ongoing") },
                                label = { Text("En cours", fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.fi_rr_time_past),
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                            
                            // Bouton Terminé
                            FilterChip(
                                selected = event.status == "completed",
                                onClick = { if (event.status != "completed") onChangeStatus("completed") },
                                label = { Text("Terminé", fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.fi_rs_check),
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF4CAF50),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
                
                // Actions rapides
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = false,
                        onClick = onEdit,
                        label = { Text("Modifier", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.fi_rr_edit),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        enabled = !event.isOngoing && !event.isCompleted
                    )
                    
                    if (!event.isCompleted && !event.isCancelled) {
                        FilterChip(
                            selected = false,
                            onClick = { showDeleteDialog = true },
                            label = { Text("Supprimer", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.fi_rr_trash),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            enabled = !event.isOngoing && !event.isCompleted,
                            colors = FilterChipDefaults.filterChipColors(
                                disabledLabelColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                                labelColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                    
                    if (event.isActive) {
                        FilterChip(
                            selected = false,
                            onClick = { showCancelDialog = true },
                            label = { Text("Annuler", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.fi_rr_time_past),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                labelColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }
        }
    }
    
    // Dialogue de confirmation d'annulation
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.fi_rr_shield_exclamation),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("Annuler l'événement ?") },
            text = {
                Column {
                    Text("Cette action va :")
                    Spacer(Modifier.height(8.dp))
                    Text("• Annuler tous les billets vendus (${event.ticketsSold})")
                    Text("• Rembourser automatiquement tous les participants")
                    Text("• Changer le statut de l'événement en 'Annulé'")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Montant total à rembourser : ${event.ticketsSold * (event.priceWithTva?.toDoubleOrNull() ?: event.price?.toDoubleOrNull() ?: 0.0)} ${event.currency}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        onCancel()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Confirmer l'annulation")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
    
    // Dialogue de confirmation de suppression
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.fi_rr_trash),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("Supprimer l'événement ?") },
            text = {
                Column {
                    if (event.ticketsSold > 0) {
                        Text(
                            "⚠️ Impossible de supprimer cet événement car ${event.ticketsSold} billet(s) ont été vendu(s).",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Vous devez d'abord annuler l'événement pour rembourser les participants.")
                    } else {
                        Text("Êtes-vous sûr de vouloir supprimer cet événement ?")
                        Spacer(Modifier.height(8.dp))
                        Text("L'événement sera marqué comme supprimé mais restera dans la base de données.")
                    }
                }
            },
            confirmButton = {
                if (event.ticketsSold == 0) {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            onDelete()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Supprimer")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(if (event.ticketsSold > 0) "Fermer" else "Annuler")
                }
            }
        )
    }
}