package com.janeirohurley.gevent.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.model.TicketModel

@Composable
fun TicketCard(
    ticket: TicketModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var dividerY by remember { mutableStateOf<Float?>(null) }


    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        BoxWithConstraints {
            val cardWidth = maxWidth.coerceAtMost(380.dp)
            val density = LocalDensity.current

            // hauteur fixe approximative pour export
            val cardHeight = 400.dp

            Box(modifier = Modifier.width(cardWidth)) {

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Scanner le QR", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("Point this QR to the scan place", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        Spacer(Modifier.height(16.dp))
                        BoxWithConstraints {
                            val qrSize = maxWidth * 0.75f
                            Image(painterResource(R.drawable.qrcode), contentDescription = "QR Code", modifier = Modifier.size(qrSize))
                        }
                        Spacer(Modifier.height(24.dp))
                        DottedDivider(
                            modifier = Modifier.onGloballyPositioned {
                                dividerY = it.positionInParent().y
                            }
                        )

                        Spacer(Modifier.height(18.dp))
                        Text(ticket.code.uppercase(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        Spacer(Modifier.height(12.dp))
                        InfoRow("Nom", ticket.holderName)
                        ticket.seat?.let { InfoRow("Place", it) }
                        InfoRow("Prix", ticket.price)
                        InfoRow("Acheté le", ticket.purchaseDate)
                    }
                }

                // 🎯 Boules perforation toujours visibles
                dividerY?.let { y ->
                    TicketPerforation(
                        cardWidth = cardWidth,
                        dividerY = y
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
