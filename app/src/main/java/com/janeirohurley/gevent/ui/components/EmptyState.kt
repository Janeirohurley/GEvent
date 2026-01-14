package com.janeirohurley.gevent.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.janeirohurley.gevent.R

/**
 * Composant pour afficher un état vide avec style
 */
@Composable
fun EmptyState(
    title: String,
    message: String,
    iconRes: Int = R.drawable.fi_rr_heart, // Icône par défaut
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icône illustrative
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Titre
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )

        // Bouton optionnel
        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onButtonClick,
                modifier = Modifier.height(32.dp), // 👈 hauteur réduite
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = buttonText,
                    fontSize = 11.sp
                )
            }
        }

    }
}

/**
 * État vide pour les événements
 */
@Composable
fun EmptyEventsState(
    message: String = "Aucun événement disponible pour le moment",
    onRefresh: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = "Aucun événement",
        message = message,
        iconRes = R.drawable.brush_cleaning,
        buttonText = if (onRefresh != null) "Rafraîchir" else null,
        onButtonClick = onRefresh,
        modifier = modifier
    )
}

/**
 * État vide pour la recherche
 */
@Composable
fun EmptySearchState(
    query: String,
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = "Aucun résultat",
        message = "Aucun événement trouvé pour \"$query\"\nEssayez avec d'autres mots-clés",
        iconRes = R.drawable.fi_rr_settings,
        modifier = modifier
    )
}

/**
 * État d'erreur
 */
@Composable
fun ErrorState(
    title: String = "Oups!",
    message: String = "Une erreur s'est produite",
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = title,
        message = message,
        iconRes = R.drawable.fi_rr_settings,
        buttonText = if (onRetry != null) "Réessayer" else null,
        onButtonClick = onRetry,
        modifier = modifier
    )
}
