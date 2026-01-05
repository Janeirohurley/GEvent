package com.janeirohurley.gevent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.janeirohurley.gevent.navigation.Screen
import com.janeirohurley.gevent.ui.components.BottomNavigationBar
import com.janeirohurley.gevent.ui.screen.CancelBookingScreen
import com.janeirohurley.gevent.ui.screen.EventDetailsScreen
import com.janeirohurley.gevent.ui.screen.FavoriteScreen
import com.janeirohurley.gevent.ui.screen.HomeScreen
import com.janeirohurley.gevent.ui.screen.TicketsScreen
import com.janeirohurley.gevent.ui.theme.GEventTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            GEventTheme {
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(
                selectedRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        // Pop up to the start destination to avoid building up a large stack
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Ticket.route) {
                TicketsScreen(navController = navController)
            }
            composable(Screen.Favorites.route) {
                FavoriteScreen()
            }
//            composable(Screen.Setting.route) {
//                // TODO: Create SettingScreen
//                HomeScreen()
//            }
//            composable(Screen.Profile.route) {
//                // TODO: Create ProfileScreen
//                HomeScreen()
//            }
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
                EventDetailsScreen(navController = navController, imageRes = R.drawable.event_image)
            }
        }
    }
}
