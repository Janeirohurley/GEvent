package com.janeirohurley.gevent.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.janeirohurley.gevent.R

/* -----------------------------
   Navigation items
------------------------------ */
sealed class NavigationItem(
    val route: String,
    val title: String,
    val selectedIconRes: Int,
    val unselectedIconRes: Int
) {
    object Home : NavigationItem(
        "home",
        "Accueil",
        R.drawable.home,
        R.drawable.home
    )

    object Explore : NavigationItem(
        "ticket",
        "Ticket",
        R.drawable.fi_rr_ticket,
        R.drawable.fi_rr_ticket
    )

    object Favorites : NavigationItem(
        "favorites",
        "Favoris",
        R.drawable.fi_rr_heart,
        R.drawable.fi_rr_heart
    )

    object Setting : NavigationItem(
        "setting",
        "Setting",
        R.drawable.fi_rr_settings,
        R.drawable.fi_rr_settings
    )

    object Profile : NavigationItem(
        "profile",
        "Profil",
        R.drawable.fi_rr_portrait,
        R.drawable.fi_rr_portrait
    )

    companion object {
        // Bouton central pour la gestion d'événements
        const val MANAGE_EVENTS_ROUTE = "manage_events"

        // Items à gauche du bouton central
        val leftItems = listOf(Home, Explore)

        // Items à droite du bouton central
        val rightItems = listOf(Favorites, Setting)

        // Tous les items normaux (pour compatibilité)
        val items = listOf(Home, Explore, Favorites, Setting, Profile)
    }
}

/* -----------------------------
   Bottom Navigation Bar
------------------------------ */
@Composable
fun BottomNavigationBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Items à gauche
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavigationItem.leftItems.forEach { item ->
                        BottomNavItem(
                            item = item,
                            isSelected = selectedRoute == item.route,
                            onClick = { onNavigate(item.route) }
                        )
                    }
                }

                // Spacer pour le bouton central
                Spacer(modifier = Modifier.width(72.dp))

                // Items à droite
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavigationItem.rightItems.forEach { item ->
                        BottomNavItem(
                            item = item,
                            isSelected = selectedRoute == item.route,
                            onClick = { onNavigate(item.route) }
                        )
                    }
                }
            }
        }

        // Bouton central flottant
        FloatingManageButton(
            isSelected = selectedRoute == NavigationItem.MANAGE_EVENTS_ROUTE,
            onClick = { onNavigate(NavigationItem.MANAGE_EVENTS_ROUTE) },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

/* -----------------------------
   Bottom Navigation Item
------------------------------ */
@Composable
private fun BottomNavItem(
    item: NavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Optimisation: Animations plus rapides et specs simplifiées
    val iconColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        animationSpec = tween(durationMillis = 150),
        label = "iconColor"
    )

    val indicatorWidth by animateDpAsState(
        targetValue = if (isSelected) 30.dp else 0.dp,
        animationSpec = tween(durationMillis = 150),
        label = "indicatorWidth"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.6f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "pressAlpha"
    )

    Box(
        modifier = Modifier
            .width(72.dp)
            .fillMaxHeight()
            .alpha(alpha)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Indicator ABSOLUTE (au-dessus de la BottomBar)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)

                .height(3.dp)
                .width(indicatorWidth)
                .background(
                    MaterialTheme.colorScheme.primary,

                )
                .zIndex(1f) //  assure qu'il est au-dessus
        )

        //  Icône centrée
        Icon(
            painter = painterResource(
                if (isSelected)
                    item.selectedIconRes
                else
                    item.unselectedIconRes
            ),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .align(Alignment.Center)
                .size(20.dp)
        )
    }
}

/* -----------------------------
   Floating Central Manage Button
------------------------------ */
@Composable
private fun FloatingManageButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.surface
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 200),
        label = "backgroundColor"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.onPrimaryContainer,
        animationSpec = tween(durationMillis = 200),
        label = "iconColor"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "scale"
    )

    Surface(
        modifier = modifier
            .offset(y = (-20).dp)
            .size(50.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(36.dp),
        color = backgroundColor,
        shadowElevation = 1.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.fi_rr_calendar),
                contentDescription = "Gérer événements",
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
