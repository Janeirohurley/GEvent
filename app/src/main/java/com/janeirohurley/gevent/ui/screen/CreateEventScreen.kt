package com.janeirohurley.gevent.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.janeirohurley.gevent.R
import com.janeirohurley.gevent.data.model.CreateEventRequest
import com.janeirohurley.gevent.viewmodel.OrganizerViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.janeirohurley.gevent.data.model.Category
import com.janeirohurley.gevent.ui.components.CustomInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: OrganizerViewModel = viewModel()
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Int?>(null) }
    var isFree by remember { mutableStateOf(false) }
    var price by remember { mutableStateOf("") }
    var totalCapacity by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }

    // Date pickers states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Image picker launcher (doit être déclaré avant permissionLauncher)
    val imagePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        selectedImageUri = uri
    }

    // Permission launcher
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        }
    }

    // Categories list - chargées depuis le backend
    val categories = remember { mutableStateOf<List<Category>>(emptyList()) }
    var expandedCategory by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observer les états
    val isLoading by viewModel.isLoading.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()
    val error by viewModel.error.collectAsState()

    // Afficher les messages de succès et retourner
    LaunchedEffect(operationSuccess) {
        operationSuccess?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearOperationSuccess()
            navController.popBackStack()
        }
    }

    // Charger les catégories au démarrage
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }
    
    // Observer les catégories du ViewModel
    LaunchedEffect(viewModel.categories.collectAsState().value) {
        categories.value = viewModel.categories.value
    }

    // Afficher les erreurs
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
                        "Créer un Événement",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.fi_rr_arrow_left),
                            contentDescription = "Retour",
                            modifier = Modifier.size(18.dp)

                        )
                    }
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
                .verticalScroll(scrollState)
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Titre
            CustomInput(
                value = title,
                onValueChange = { title = it },
                placeholder = "Titre de l'événement",
                height = 50.dp
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 12.sp
                ),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                )

            )

            // Sélection d'image
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (selectedImageUri != null) {
                        // Afficher l'image sélectionnée
                        val bitmap = remember(selectedImageUri) {
                            context.contentResolver.openInputStream(selectedImageUri!!)?.use { stream ->
                                android.graphics.BitmapFactory.decodeStream(stream)
                            }
                        }

                        bitmap?.let {
                            androidx.compose.foundation.Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Image de l'événement",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        // Icône placeholder
                        Icon(
                            painter = painterResource(R.drawable.image_up),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    }

                    Button(
                        onClick = {
                            // Vérifier la version d'Android
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                // Android 13+ : demander READ_MEDIA_IMAGES
                                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                            } else {
                                // Android 12 et inférieur : demander READ_EXTERNAL_STORAGE
                                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                if (selectedImageUri != null) R.drawable.image_up
                                else R.drawable.image_up
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (selectedImageUri != null) "Changer l'image"
                            else "Sélectionner une image",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Catégorie (Dropdown)
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {

                CustomInput(
                    modifier = Modifier.menuAnchor(),
                    value = categories.value.find { it.id == selectedCategory }?.name ?: "",
                    onValueChange = {},
                    placeholder = "Catégorie",
                    height = 50.dp,
                    suffix = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                    }
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.value.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category.id
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            // Location

            CustomInput(
                value = location,
                onValueChange = {location = it},
                placeholder = "Ex: Bujumbura, Burundi",
                height = 50.dp,
                prefix = {
                    Icon(
                        painter = painterResource(R.drawable.fi_rr_marker),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                        contentDescription = null)
                }
            )

            // Date de début
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showStartDatePicker = true }
            ) {
                CustomInput(
                    value = date,
                    onValueChange = {},
                    placeholder = "Sélectionner la date et l'heure",
                    height = 50.dp,
                    prefix = {
                        Icon(
                            painter = painterResource(R.drawable.fi_rr_calendar),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }


            // Date de fin (optionnelle)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEndDatePicker = true }
            ) {
                CustomInput(
                    value = endDate,
                    onValueChange = {},
                    placeholder = "Sélectionner la date et l'heure (optionnel)",
                    height = 50.dp,
                    prefix = {
                        Icon(
                            painter = painterResource(R.drawable.fi_rr_calendar),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }


            CustomInput(
                value = duration,
                onValueChange = { duration = it },
                placeholder = "Durée (optionnelle)",
                height = 50.dp,
            )

            // Capacité totale
            CustomInput(
                value = totalCapacity,
                onValueChange = { totalCapacity = it },
                placeholder = "Capacité totale",
            )

            // Switch événement gratuit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Événement gratuit",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 14.sp
                )
                Switch(
                    checked = isFree,
                    onCheckedChange = { isFree = it },
                    modifier = Modifier.height(10.dp)
                )
            }

            // Prix (si payant)
            if (!isFree) {


                CustomInput(
                    value = price,
                    onValueChange = { price = it },
                    placeholder = "Prix en Fbu",
                    height = 50.dp,
                )
            }

            Spacer(Modifier.height(8.dp))

            // Bouton de création
            Button(
                onClick = {
                    val request = CreateEventRequest(
                        title = title,
                        description = description.ifBlank { null },
                        categoryId = selectedCategory ?: 1, // Catégorie par défaut si non sélectionnée
                        location = location.ifBlank { null },
                        latitude = null, // Le backend gérera la géolocalisation
                        longitude = null, // Le backend gérera la géolocalisation
                        date = date,
                        endDate = endDate.ifBlank { null },
                        duration = duration.ifBlank { null },
                        isFree = isFree,
                        price = if (!isFree && price.isNotBlank()) price else null,
                        tvaRate = "18.00", // Taux TVA par défaut du Burundi
                        totalCapacity = totalCapacity.toIntOrNull() ?: 0,
                        organizerName = null // Le backend utilisera l'utilisateur connecté
                    )

                    selectedImageUri?.let { uri ->
                        viewModel.createEventWithImage(request, uri, context)
                    } ?: run {
                        viewModel.createEvent(request)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && title.isNotBlank() && date.isNotBlank() &&
                         totalCapacity.isNotBlank() && selectedCategory != null
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.fi_rr_plus),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Créer l'événement",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // Date Pickers
        if (showStartDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                onDateSelected = { selectedDate ->
                    showStartDatePicker = false
                    showStartTimePicker = true
                    date = selectedDate
                }
            )
        }

        if (showStartTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showStartTimePicker = false },
                onTimeSelected = { selectedTime ->
                    showStartTimePicker = false
                    date = "$date $selectedTime"
                }
            )
        }

        if (showEndDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                onDateSelected = { selectedDate ->
                    showEndDatePicker = false
                    showEndTimePicker = true
                    endDate = selectedDate
                }
            )
        }

        if (showEndTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showEndTimePicker = false },
                onTimeSelected = { selectedTime ->
                    showEndTimePicker = false
                    endDate = "$endDate $selectedTime"
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = java.util.Calendar.getInstance().apply {
                            timeInMillis = millis
                        }
                        val dateStr = String.format(
                            "%04d-%02d-%02d",
                            calendar.get(java.util.Calendar.YEAR),
                            calendar.get(java.util.Calendar.MONTH) + 1,
                            calendar.get(java.util.Calendar.DAY_OF_MONTH)
                        )
                        onDateSelected(dateStr)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Annuler")
            }
        }
    ) {
        androidx.compose.material3.DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val timePickerState = rememberTimePickerState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    val timeStr = String.format(
                        "%02d:%02d:00",
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    onTimeSelected(timeStr)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Annuler")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}
