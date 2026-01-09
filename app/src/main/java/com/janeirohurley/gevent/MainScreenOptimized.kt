package com.janeirohurley.gevent

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.janeirohurley.gevent.navigation.Screen
import com.janeirohurley.gevent.ui.components.BottomNavigationBar
import com.janeirohurley.gevent.ui.screen.*
import com.janeirohurley.gevent.model.TicketModel
import com.janeirohurley.gevent.viewmodel.EventUiModel
import com.janeirohurley.gevent.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/**
 * Version ULTRA-OPTIMISÉE du MainScreen
 *
 * Différences avec l'ancienne version:
 * 1. Pas de NavHost - Les screens restent en mémoire
 * 2. Crossfade ultra-rapide pour les transitions (100ms)
 * 3. Les screens ne sont créés qu'UNE SEULE FOIS
 * 4. Navigation INSTANTANÉE
 */

@Composable
fun MainScreenOptimized() {
    // NavController pour gérer TOUTES les navigations
    val navController = rememberNavController()

    // Observer la route actuelle
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    // Routes principales (avec bottom bar)
    val mainRoutes = remember {
        listOf("home", "ticket", "favorites", "setting", "profile")
    }

    // Check si on est sur une route principale
    val showBottomBar = currentRoute in mainRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Afficher la bottom bar seulement sur les routes principales
            if (showBottomBar) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    BottomNavigationBar(
                        selectedRoute = currentRoute,
                        onNavigate = { route ->
                            // Navigation vers les routes principales
                            if (currentRoute != route && route in mainRoutes) {
                                navController.navigate(route) {
                                    popUpTo("home") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier
                    )
                    // Fond sous la barre système
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .height(24.dp)
                            .background(MaterialTheme.colorScheme.surface)
                    )
                }
            }
        }
    ) { innerPadding ->
        // NavHost avec transitions optimisées pour TOUS les screens
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                // Animation d'entrée fluide
                slideInHorizontally(
                    initialOffsetX = { it / 4 },
                    animationSpec = tween(200)
                ) + fadeIn(tween(200))
            },
            exitTransition = {
                // Animation de sortie fluide
                slideOutHorizontally(
                    targetOffsetX = { -it / 4 },
                    animationSpec = tween(200)
                ) + fadeOut(tween(200))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 4 },
                    animationSpec = tween(200)
                ) + fadeIn(tween(200))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it / 4 },
                    animationSpec = tween(200)
                ) + fadeOut(tween(200))
            }
        ) {
            // Routes principales (avec bottom bar)
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("ticket") {
                TicketsScreen(navController = navController)
            }
            composable("favorites") {
                FavoriteScreen()
            }
            composable("setting") {
                // TODO: Créer SettingScreen
                HomeScreen(navController = navController)
            }
            composable("profile") {
                // TODO: Créer ProfileScreen
                HomeScreen(navController = navController)
            }

            // Routes secondaires (SANS bottom bar)
            composable(Screen.EventDetails.route) {
                val event = EventUiModel(
                    id = "1",
                    title = "Soirée Networking Paris 2024 burundi",
                    date = "5 janvier 2026, 19:00",
                    imageRes = R.drawable.event_image,
                    isFavorite = false,
                    creatorImageRes = R.drawable.creator_image,
                    creatorName = "Jean Dupont",
                    joinedAvatars = listOf(R.drawable.creator_image, R.drawable.event_image),
                    isFree = false,
                    price = "2000 Fbu"
                )
                EventDetailsScreen(navController = navController, event = event)
            }

            composable(Screen.Order.route) {
                val event = EventUiModel(
                    id = "1",
                    title = "Soirée Networking Paris 2024 burundi",
                    date = "5 janvier 2026, 19:00",
                    imageRes = R.drawable.event_image,
                    isFavorite = false,
                    creatorImageRes = R.drawable.creator_image,
                    creatorName = "Jean Dupont",
                    joinedAvatars = listOf(R.drawable.creator_image, R.drawable.event_image),
                    isFree = false,
                    price = "2000 Fbu"
                )
                OrderScreen(
                    onBack = { navController.popBackStack() },
                    event = event,
                    onViewTicket = { navController.navigate("view_ticket") },
                    onGoHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.viewTicket.route) {
                val ticket = TicketModel(
                    code = "ABC123456",
                    eventTitle = "Soirée Networking Paris 2024 burundi",
                    eventDate = "5 janvier 2026, 19:00",
                    eventLocation = "Grand Palais, Bujumbura",
                    holderName = "Jean Dupont",
                    seat = "A12",
                    price = "2000 Fbu",
                    purchaseDate = "2 janvier 2026",
                    qrCode = null
                )
                ViewTicket(ticket = ticket, onBack = { navController.popBackStack() })
            }

            composable(Screen.CancelBooking.route) {
                CancelBookingScreen(
                    reasons = listOf(
                        "Changement de plan",
                        "Problème personnel",
                        "Météo défavorable",
                        "Autre"
                    ),
                    onBack = { navController.popBackStack() },
                    onCancelBooking = { reason, comment ->
                        println("Annulé pour: $reason | Commentaire: $comment")
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
