package com.janeirohurley.gevent.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.janeirohurley.gevent.ui.components.Filter
import com.janeirohurley.gevent.ui.components.MapEventList
import com.janeirohurley.gevent.ui.components.RecommendationCard
import com.janeirohurley.gevent.ui.components.SearchBar
import com.janeirohurley.gevent.viewmodel.EventUiModel
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.ui.components.MapEventListOrientation
import com.janeirohurley.gevent.ui.components.UpcomingEventCard

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

@Composable
fun HomeScreen(modifier: Modifier = Modifier,navController:NavHostController ) {

    // -------------------------
    // États avec préservation
    // -------------------------
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filters = listOf("Music", "Education", "Film&Cinema", "Sport", "Jeux", "Voyage", "Concert")
    var selectedFilter by rememberSaveable { mutableStateOf<String?>(null) }
    var showFilterMenu by rememberSaveable { mutableStateOf(false) }

    // États de scroll préservés
    val mainScrollState = rememberScrollState()
    val filterScrollState = rememberScrollState()
    val upcomingEventsScrollState = rememberLazyListState()

    val events = remember {
        listOf(
            EventUiModel(
                id = "1",
                title = "Concert Live",
                date = "THU 26 Mai,09:00 - FRI 27 Mai,10:00",
                imageRes = R.drawable.creator_image,
                creatorImageRes = R.drawable.creator_image,
                creatorName = "Janeiro Hurley",
                joinedAvatars = listOf(
                    R.drawable.event_image,
                    R.drawable.creator_image
                ),
                isFree = false,
                price = "10.000 BIF",
                isFavorite = true
            ),
            EventUiModel(
                id = "2",
                title = "Tech Meetup",
                date = "18 Avril 2026",
                imageRes = R.drawable.event_image,
                creatorImageRes = R.drawable.creator_image,
                creatorName = "Janeiro Hurley",
                joinedAvatars = listOf(
                    R.drawable.event_image,
                    R.drawable.creator_image
                ),
                isFree = true
            ),
            EventUiModel(
                id = "3",
                title = "Festival Film",
                date = "THU 26 Mai ,09:00 - FRI 27 Mai,10:00",
                imageRes = R.drawable.event_image,
                creatorImageRes = R.drawable.creator_image,
                creatorName = "Janeiro Hurley",
                joinedAvatars = listOf(
                    R.drawable.event_image,
                    R.drawable.creator_image
                ),
                isFree = false,
                price = "5.000 BIF"
            ),
            EventUiModel(
                id = "4",
                title = "Exposition d'Art",
                date = "2 Mai 2026",
                imageRes = R.drawable.event_image,
                creatorImageRes = R.drawable.creator_image,
                creatorName = "Janeiro Hurley",
                joinedAvatars = listOf(
                    R.drawable.event_image,
                    R.drawable.creator_image
                ),
                isFree = true
            ),
            EventUiModel(
                id = "5",
                title = "Marathon",
                date = "10 Mai 2026",
                imageRes = R.drawable.event_image,
                creatorImageRes = R.drawable.creator_image,
                creatorName = "Janeiro Hurley",
                joinedAvatars = listOf(
                    R.drawable.event_image,
                    R.drawable.creator_image
                ),
                isFree = false,
                price = "15.000 BIF"
            )
        )
    }

    // Optimisation: Utiliser derivedStateOf pour éviter les recompositions inutiles
    val filteredEvents by remember {
        derivedStateOf {
            events.filter { event ->
                val matchesSearch = searchQuery.isEmpty() ||
                    event.title.contains(searchQuery, ignoreCase = true)

                val matchesFilter = selectedFilter == null ||
                    event.title.contains(selectedFilter!!, ignoreCase = true)

                matchesSearch && matchesFilter
            }
        }
    }

    // -------------------------
    // Layout principal avec scrolling
    // -------------------------
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                Text(
                    text = "Bonjour Janvier",
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
        // Contenu scrollable avec état préservé
        // -------------------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(mainScrollState)
        ) {


            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Evenements à venir",
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = "Tous",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            // Liste Horizontal A venier

            LazyRow(
                state = upcomingEventsScrollState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    UpcomingEventCard(
                        title = "Festival de Musique 2026",
                        date = "SAT 15 Mai 2026, 18:00",
                        location = "Bujumbura",
                        imageRes = R.drawable.event_image,
                        onClick = { println("Festival de Musique clicked") }

                    )
                }
                item {
                    UpcomingEventCard(
                        title = "Concert Live Rock",
                        date = "FRI 20 Juin 2026, 20:00",
                        location = "Gitega",
                        imageRes = R.drawable.creator_image,
                        onClick = { println("Concert Live Rock clicked") }
                    )

                }

                   item {
                    UpcomingEventCard(
                        title = "Théâtre Moderne",
                        date = "WED 10 Juillet 2026, 19:30",
                        location = "Bujumbura",
                        imageRes = R.drawable.event_image,
                        onClick = { println("Théâtre Moderne clicked") }
                    )
                }

            }
            // -------------------------
            // Liste des événements avec MapEventList
            // -------------------------

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Événement Populaire",
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = "Tous",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            MapEventList(
                events = filteredEvents,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                orientation = MapEventListOrientation.HORIZONTAL,
                onEventClick = { event ->
                    // Action au clic sur un événement
                    println("Event clicked: ${event.title}")
                },
                onFavoriteClick = { event ->
                    // Toggle favoris
                    println("Favorite toggled for: ${event.title}")
                }
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Recommandation Pour Toi",
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = "Tous",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            // Liste verticale de recommandations
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RecommendationCard(
                    title = "Festival de Musique 2026",
                    date = "SAT 15 Mai 2026, 18:00",
                    location = "Bujumbura",
                    imageRes = R.drawable.event_image,
                    isFree = false,
                    price = "2.000 BIF",
                    onClick = { println("Festival de Musique clicked") }
                )

                RecommendationCard(
                    title = "Concert Live Rock",
                    date = "FRI 20 Juin 2026, 20:00",
                    location = "Gitega",
                    imageRes = R.drawable.creator_image,
                    isFree = false,
                    price = "5.000 BIF",
                    onClick = { navController.navigate("event_details") }
                )

                RecommendationCard(
                    title = "Théâtre Moderne",
                    date = "WED 10 Juillet 2026, 19:30",
                    location = "Bujumbura",
                    imageRes = R.drawable.event_image,
                    isFree = true,
                    onClick = { println("Théâtre Moderne clicked") }
                )
            }


        }
    }
}
