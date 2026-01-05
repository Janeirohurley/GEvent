package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.ui.components.RecommendationCard


@Composable
fun FavoriteScreen(modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ){
            Text("Favorites",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Liste verticale de recommandations
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RecommendationCard(
                    title = "Festival de Musique 2026",
                    date = "SAT 15 Mai 2026, 18:00",
                    location = "Bujumbura",
                    imageRes = R.drawable.event_image,
                    isFree = false,
                    price = "2.000 BIF",
                    onClick = { println("Festival de Musique clicked") } ,


                )

                RecommendationCard(
                    title = "Concert Live Rock",
                    date = "FRI 20 Juin 2026, 20:00",
                    location = "Gitega",
                    imageRes = R.drawable.creator_image,
                    isFree = false,
                    price = "5.000 BIF",
                    onClick = { println("Concert Live Rock clicked") }
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
}