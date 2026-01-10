package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.viewmodel.AuthViewModel
import com.janeirohurley.gevent.viewmodel.UserViewModel
import coil.compose.AsyncImage

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    authViewModel: AuthViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    // États du profil
    val currentUser by userViewModel.currentUser.collectAsState()
    val authUser by authViewModel.userProfile.collectAsState()

    // Utiliser le profil de l'auth ou celui du UserViewModel
    val user = currentUser ?: authUser

    // Charger le profil si nécessaire
    LaunchedEffect(Unit) {
        if (currentUser == null) {
            userViewModel.loadProfile()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Header avec photo de profil
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Photo de profil
                Box {
                    if (user?.profileImage != null) {
                        AsyncImage(
                            model = user.profileImage,
                            contentDescription = "Photo de profil",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(
                                    4.dp,
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                                .shadow(8.dp, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.fi_rr_portrait),
                            contentDescription = "Photo de profil",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(
                                    4.dp,
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                                .shadow(0.dp, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Badge edit
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(32.dp)
                            .clickable { /* TODO: Modifier photo */ },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.fi_rr_settings),
                            contentDescription = "Modifier",
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = user?.let {
                        if (!it.firstName.isNullOrBlank() && !it.lastName.isNullOrBlank()) {
                            "${it.firstName} ${it.lastName}"
                        } else {
                            it.username
                        }
                    } ?: "Utilisateur",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = user?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TODO: Statistiques - nécessite endpoints API pour nombre d'événements, tickets, favoris
        // Row(
        //     modifier = Modifier
        //         .fillMaxWidth()
        //         .padding(horizontal = 24.dp),
        //     horizontalArrangement = Arrangement.SpaceEvenly
        // ) {
        //     StatItem(number = "12", label = "Événements")
        //     StatItem(number = "45", label = "Tickets")
        //     StatItem(number = "8", label = "Favoris")
        // }

        Spacer(modifier = Modifier.height(16.dp))

        // Section: Informations personnelles
        ProfileSection(title = "Informations personnelles") {
            ProfileInfoItem(
                label = "Nom complet",
                value = user?.let {
                    if (!it.firstName.isNullOrBlank() && !it.lastName.isNullOrBlank()) {
                        "${it.firstName} ${it.lastName}"
                    } else if (!it.firstName.isNullOrBlank()) {
                        it.firstName
                    } else if (!it.lastName.isNullOrBlank()) {
                        it.lastName
                    } else {
                        "Non renseigné"
                    }
                } ?: "Non renseigné",
                onClick = { /* TODO */ }
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color.copy(alpha = 0.4f)
            )

            ProfileInfoItem(
                label = "Email",
                value = user?.email ?: "Non renseigné",
                onClick = { /* TODO */ }
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color.copy(alpha = 0.4f)
            )

            ProfileInfoItem(
                label = "Téléphone",
                value = user?.phoneNumber ?: "Non renseigné",
                onClick = { /* TODO */ }
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color.copy(alpha = 0.4f)
            )

            ProfileInfoItem(
                label = "Date de naissance",
                value = user?.dateOfBirth ?: "Non renseigné",
                onClick = { /* TODO */ }
            )
        }

        // TODO: Section Préférences - nécessite endpoints API pour catégories favorites et langue
        // Spacer(modifier = Modifier.height(16.dp))
        //
        // ProfileSection(title = "Préférences") {
        //     ProfileInfoItem(
        //         label = "Catégories favorites",
        //         value = "Musique, Sport, Education",
        //         onClick = { /* TODO */ }
        //     )
        //     HorizontalDivider(
        //         modifier = Modifier.padding(vertical = 8.dp),
        //         thickness = DividerDefaults.Thickness,
        //         color = DividerDefaults.color.copy(alpha = 0.4f)
        //     )
        //
        //     ProfileInfoItem(
        //         label = "Langue",
        //         value = "Français",
        //         onClick = { /* TODO */ }
        //     )
        // }

        Spacer(modifier = Modifier.height(32.dp))

        // Bouton Modifier le profil
        Button(
            onClick = { /* TODO: Modifier profil */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.fi_rr_settings),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Modifier le profil",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton Déconnexion
        OutlinedButton(
            onClick = { authViewModel.logout() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.fi_rr_settings),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
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
private fun StatItem(number: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun ProfileSection(
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
                modifier = Modifier.padding(10.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(
    label: String,
    value: String,
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
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            painter = painterResource(R.drawable.fi_rr_settings),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.size(16.dp)
        )
    }
}
