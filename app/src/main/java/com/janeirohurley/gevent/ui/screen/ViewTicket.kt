
package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.janeirohurley.gevent.model.TicketModel
import com.janeirohurley.gevent.ui.components.TicketCard
import com.janeirohurley.gevent.ui.components.TicketCardForExport
import com.janeirohurley.gevent.ui.components.generateTicketBitmap
import com.janeirohurley.gevent.ui.components.saveBitmapToGallery
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewTicket(
    ticket: TicketModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var exportRequested by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mon ticket") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        isLoading = true
                        exportRequested = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Spacer(Modifier.width(8.dp))
                    Text("Télécharger le ticket")
                }
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            TicketCard(ticket = ticket)
        }

        // 🌟 Loader overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // ⚡ Export Ticket hors écran
        if (exportRequested) {
            TicketCardForExport(
                ticket = ticket,
                onBitmapReady = { bitmap ->
                    try {
                        val uri = saveBitmapToGallery(context, bitmap)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (uri != null) "Ticket enregistré 🎉" else "Échec ❌"
                            )
                        }
                    } catch (e: Exception) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Erreur ❌")
                        }
                    } finally {
                        exportRequested = false
                        isLoading = false
                    }
                }
            )
        }
    }
}
