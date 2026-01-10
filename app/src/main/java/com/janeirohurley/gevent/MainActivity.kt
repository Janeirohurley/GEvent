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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.janeirohurley.gevent.ui.screen.LoginScreen
import com.janeirohurley.gevent.ui.screen.RegisterScreen
import com.janeirohurley.gevent.ui.screen.ManageEventsScreen
import com.janeirohurley.gevent.ui.theme.GEventTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.janeirohurley.gevent.viewmodel.AuthViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment

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
    val authViewModel: AuthViewModel = viewModel()
    val isCheckingAuth by authViewModel.isCheckingAuth.collectAsState()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
    val mainRoutes = remember {
        com.janeirohurley.gevent.ui.components.NavigationItem.items.map { it.route }
    }

    // Gérer la redirection en fonction de l'état d'authentification
    LaunchedEffect(isCheckingAuth, isAuthenticated) {
        if (!isCheckingAuth) {
            val currentDest = navController.currentBackStackEntry?.destination?.route
            if (isAuthenticated && (currentDest == "login" || currentDest == "register")) {
                navController.navigate(Screen.Home.route) {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            } else if (!isAuthenticated && currentDest !in listOf("login", "register")) {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    // Afficher un écran de chargement pendant la vérification
    if (isCheckingAuth) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (currentRoute in mainRoutes) {
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
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) Screen.Home.route else "login",
            modifier = Modifier.padding(innerPadding),
            // Transitions ULTRA-RAPIDES: 150ms avec fade simple
            enterTransition = {
                fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(250))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(200))
            }
        ) {
            // Écrans d'authentification
            composable("login") {
                LoginScreen(navController = navController)
            }
            composable("register") {
                RegisterScreen(navController = navController)
            }

            // Écrans principaux
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Ticket.route) {
                TicketsScreen(navController = navController)
            }
            composable("manage_events") {
                ManageEventsScreen(navController = navController)
            }
            composable("view_ticket/{ticketId}") { backStackEntry ->
                val ticketId = backStackEntry.arguments?.getString("ticketId") ?: ""
                val ticketViewModel: com.janeirohurley.gevent.viewmodel.TicketViewModel = viewModel()

                // Charger le ticket spécifique
                LaunchedEffect(ticketId) {
                    ticketViewModel.loadMyTickets() // Charge tous les tickets
                }

                // Récupérer les tickets
                val tickets by ticketViewModel.tickets.collectAsState()
                val isLoading by ticketViewModel.isLoading.collectAsState()

                // Trouver le ticket correspondant
                val ticket = tickets.find { it.id == ticketId }?.let {
                    TicketModel(
                        code = it.code,
                        eventTitle = it.event.title,
                        eventDate = com.janeirohurley.gevent.utils.DateUtils.formatDateFromString(
                            it.event.date,
                            inputPattern = "yyyy-MM-dd'T'HH:mm:ss",
                            outputPattern = "dd MMMM yyyy, HH:mm"
                        ) ?: it.event.date,
                        eventLocation = it.event.location ?: "Non spécifié",
                        holderName = it.holderName,
                        seat = it.seat,
                        price = it.price,
                        purchaseDate = com.janeirohurley.gevent.utils.DateUtils.formatDateFromString(
                            it.purchaseDate,
                            inputPattern = "yyyy-MM-dd'T'HH:mm:ss",
                            outputPattern = "dd MMMM yyyy"
                        ) ?: it.purchaseDate,
                        qrCode = it.qrCode
                    )
                }

                if (ticket != null) {
                    ViewTicket(ticket = ticket, onBack = { navController.popBackStack() })
                } else if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ticket non trouvé")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { navController.popBackStack() }) {
                                Text("Retour")
                            }
                        }
                    }
                }
            }
            composable(Screen.Favorites.route) {
                FavoriteScreen(navController = navController)
            }
            composable(Screen.Setting.route) {
                SettingScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    authViewModel = authViewModel
                )
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
            composable("event_details/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: "1"
                val eventViewModel: com.janeirohurley.gevent.viewmodel.EventViewModel = viewModel()

                // Charger l'événement spécifique
                LaunchedEffect(eventId) {
                    eventViewModel.loadEventById(eventId)
                }

                // Récupérer l'événement depuis le ViewModel
                val currentEvent by eventViewModel.currentEvent.collectAsState()
                val isLoading by eventViewModel.isLoading.collectAsState()

                // Convertir en EventUiModel si trouvé
                val event = currentEvent?.let {
                    com.janeirohurley.gevent.utils.DataMapper.run { it.toUiModel() }
                }

                if (event != null) {
                    EventDetailsScreen(navController = navController, event = event)
                } else if (isLoading) {
                    // Afficher un indicateur de chargement
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Afficher un message d'erreur
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Événement non trouvé")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { navController.popBackStack() }) {
                                Text("Retour")
                            }
                        }
                    }
                }
            }

            composable("order/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: "1"
                val eventViewModel: com.janeirohurley.gevent.viewmodel.EventViewModel = viewModel()

                // Charger l'événement spécifique
                LaunchedEffect(eventId) {
                    eventViewModel.loadEventById(eventId)
                }

                // Récupérer l'événement depuis le ViewModel
                val currentEvent by eventViewModel.currentEvent.collectAsState()
                val isLoading by eventViewModel.isLoading.collectAsState()

                // Convertir en EventUiModel si trouvé
                val event = currentEvent?.let {
                    com.janeirohurley.gevent.utils.DataMapper.run { it.toUiModel() }
                }

                if (event != null) {
                    OrderScreen(
                        onBack = { navController.popBackStack() },
                        event = event,
                        onViewTicket = { ticketId ->
                            navController.navigate("view_ticket/$ticketId")
                        },
                        onGoHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                } else if (isLoading) {
                    // Afficher un indicateur de chargement
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Afficher un message d'erreur
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Événement non trouvé")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { navController.popBackStack() }) {
                                Text("Retour")
                            }
                        }
                    }
                }
            }
        }
    }
}
