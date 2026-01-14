package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.janeirohurley.gevent.ui.components.CreateEventContent
import com.janeirohurley.gevent.ui.components.EventStatusTabs
import com.janeirohurley.gevent.ui.components.ScanQRContent
import com.janeirohurley.gevent.ui.components.MyEventsContent
import com.janeirohurley.gevent.viewmodel.OrganizerViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEventsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: OrganizerViewModel = viewModel()
) {
    val tabs = listOf("Événements", "Scanner QR", "Créer")
    var selectedTab by remember { mutableStateOf("Événements") }
    val snackbarHostState = remember { SnackbarHostState() }

    // Charger les événements au démarrage
    LaunchedEffect(Unit) {
        viewModel.loadMyEvents()
    }

    // Observer les états
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()

    // Afficher les messages de succès
    LaunchedEffect(operationSuccess) {
        operationSuccess?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearOperationSuccess()
        }
    }

    // Afficher les messages d'erreur
    LaunchedEffect(error) {
        error?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gestion d'Événements",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // Tabs
            EventStatusTabs(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(Modifier.height(20.dp))

            // Contenu selon le tab sélectionné
            when (selectedTab) {
                "Événements" -> MyEventsContent(
                    events = events,
                    isLoading = isLoading,
                    error = error,
                    onRefresh = { viewModel.loadMyEvents() },
                    onCancelEvent = { eventId -> viewModel.cancelEvent(eventId) },
                    onCompleteEvent = { eventId -> viewModel.completeEvent(eventId) },
                    onEditEvent = { eventId -> navController.navigate("create_event/$eventId") },
                    onDeleteEvent = { eventId -> viewModel.deleteEvent(eventId) },
                    onChangeStatus = { eventId, status -> viewModel.changeEventStatus(eventId, status) }
                )
                "Scanner QR" -> ScanQRContent(
                    navController = navController
                )
                "Créer" -> CreateEventContent(navController = navController)
            }
        }
    }
}

