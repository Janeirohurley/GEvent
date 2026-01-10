package com.janeirohurley.gevent.ui.screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import com.janeirohurley.gevent.ui.components.AppBottomDrawer
import com.janeirohurley.gevent.ui.components.EmptyEventsState
import com.janeirohurley.gevent.ui.components.EventStatusTabs
import com.janeirohurley.gevent.ui.components.RecommendationCard
import com.janeirohurley.gevent.ui.components.StarRating
import com.janeirohurley.gevent.utils.DataMapper.toTicketUiModelList
import com.janeirohurley.gevent.utils.truncateByWords
import com.janeirohurley.gevent.viewmodel.TicketUiModel
import com.janeirohurley.gevent.viewmodel.TicketViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: TicketViewModel = viewModel()
) {
    // Charger les tickets au démarrage
    LaunchedEffect(Unit) {
        viewModel.loadMyTickets()
    }

    val tabs = listOf("A venir", "Terminé", "Annulé")
    var selectedTab by remember { mutableStateOf("A venir") }

    var showReviewSheet by remember { mutableStateOf(false) }
    var showCancelSheet by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(0) }
    var reviewComment by remember { mutableStateOf("") }
    var cancelReason by remember { mutableStateOf("") }
    var cancelComment by remember { mutableStateOf("") }
    var selectedTicket by remember { mutableStateOf<TicketUiModel?>(null) }

    // État pour le Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Récupérer les états du ViewModel
    val tickets by viewModel.tickets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Observer les erreurs et afficher un Snackbar
    LaunchedEffect(error) {
        error?.let { errorMessage ->
            scope.launch {
                snackbarHostState.showSnackbar(errorMessage)
                viewModel.clearError()
            }
        }
    }

    // Convertir en UI models avec gestion d'erreur
    val ticketsUi = try {
        Log.d("TICKET_SCREEN", "Converting ${tickets.size} tickets to UI models")
        tickets.toTicketUiModelList()
    } catch (e: Exception) {
        Log.e("TICKET_SCREEN", "Error converting tickets to UI models", e)
        scope.launch {
            snackbarHostState.showSnackbar("Erreur de conversion des tickets: ${e.message}")
        }
        emptyList()
    }

    // Filtrer les tickets selon le tab sélectionné
    val filteredTickets = when (selectedTab) {
        "A venir" -> ticketsUi.filter { it.status == "active" || it.status == "confirmed" }
        "Terminé" -> ticketsUi.filter { it.status == "used" }
        "Annulé" -> ticketsUi.filter { it.status == "cancelled" }
        else -> emptyList()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Tous les tickets",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        EventStatusTabs(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
        )
        Spacer(Modifier.height(20.dp))

        // État de chargement
        if (isLoading && ticketsUi.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        // État d'erreur
        else if (error != null && ticketsUi.isEmpty()) {
            EmptyEventsState(
                message = error ?: "Une erreur est survenue",
                onRefresh = { viewModel.loadMyTickets() }
            )
        }
        // Liste vide
        else if (filteredTickets.isEmpty()) {
            EmptyEventsState(
                message = when (selectedTab) {
                    "A venir" -> "Aucun ticket à venir"
                    "Terminé" -> "Aucun ticket terminé"
                    "Annulé" -> "Aucun ticket annulé"
                    else -> "Aucun ticket"
                },
                onRefresh = null
            )
        }
        // Liste des tickets
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredTickets,
                    key = { it.id }
                ) { ticket ->
                    RecommendationCard(
                        title = ticket.eventTitle,
                        date = ticket.eventDate,
                        location = ticket.eventLocation,
                        imageRes = ticket.eventImageRes,
                        isFree = false,
                        price = when (selectedTab) {
                            "A venir" -> ticket.price
                            "Terminé" -> "Terminé"
                            "Annulé" -> "Annulé"
                            else -> ticket.price
                        },
                        onClick = {
                            // Navigation vers les détails de l'événement
                            navController.navigate("event_details/${ticket.id}")
                        },
                        actions = {
                            when (selectedTab) {
                                "A venir" -> {
                                    // Actions pour tickets à venir
                                    OutlinedButton(
                                        onClick = {
                                            selectedTicket = ticket
                                            showCancelSheet = true
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.4f
                                            )
                                        ),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    ) {
                                        Text("Annuler")
                                    }

                                    Button(
                                        onClick = {
                                            navController.navigate("view_ticket/${ticket.id}")
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary.copy(
                                                alpha = 0.2f
                                            ),
                                            contentColor = MaterialTheme.colorScheme.primary
                                        ),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text("Voir le Ticket")
                                    }
                                }

                                "Terminé" -> {
                                    // Actions pour tickets terminés
                                    OutlinedButton(
                                        onClick = {
                                            navController.navigate("view_ticket/${ticket.id}")
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.7f
                                            )
                                        ),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    ) {
                                        Text("Détails")
                                    }

                                    Button(
                                        onClick = {
                                            selectedTicket = ticket
                                            showReviewSheet = true
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(4.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary.copy(
                                                alpha = 0.2f
                                            ),
                                            contentColor = MaterialTheme.colorScheme.primary
                                        ),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text("Évaluez-nous")
                                    }
                                }

                                "Annulé" -> {
                                    // Actions pour tickets annulés
                                    OutlinedButton(
                                        onClick = {
                                            navController.navigate("view_ticket/${ticket.id}")
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onSurface.copy(
                                                alpha = 0.7f
                                            )
                                        ),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    ) {
                                        Text("Détails")
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
        }

        // Snackbar pour les messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    // Bottom sheet pour l'évaluation
    AppBottomDrawer(
        isVisible = showReviewSheet,
        onDismiss = {
            showReviewSheet = false
            rating = 0
            reviewComment = ""
            selectedTicket = null
        }
    ) {
        Text(
            "Évaluez-nous",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        // Afficher le ticket sélectionné
        selectedTicket?.let { ticket ->
            RecommendationCard(
                title = ticket.eventTitle,
                date = ticket.eventDate,
                location =  truncateByWords(ticket.eventLocation, maxWords = 1),
                imageRes = ticket.eventImageRes,
                isFree = false,
                price = "Terminé",
                onClick = {},
                modifier = Modifier.padding(horizontal = 7.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Votre note",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(8.dp))

            StarRating(
                rating = rating,
                onRatingSelected = { rating = it },
                size = 28.dp
            )
        }

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = reviewComment,
            onValueChange = { reviewComment = it },
            placeholder = {
                Text("Laissez un commentaire (optionnel)")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            maxLines = 5
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    showReviewSheet = false
                    rating = 0
                    reviewComment = ""
                    selectedTicket = null
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Annuler")
            }

            Button(
                onClick = {
                    // TODO: Envoyer l'évaluation au backend via API
                    // Exemple: reviewViewModel.submitReview(selectedTicket?.id, rating, reviewComment)
                    println("Rating: $rating | Comment: $reviewComment | Ticket: ${selectedTicket?.id}")

                    // Afficher un message de confirmation
                    scope.launch {
                        snackbarHostState.showSnackbar("Merci pour votre évaluation!")
                    }

                    showReviewSheet = false
                    rating = 0
                    reviewComment = ""
                    selectedTicket = null
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                enabled = rating > 0
            ) {
                Text("Continuer")
            }
        }
    }

    // Bottom sheet pour l'annulation de ticket
    AppBottomDrawer(
        isVisible = showCancelSheet,
        onDismiss = {
            showCancelSheet = false
            cancelReason = ""
            cancelComment = ""
            selectedTicket = null
        }
    ) {
        Text(
            "Annuler le ticket",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        // Afficher le ticket sélectionné
        selectedTicket?.let { ticket ->
            RecommendationCard(
                title = ticket.eventTitle,
                date = ticket.eventDate,
                location = ticket.eventLocation,
                imageRes = ticket.eventImageRes,
                isFree = false,
                price = ticket.price,
                onClick = {},
                modifier = Modifier.padding(horizontal = 7.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "Raison de l'annulation",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = cancelReason,
            onValueChange = { cancelReason = it },
            placeholder = {
                Text("Ex: Changement de plans, problème personnel...")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = cancelComment,
            onValueChange = { cancelComment = it },
            placeholder = {
                Text("Commentaire additionnel (optionnel)")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(12.dp),
            maxLines = 4
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    showCancelSheet = false
                    cancelReason = ""
                    cancelComment = ""
                    selectedTicket = null
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Retour")
            }

            Button(
                onClick = {
                    selectedTicket?.let { ticket ->
                        // Appeler le ViewModel pour annuler le ticket
                        viewModel.cancelTicket(
                            ticketId = ticket.id,
                            reason = cancelReason,
                            comment = cancelComment.ifBlank { null }
                        )
                        // Afficher un message de confirmation
                        scope.launch {
                            snackbarHostState.showSnackbar("Ticket annulé avec succès")
                        }
                    }
                    showCancelSheet = false
                    cancelReason = ""
                    cancelComment = ""
                    selectedTicket = null
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                enabled = cancelReason.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Confirmer l'annulation")
            }
        }
    }
}
