package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.ui.components.RecommendationCard
import com.janeirohurley.gevent.ui.components.RecommendationCardSkeleton
import com.janeirohurley.gevent.ui.components.EmptyEventsState
import com.janeirohurley.gevent.viewmodel.EventViewModel
import com.janeirohurley.gevent.utils.DataMapper.toUiModelList
import com.janeirohurley.gevent.utils.DateFormatter


@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: EventViewModel = viewModel()
) {
    // États du ViewModel
    val favoriteEvents by viewModel.favoriteEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Charger les favoris au démarrage
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    // Convertir les événements API en EventUiModel
    val favoritesUi = remember(favoriteEvents) {
        try {
            favoriteEvents.toUiModelList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // En-tête
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Favoris",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Gestion des états de chargement et d'erreur
        when {
            // État de chargement
            isLoading && favoritesUi.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(5) {
                        RecommendationCardSkeleton()
                    }
                }
            }

            // État d'erreur ou liste vide
            !isLoading && favoritesUi.isEmpty() -> {
                EmptyEventsState(
                    message = if (error != null) {
                        "Impossible de charger vos favoris\n$error"
                    } else {
                        "Aucun événement favori\nAjoutez des événements à vos favoris pour les retrouver ici"
                    },
                    onRefresh = if (error != null) {
                        { viewModel.loadFavorites() }
                    } else null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Liste des favoris
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        favoritesUi.forEach { event ->
                            RecommendationCard(
                                title = event.title,
                                date = DateFormatter.formatDate(event.date),
                                location = event.location ?: "Burundi",
                                imageRes = event.imageRes,
                                isFree = event.isFree,
                                price = event.price ?: "Gratuit",
                                onClick = {
                                    navController.navigate("event_details/${event.id}")
                                }
                            )
                        }

                        // Espacement en bas
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}