package com.janeirohurley.gevent.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.ui.components.EmptyEventsState
import com.janeirohurley.gevent.ui.components.EmptySearchState
import com.janeirohurley.gevent.ui.components.EventCardSkeleton
import com.janeirohurley.gevent.ui.components.Filter
import com.janeirohurley.gevent.ui.components.MapEventList
import com.janeirohurley.gevent.ui.components.MapEventListOrientation
import com.janeirohurley.gevent.ui.components.RecommendationCard
import com.janeirohurley.gevent.ui.components.RecommendationCardSkeleton
import com.janeirohurley.gevent.ui.components.SearchBar
import com.janeirohurley.gevent.ui.components.UpcomingEventCard
import com.janeirohurley.gevent.ui.components.UpcomingEventCardSkeleton
import com.janeirohurley.gevent.ui.components.shimmerEffect
import com.janeirohurley.gevent.utils.DataMapper.toUiModelList
import com.janeirohurley.gevent.utils.DateFormatter
import com.janeirohurley.gevent.viewmodel.EventViewModel
import com.janeirohurley.gevent.viewmodel.UserViewModel

// Fonction pour obtenir la couleur personnalisée de chaque filtre
fun getFilterColor(filter: String): Color {
    return when (filter) {
        "Music" -> Color(0xFFE91E63)        // Rose/Pink
        "Education" -> Color(0xFF2196F3)    // Bleu
        "Film&Cinema" -> Color(0xFF9C27B0)  // Violet
        "Sport" -> Color(0xFF4CAF50)        // Vert
        "Jeux" -> Color(0xFFFF9800)         // Orange
        "Voyage" -> Color(0xFF00BCD4)       // Cyan
        "Concert" -> Color(0xFFF44336)      // Rouge
        else -> Color(0xFFB118C2)           // Couleur par défaut
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: EventViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {

    // -------------------------
    // États avec préservation
    // -------------------------
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val categories by viewModel.categories.collectAsState()
    val filters = remember(categories) { categories.map { it.name } }
    var selectedFilter by rememberSaveable { mutableStateOf<String?>(null) }
    var showFilterMenu by rememberSaveable { mutableStateOf(false) }

    // États de scroll préservés
    val mainScrollState = rememberScrollState()
    val filterScrollState = rememberScrollState()
    val upcomingEventsScrollState = rememberLazyListState()
    
    // Pull-to-refresh state
    val pullToRefreshState = rememberPullToRefreshState()

    // États du ViewModel
    val apiEvents by viewModel.events.collectAsState()
    val popularEvents by viewModel.popularEvents.collectAsState()
    val upcomingEvents by viewModel.upcomingEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // États de l'utilisateur
    val currentUser by userViewModel.currentUser.collectAsState()

    // Charger les données au démarrage
    LaunchedEffect(Unit) {
        viewModel.loadEvents()
        viewModel.loadPopularEvents()
        viewModel.loadUpcomingEvents()
        viewModel.loadCategories()
        userViewModel.loadProfile()
    }

    // Charger les données en fonction du filtre et de la recherche
    LaunchedEffect(selectedFilter, searchQuery) {
        // Debounce de 500ms pour la recherche
        kotlinx.coroutines.delay(500)
        viewModel.loadEvents(
            category = selectedFilter,
            search = searchQuery.ifEmpty { null }
        )
        viewModel.loadPopularEvents(
            category = selectedFilter,
            search = searchQuery.ifEmpty { null }
        )
        viewModel.loadUpcomingEvents(
            category = selectedFilter,
            search = searchQuery.ifEmpty { null }
        )
    }

    // Convertir les événements API en EventUiModel de manière sécurisée
    val events = remember(apiEvents) {
        try {
            apiEvents.toUiModelList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    val popularEventsUi = remember(popularEvents) {
        try {
            popularEvents.toUiModelList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    val upcomingEventsUi = remember(upcomingEvents) {
        try {
            upcomingEvents.toUiModelList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Événements filtrés (déjà filtrés par l'API, mais on peut ajouter un filtre local)
    val filteredEvents = events

    // Afficher une erreur réseau globale si nécessaire
    if (error != null && !isLoading && events.isEmpty() && popularEventsUi.isEmpty() && upcomingEventsUi.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.fi_rr_wifi_slash),
                contentDescription = "Erreur réseau",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Erreur de connexion",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error ?: "Impossible de charger les données",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.clearError()
                    viewModel.loadEvents()
                    viewModel.loadPopularEvents()
                    viewModel.loadUpcomingEvents()
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.fi_rr_rotate_right),
                    contentDescription = "Réessayer",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Réessayer")
            }
        }
        return
    }

    // -------------------------
    // Layout principal avec scrolling et pull-to-refresh
    // -------------------------
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        // -------------------------
        // SearchBar FIXE (ne scroll pas)
        // -------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val userName = currentUser?.let {
                    it.firstName ?: it.username
                } ?: "Utilisateur"

                Text(
                    text = "Bonjour $userName",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    painter = painterResource(R.drawable.fi_rr_bell),
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Optimisation: Suppression de l'animation inutile sur SearchBar
            SearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                onFilterClick = { showFilterMenu = !showFilterMenu },
                placeholder = "Rechercher un événement...",
                modifier = Modifier.fillMaxWidth()
            )

            // -------------------------
            // Filtres horizontaux (sous la SearchBar, aussi fixe)
            // -------------------------
            // Optimisation: Animation simplifiée et plus rapide
            AnimatedVisibility(
                visible = showFilterMenu,
                enter = expandVertically(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(150))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(filterScrollState)
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)

                    ) {
                        filters.forEach { filter ->
                            Filter(
                                label = filter,
                                isSelected = selectedFilter == filter,
                                onClick = {
                                    selectedFilter = if (selectedFilter == filter) null else filter
                                },
                                customColor = if (selectedFilter == filter)  getFilterColor(filter) else null,
                                customContentColor = if (selectedFilter == filter)  Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }

        // -------------------------
        // Contenu scrollable avec état préservé et pull-to-refresh
        // -------------------------
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = isLoading,
            onRefresh = {
                viewModel.loadEvents()
                viewModel.loadPopularEvents()
                viewModel.loadUpcomingEvents()
                userViewModel.loadProfile()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(mainScrollState)
            ) {


            // Titre avec skeleton pendant le chargement
            if (isLoading && upcomingEventsUi.isEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Evenements à venir",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 13.sp
                    )


                }
            }

            // Afficher un skeleton de chargement ou les événements à venir
            if (isLoading && upcomingEventsUi.isEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(3) {
                        UpcomingEventCardSkeleton()
                    }
                }
            } else if (!isLoading && upcomingEventsUi.isEmpty()) {
                EmptyEventsState(
                    message = "Aucun événement à venir disponible",
                    onRefresh = if (error != null) {
                        { viewModel.loadUpcomingEvents() }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                )
            } else if (upcomingEventsUi.isNotEmpty()) {
                LazyRow(
                    state = upcomingEventsScrollState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(upcomingEventsUi.size) { index ->
                        val event = upcomingEventsUi[index]
                        UpcomingEventCard(
                            title = event.title,
                            date = DateFormatter.formatShortDate(event.date),
                            location = event.location ?: "Burundi",
                            imageRes = event.imageRes,
                            onClick = {
                                navController.navigate("event_details/${event.id}")
                            }
                        )
                    }
                }
            }
            // -------------------------
            // Liste des événements avec MapEventList
            // -------------------------

            // Titre avec skeleton pendant le chargement
            if (isLoading && popularEventsUi.isEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Événement Populaire",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 13.sp
                    )


                }
            }

            // Afficher les événements populaires avec skeleton loading
            if (isLoading && popularEventsUi.isEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(2) {
                        EventCardSkeleton(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else if (!isLoading && popularEventsUi.isEmpty()) {
                EmptyEventsState(
                    message = "Aucun événement populaire disponible",
                    onRefresh = if (error != null) {
                        { viewModel.loadPopularEvents() }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                )
            } else if (popularEventsUi.isNotEmpty()) {
                MapEventList(
                    events = popularEventsUi,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    orientation = MapEventListOrientation.HORIZONTAL,
                    onEventClick = { event ->
                        navController.navigate("event_details/${event.id}")
                    },
                    onFavoriteClick = { event ->
                        // Toggle favoris via ViewModel
                        viewModel.toggleFavorite(event.id, event.isFavorite)
                    }
                )
            }

            // Titre avec skeleton pendant le chargement
            if (isLoading && filteredEvents.isEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(220.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .shimmerEffect()
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Recommandation Pour Toi",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 13.sp
                    )


                }
            }

            // Liste verticale de recommandations avec skeleton loading
            if (isLoading && filteredEvents.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) {
                        RecommendationCardSkeleton()
                    }
                }
            } else if (!isLoading && filteredEvents.isEmpty()) {
                if (searchQuery.isNotEmpty()) {
                    EmptySearchState(
                        query = searchQuery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                } else {
                    EmptyEventsState(
                        message = "Aucune recommandation disponible",
                        onRefresh = if (error != null) {
                            { viewModel.loadEvents() }
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }
            } else if (filteredEvents.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filteredEvents.take(5).forEach { event ->
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
                }
            }


            }
        }
        }
    }
}
