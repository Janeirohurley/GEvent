package com.janeirohurley.gevent.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.janeirohurley.gevent.data.model.OrganizerEvent

@Composable
fun MyEventsContent(
    events: List<OrganizerEvent>,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onCancelEvent: (String) -> Unit,
    onCompleteEvent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            "Vos événements organisés",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        // État de chargement
        if (isLoading && events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        // État d'erreur
        else if (error != null && events.isEmpty()) {
            EmptyEventsState(
                message = error,
                onRefresh = onRefresh
            )
        }
        // Liste vide
        else if (events.isEmpty()) {
            EmptyEventsState(
                message = "Aucun événement organisé",
                onRefresh = null
            )
        }
        // Liste des événements
        else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = events,
                    key = { it.id }
                ) { event ->
                    EventManageCard(
                        event = event,
                        onCancel = { onCancelEvent(event.id) },
                        onComplete = { onCompleteEvent(event.id) },
                        onEdit = { /* TODO: Navigation vers édition */ },
                        onDelete = { /* TODO: Suppression avec confirmation */ }
                    )
                }
            }
        }
    }
}