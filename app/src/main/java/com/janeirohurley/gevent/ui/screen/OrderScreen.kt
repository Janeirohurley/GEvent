package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.viewmodel.EventUiModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    event: EventUiModel,
    onViewTicket: (() -> Unit)? = null,
    onGoHome: (() -> Unit)? = null
){
    var ticketCount by remember { mutableStateOf(1) }
    val pricePerTicket = event.price?.replace("€","")?.replace("Fbu","")?.trim()?.toFloatOrNull() ?: 0f
    val subtotal = pricePerTicket * ticketCount
    val tax = subtotal * 0.10f
    val total = subtotal + tax
    var selectedPayment by remember { mutableStateOf("Gasape Cash") }
    var showSuccess by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Réservation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.fi_rr_angle_left),
                            contentDescription = "Retour",
                            Modifier.size(20.dp)
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
                    // Simulation succès/erreur
                    if (ticketCount > 0) {
                        showSuccess = true
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = ticketCount > 0
            ) {
                Text("Passer la commande")
            }
        },
        modifier = modifier.fillMaxSize()
    ){paddingValues ->
        // Popups de succès ou d'erreur
        if (showSuccess) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showSuccess = false },
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
                        Button(
                            onClick = {
                                showSuccess = false
                                onViewTicket?.invoke()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Voir le ticket")
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                showSuccess = false
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
        if (showError) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showError = false },
                title = { Text("Erreur", fontWeight = FontWeight.Bold) },
                text = { Text("Une erreur est survenue lors de la commande. Veuillez réessayer.") },
                confirmButton = {
                    Button(onClick = { showError = false }) { Text("OK") }
                }
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
                    // Image à gauche avec coins arrondis
                    Image(
                        painter = painterResource(event.imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    // Contenu à droite
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                            // Titre
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        Spacer(modifier = Modifier.height(4.dp))
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
                                text = "Grand Palais, Paris", // À remplacer par event.location si dispo
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


                    }
                }


            }
            Spacer(modifier = Modifier.height(12.dp))

            Text("Résumé de la commande", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            // Sélecteur de quantité
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Nombre de tickets", style = MaterialTheme.typography.bodyMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (ticketCount > 1) ticketCount-- }) {
                        Icon(painter = painterResource(R.drawable.fi_rr_minus), contentDescription = "Diminuer")
                    }
                    Text(ticketCount.toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { ticketCount++ }) {
                        Icon(painter = painterResource(R.drawable.fi_rr_plus), contentDescription = "Augmenter",modifier = Modifier.size(18.dp))
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
            Text("Méthode de paiement", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gasape Cash pré-sélectionné
                Icon(
                    painter = painterResource(R.drawable.fi_rr_wallet),
                    contentDescription = "Gasape Cash",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Gasape Cash", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }

        }
    }
}