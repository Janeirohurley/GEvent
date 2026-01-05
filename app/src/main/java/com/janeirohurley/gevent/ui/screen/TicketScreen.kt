package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.ui.components.AppBottomDrawer
import com.janeirohurley.gevent.ui.components.EventStatusTabs
import com.janeirohurley.gevent.ui.components.RecommendationCard
import com.janeirohurley.gevent.ui.components.StarRating


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(modifier: Modifier = Modifier, navController: NavHostController,){

    val tabs = listOf("A venir", "Terminé", "Annulé")
    var selectedTab by remember { mutableStateOf("A venir") }

    var showSheet by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(0) }

    var comment by remember { mutableStateOf("") }



    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ){
            Text("Tous les ticket",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold )
        }

        EventStatusTabs(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
        )
        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 5.dp)
        ) {
            // Liste verticale de recommandations
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RecommendationCard(
                    title = "Festival de Musique 2026",
                    date = "SAT 15 Mai 2026, 18:00",
                    location = "Bujumbura",
                    imageRes = R.drawable.event_image,
                    isFree = false,
                    price = "Payee",
                    onClick = { println("Festival de Musique clicked") },
                    actions = {
                        OutlinedButton(
                            onClick = {navController.navigate("cancel_booking") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp), // 👈 ici
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor =  MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color =  MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text("Annuler")
                        }

                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Voir le Ticket")
                        }


                    }
                )

                RecommendationCard(
                    title = "Concert Live Rock",
                    date = "FRI 20 Juin 2026, 20:00",
                    location = "Gitega",
                    imageRes = R.drawable.creator_image,
                    isFree = false,
                    price = "Terminee",
                    onClick = { println("Concert Live Rock clicked") },
                    actions = {
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp), // 👈 ici
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor =  MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color =  MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text("Voir les details")
                        }

                        Button(
                            onClick = { showSheet = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("évaluez-nous")
                        }


                    }

                )

                RecommendationCard(
                    title = "Théâtre Moderne",
                    date = "WED 10 Juillet 2026, 19:30",
                    location = "Bujumbura",
                    imageRes = R.drawable.event_image,
                    isFree = false,
                    price = "Annuller",
                    onClick = { println("Théâtre Moderne clicked") },
                    actions = {
                        OutlinedButton(
                            onClick = {  },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp), // 👈 ici
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor =  MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color =  MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text("Voir les details")
                        }
                    }
                )
                RecommendationCard(
                    title = "Théâtre Moderne",
                    date = "WED 10 Juillet 2026, 19:30",
                    location = "Bujumbura",
                    imageRes = R.drawable.event_image,
                    isFree = true,
                    onClick = { println("Théâtre Moderne clicked") }
                )
                RecommendationCard(
                    title = "Théâtre Moderne",
                    date = "WED 10 Juillet 2026, 19:30",
                    location = "Bujumbura",
                    imageRes = R.drawable.event_image,
                    isFree = true,
                    onClick = { println("Théâtre Moderne clicked") }
                )
                RecommendationCard(
                    title = "Théâtre Moderne",
                    date = "WED 10 Juillet 2026, 19:30",
                    location = "Bujumbura",
                    imageRes = R.drawable.event_image,
                    isFree = true,
                    onClick = { println("Théâtre Moderne clicked") }
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
    AppBottomDrawer(
        isVisible = showSheet,
        onDismiss = { showSheet = false }
    ) {
        Text(
            "Évaluez-nous",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        RecommendationCard(
            title = "Théâtre Moderne",
            date = "WED 10 Juillet 2026, 19:30",
            location = "Bujumbura",
            imageRes = R.drawable.event_image,
            isFree = false,
            price = "Terminé",
            onClick = {},
            modifier = Modifier.padding(horizontal = 7.dp)
        )

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Votre note",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(8.dp))

            StarRating(
                rating = rating,
                onRatingSelected = { rating = it },
                size = 28.dp
            )

        }

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            placeholder = {
                Text("Laissez un commentaire (optionnel)")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            maxLines = 5
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { showSheet = false },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Annuler")
            }

            Button(
                onClick = {
                    // 👉 action submit
                    println("Rating: $rating | Comment: $comment")
                    showSheet = false
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                enabled = rating > 0
            ) {
                Text("Continuer")
            }
        }
    }

}