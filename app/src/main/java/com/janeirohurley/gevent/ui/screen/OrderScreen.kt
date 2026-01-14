package com.janeirohurley.gevent.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.viewmodel.EventUiModel
import com.janeirohurley.gevent.viewmodel.TicketViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    event: EventUiModel,
    onViewTicket: ((String) -> Unit)? = null,
    onGoHome: (() -> Unit)? = null,
    viewModel: TicketViewModel = viewModel()
){
    var ticketCount by remember { mutableStateOf(1) }
    val pricePerTicket = event.price?.replace("€","")?.replace("Fbu","")?.trim()?.toFloatOrNull() ?: 0f
    val subtotal = pricePerTicket * ticketCount
    val tax = subtotal * 0.10f
    val total = subtotal + tax
    var selectedPayment by remember { mutableStateOf("Gasape Cash") }

    // Observer les états du ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val bookingSuccess by viewModel.bookingSuccess.collectAsState()

    // État pour afficher le dialogue de succès
    var showSuccessDialog by remember { mutableStateOf(false) }
    var bookedTicketId by remember { mutableStateOf<String?>(null) }

    // Observer le succès de la réservation
    LaunchedEffect(bookingSuccess) {
        bookingSuccess?.let { booking ->
            try {
                Log.d("ORDER_DEBUG", "========== BOOKING SUCCESS ==========")
                Log.d("ORDER_DEBUG", "Order ID: ${booking.id}")
                Log.d("ORDER_DEBUG", "Order Number: ${booking.orderNumber}")
                Log.d("ORDER_DEBUG", "Number of tickets: ${booking.tickets.size}")

                // Récupérer le premier ticket (utilise le helper ou directement la liste)
                val firstTicket = booking.ticket ?: booking.tickets.firstOrNull()

                if (firstTicket != null) {
                    bookedTicketId = firstTicket.id
                    Log.d("ORDER_DEBUG", "First ticket ID: ${firstTicket.id}")
                    Log.d("ORDER_DEBUG", "Ticket code: ${firstTicket.code}")
                    showSuccessDialog = true
                } else {
                    Log.e("ORDER_ERROR", "No tickets in booking response!")
                    Log.e("ORDER_ERROR", "Booking response: $booking")
                    // Afficher quand même le dialogue mais sans ID de ticket
                    showSuccessDialog = true
                }
                Log.d("ORDER_DEBUG", "====================================")
                viewModel.clearBookingSuccess()
            } catch (e: Exception) {
                Log.e("ORDER_ERROR", "Error processing booking success", e)
                // Afficher le dialogue même en cas d'erreur
                showSuccessDialog = true
                viewModel.clearBookingSuccess()
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Réservation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.fi_rr_angle_left),
                            contentDescription = "Retour",
                            Modifier.size(14.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    // DEBUG: Afficher les données avant l'envoi
                    val paymentMethodFormatted = selectedPayment.lowercase().replace(" ", "_")
                    Log.d("ORDER_DEBUG", "========== ORDER SCREEN DEBUG ==========")
                    Log.d("ORDER_DEBUG", "Event ID: ${event.id}")
                    Log.d("ORDER_DEBUG", "Event Title: ${event.title}")
                    Log.d("ORDER_DEBUG", "Ticket Count: $ticketCount")
                    Log.d("ORDER_DEBUG", "Selected Payment: $selectedPayment")
                    Log.d("ORDER_DEBUG", "Payment Method Formatted: $paymentMethodFormatted")
                    Log.d("ORDER_DEBUG", "Price per ticket: $pricePerTicket")
                    Log.d("ORDER_DEBUG", "Total: $total")
                    Log.d("ORDER_DEBUG", "=========================================")

                    // Appeler le ViewModel pour réserver le ticket
                    viewModel.bookTicket(
                        eventId = event.id,
                        quantity = ticketCount,
                        paymentMethod = paymentMethodFormatted,
                        seatNumber = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(45.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = ticketCount > 0 && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Passer la commande")
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ){paddingValues ->
        // Dialogue de succès
        if (showSuccessDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.fi_rs_shopping_cart_check),
                            contentDescription = "Succès",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Félicitations !", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Votre commande a été effectuée avec succès !", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(6.dp))
                        Text("Vous faites bien de participer à cet événement, bravo pour votre engagement. 🎉", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                    }
                },
                confirmButton = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Bouton "Voir le ticket" seulement si on a un ID de ticket
                        if (bookedTicketId != null) {
                            Button(
                                onClick = {
                                    showSuccessDialog = false
                                    bookedTicketId?.let { ticketId ->
                                        Log.d("ORDER_DEBUG", "Navigating to ticket: $ticketId")
                                        onViewTicket?.invoke(ticketId)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Voir le ticket")
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                onGoHome?.invoke()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Accueil")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Dialogue d'erreur
        if (error != null) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.fi_rr_cross),
                            contentDescription = "Erreur",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Erreur de réservation",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val errorMessage = when {
                            error!!.contains("Solde insuffisant", ignoreCase = true) -> {
                                "Votre solde est insuffisant pour effectuer cette commande. Veuillez recharger votre compte Gasape Cash."
                            }
                            error!!.contains("HTTP 400", ignoreCase = true) -> {
                                "Données invalides. Vérifiez les informations de votre commande."
                            }
                            error!!.contains("HTTP 401", ignoreCase = true) -> {
                                "Session expirée. Veuillez vous reconnecter."
                            }
                            error!!.contains("HTTP 404", ignoreCase = true) -> {
                                "Événement introuvable. Il a peut-être été supprimé."
                            }
                            error!!.contains("réseau", ignoreCase = true) || error!!.contains("connexion", ignoreCase = true) -> {
                                "Problème de connexion. Vérifiez votre connexion internet."
                            }
                            else -> error
                        }
                        
                        Text(
                            text = errorMessage ?: "Une erreur est survenue",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        if (error!!.contains("Solde insuffisant", ignoreCase = true)) {
                            Spacer(Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.fi_rr_wallet),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Rechargez votre compte pour continuer",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.clearError() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Compris")
                    }
                },
                shape = RoundedCornerShape(14.dp)
            )
        }
        Column( modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)){
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Image à gauche avec coins arrondis - Support des images locales et réseau
                    when (event.imageRes) {
                        is Int -> {
                            Image(
                                painter = painterResource(event.imageRes),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                        is String -> {
                            AsyncImage(
                                model = event.imageRes,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }

                    // Contenu à droite
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                            // Titre
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                fontSize = 13.sp
                            )

                        // Lieu
                        if (event.location != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.fi_rr_marker),
                                    contentDescription = "Lieu",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = event.location,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    fontSize = 12.sp

                                )
                            }
                        }

                        // Date/heure
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.fi_rr_calendar) ,
                                contentDescription = "Date",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = event.date,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        }


                    }
                }


            }


            Text("Résumé de la commande",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            // Sélecteur de quantité
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Nombre de tickets", style = MaterialTheme.typography.bodyMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (ticketCount > 1) ticketCount-- }) {
                        Icon(painter = painterResource(R.drawable.fi_rr_minus), contentDescription = "Diminuer",modifier = Modifier.size(15.dp))
                    }
                    Text(ticketCount.toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { ticketCount++ }) {
                        Icon(painter = painterResource(R.drawable.fi_rr_plus), contentDescription = "Augmenter",modifier = Modifier.size(15.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Prix unitaire
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Prix unitaire", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Text("%.2f Fbu".format(pricePerTicket), style = MaterialTheme.typography.bodyMedium)
            }
            // Sous-total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sous-total", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Text("%.2f Fbu".format(subtotal), style = MaterialTheme.typography.bodyMedium)
            }
            // Taxe
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Taxe (10%)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Text("%.2f Fbu".format(tax), style = MaterialTheme.typography.bodyMedium)
            }
            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("%.2f Fbu".format(total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Méthode de paiement
            Text("Méthode de paiement",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = true,           // toujours sélectionné
                    onClick = null             // désactivé → non cliquable
                )

                Icon(
                    painter = painterResource(R.drawable.fi_rr_wallet),
                    contentDescription = "Gasape Cash",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Gasape Cash",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }


        }
    }
}