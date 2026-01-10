package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.ui.components.EventStatusTabs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEventsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("Événements", "Scanner QR", "Créer")
    var selectedTab by remember { mutableStateOf("Événements") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gestion d'Événements",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // Tabs
            EventStatusTabs(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(Modifier.height(20.dp))

            // Contenu selon le tab sélectionné
            when (selectedTab) {
                "Événements" -> MyEventsContent()
                "Scanner QR" -> ScanQRContent()
                "Créer" -> CreateEventContent()
            }
        }
    }
}

@Composable
private fun MyEventsContent() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Vos événements organisés",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        // TODO: Remplacer par la liste réelle des événements de l'organisateur
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(3) { index ->
                EventManageCard(
                    eventTitle = "Événement ${index + 1}",
                    eventDate = "15 Jan 2026",
                    ticketsSold = 45,
                    totalTickets = 100,
                    status = if (index == 0) "En cours" else "À venir"
                )
            }
        }
    }
}

@Composable
private fun EventManageCard(
    eventTitle: String,
    eventDate: String,
    ticketsSold: Int,
    totalTickets: Int,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        eventTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.fi_rr_calendar),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            eventDate,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (status) {
                        "En cours" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (status) {
                            "En cours" -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Statistiques des tickets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Tickets vendus",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        "$ticketsSold / $totalTickets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { /* TODO: Voir détails */ },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.fi_rr_eye),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Détails")
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanQRContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.fi_rr_qrcode),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Scanner un QR Code",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Scannez le QR code d'un ticket\npour valider l'entrée",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { /* TODO: Ouvrir scanner QR */ },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.fi_rr_camera),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Ouvrir le scanner", style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun CreateEventContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.fi_rr_plus),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Créer un Événement",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Organisez votre propre événement\net vendez des tickets",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { /* TODO: Ouvrir formulaire de création */ },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.fi_rr_plus),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Créer un événement", style = MaterialTheme.typography.titleSmall)
        }
    }
}
