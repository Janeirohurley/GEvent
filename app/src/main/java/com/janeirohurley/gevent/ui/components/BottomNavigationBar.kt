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
        val items = listOf(Home, Explore, Favorites, Setting,Profile)
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
    Surface(
        modifier = modifier.fillMaxWidth() .height(72.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationItem.items.forEach { item ->
                BottomNavItem(
                    item = item,
                    isSelected = selectedRoute == item.route,
                    onClick = { onNavigate(item.route) }
                )
            }
        }
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
                .size(26.dp)
        )
    }
}
