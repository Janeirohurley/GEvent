package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.janeirohurley.gevent.R

@Composable
fun SettingScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var locationEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Paramètres",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Gérez vos préférences",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // Section: Notifications
        SettingSection(title = "Notifications") {
            SettingToggleItem(
                title = "Notifications Push",
                description = "Recevoir des notifications sur les événements",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Apparence
        SettingSection(title = "Apparence") {
            SettingToggleItem(
                title = "Mode Sombre",
                description = "Activer le thème sombre",
                checked = darkModeEnabled,
                onCheckedChange = { darkModeEnabled = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Confidentialité
        SettingSection(title = "Confidentialité") {
            SettingToggleItem(
                title = "Localisation",
                description = "Permettre l'accès à votre localisation",
                checked = locationEnabled,
                onCheckedChange = { locationEnabled = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingItem(
                title = "Confidentialité des données",
                description = "Gérer vos données personnelles",
                onClick = { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Compte
        SettingSection(title = "Compte") {
            SettingItem(
                title = "Changer le mot de passe",
                description = "Mettre à jour votre mot de passe",
                onClick = { /* TODO */ }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingItem(
                title = "Gérer le compte",
                description = "Informations et paramètres du compte",
                onClick = { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Support
        SettingSection(title = "Support") {
            SettingItem(
                title = "Centre d'aide",
                description = "FAQ et support",
                onClick = { /* TODO */ }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingItem(
                title = "Nous contacter",
                description = "Envoyer un message au support",
                onClick = { /* TODO */ }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingItem(
                title = "À propos",
                description = "Version 1.0.0",
                onClick = { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton Déconnexion
        Button(
            onClick = { /* TODO: Déconnexion */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF5252)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "Déconnexion",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SettingSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Icon(
            painter = painterResource(R.drawable.fi_rr_settings),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        )
    }
}
