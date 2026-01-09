package com.janeirohurley.gevent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.janeirohurley.gevent.navigation.Screen
import com.janeirohurley.gevent.ui.components.BottomNavigationBar
import com.janeirohurley.gevent.ui.navigation.*
import com.janeirohurley.gevent.ui.screen.CancelBookingScreen
import com.janeirohurley.gevent.ui.screen.EventDetailsScreen
import com.janeirohurley.gevent.ui.screen.FavoriteScreen
import com.janeirohurley.gevent.ui.screen.HomeScreen
import com.janeirohurley.gevent.ui.screen.OrderScreen
import com.janeirohurley.gevent.ui.screen.TicketsScreen
import com.janeirohurley.gevent.ui.screen.ViewTicket
import com.janeirohurley.gevent.model.TicketModel
import com.janeirohurley.gevent.ui.screen.ProfileScreen
import com.janeirohurley.gevent.ui.screen.SettingScreen
import com.janeirohurley.gevent.ui.theme.GEventTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            GEventTheme {
                // Version originale avec transitions ultra-rapides (150ms)
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
    val mainRoutes = remember {
        com.janeirohurley.gevent.ui.components.NavigationItem.items.map { it.route }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (currentRoute in mainRoutes) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    BottomNavigationBar(
                        selectedRoute = currentRoute,
                        onNavigate = { route ->
                            // Optimisation: Navigation ultra-rapide avec cache d'état
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(Screen.Home.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier
                    )
                    // Fond sous la barre système pour continuité visuelle
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
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            // Transitions ULTRA-RAPIDES: 150ms avec fade simple
            enterTransition = {
                fadeIn(animationSpec = tween(150))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(100))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(150))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(100))
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Ticket.route) {
                TicketsScreen(navController = navController)
            }
            composable(Screen.viewTicket.route) {
                // Ticket simulé pour la démo
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
                ViewTicket(ticket = ticket, onBack ={navController.popBackStack()})
            }
            composable(Screen.Favorites.route) {
                FavoriteScreen()
            }
            composable(Screen.Setting.route) {
                SettingScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
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
            composable(Screen.EventDetails.route) {
                // Exemple d'EventUiModel fictif pour la démo
                val event = com.janeirohurley.gevent.viewmodel.EventUiModel(
                    id = "1",
                    title = "Soirée Networking Paris 2024 burundi",
                    date = "5 janvier 2026, 19:00",
                    imageRes = R.drawable.event_image, // Remplace par une ressource valide
                    isFavorite = false,
                    creatorImageRes = R.drawable.creator_image, // Remplace par une ressource valide
                    creatorName = "Jean Dupont",
                    joinedAvatars = listOf(
                        R.drawable.creator_image,
                        R.drawable.event_image,

                    ),
                    isFree = false,
                    price = "2000 Fbu"
                )
                EventDetailsScreen(navController = navController, event = event)
            }

            composable(Screen.Order.route) {
                // Exemple d'EventUiModel fictif pour la démo
                val event = com.janeirohurley.gevent.viewmodel.EventUiModel(
                    id = "1",
                    title = "Soirée Networking Paris 2024 burundi",
                    date = "5 janvier 2026, 19:00",
                    imageRes = R.drawable.event_image, // Remplace par une ressource valide
                    isFavorite = false,
                    creatorImageRes = R.drawable.creator_image, // Remplace par une ressource valide
                    creatorName = "Jean Dupont",
                    joinedAvatars = listOf(
                        R.drawable.creator_image,
                        R.drawable.event_image,

                        ),
                    isFree = false,
                    price = "2000 Fbu"
                )
                OrderScreen(
                    onBack = { navController.popBackStack() },
                    event = event,
                    onViewTicket = { navController.navigate("view_ticket") },
                    onGoHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
