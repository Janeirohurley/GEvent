package com.janeirohurley.gevent.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.janeirohurley.gevent.viewmodel.OrganizerViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(
    navController: NavHostController,
    viewModel: OrganizerViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    var isScanning by remember { mutableStateOf(true) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    var detectedQRCode by remember { mutableStateOf<String?>(null) }
    var showConfirmation by remember { mutableStateOf(false) }
    val scanResult by viewModel.scanResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Launcher pour demander la permission caméra
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Vérifier et demander la permission au démarrage
    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                hasCameraPermission = true
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // Observer le résultat du scan
    LaunchedEffect(scanResult) {
        scanResult?.let {
            isScanning = false
            showConfirmation = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scanner QR Code") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!hasCameraPermission) {
                // Écran de demande de permission
                CameraPermissionContent(
                    onRequestPermission = {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    onClose = { navController.navigateUp() }
                )
            } else if (isScanning) {
                // Vue caméra
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            
                            val imageAnalyzer = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also {
                                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                                        if (!showConfirmation && !isLoading) {
                                            processImageProxy(imageProxy) { qrCode ->
                                                detectedQRCode = qrCode
                                                showConfirmation = true
                                            }
                                        } else {
                                            imageProxy.close()
                                        }
                                    }
                                }
                            
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageAnalyzer
                                )
                            } catch (exc: Exception) {
                                // Gérer l'erreur
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Cadre de scan overlay
                QRScanFrame()
                
                // Confirmation de validation
                if (showConfirmation && detectedQRCode != null) {
                    QRConfirmationDialog(
                        qrCode = detectedQRCode!!,
                        onConfirm = {
                            viewModel.validateTicket(detectedQRCode!!)
                            showConfirmation = false
                        },
                        onCancel = {
                            detectedQRCode = null
                            showConfirmation = false
                        }
                    )
                }
                
                // Overlay avec instructions
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.7f)
                        )
                    ) {
                        Text(
                            if (showConfirmation) "QR Code détecté ! Confirmez pour valider" else "Placez le QR code dans le cadre",
                            modifier = Modifier.padding(16.dp),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    if (isLoading) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Black.copy(alpha = 0.7f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Validation en cours...",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                // Résultat du scan
                scanResult?.let { result ->
                    ScanResultContent(
                        result = result,
                        onScanAgain = { 
                            isScanning = true
                            detectedQRCode = null
                            showConfirmation = false
                            viewModel.clearScanResult()
                        },
                        onClose = { navController.navigateUp() }
                    )
                }
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

@Composable
private fun QRScanFrame() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        // Taille du cadre (carré)
        val frameSize = minOf(canvasWidth, canvasHeight) * 0.6f
        val frameLeft = (canvasWidth - frameSize) / 2
        val frameTop = (canvasHeight - frameSize) / 2
        
        // Dessiner l'overlay sombre avec trou transparent
        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            topLeft = Offset(0f, 0f),
            size = Size(canvasWidth, frameTop)
        )
        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            topLeft = Offset(0f, frameTop + frameSize),
            size = Size(canvasWidth, canvasHeight - frameTop - frameSize)
        )
        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            topLeft = Offset(0f, frameTop),
            size = Size(frameLeft, frameSize)
        )
        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            topLeft = Offset(frameLeft + frameSize, frameTop),
            size = Size(canvasWidth - frameLeft - frameSize, frameSize)
        )
        
        // Dessiner les coins du cadre
        val cornerLength = 40f
        val cornerStroke = 4f
        val cornerColor = Color.White
        
        // Coin haut-gauche
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft, frameTop + cornerLength),
            end = Offset(frameLeft, frameTop),
            strokeWidth = cornerStroke
        )
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft, frameTop),
            end = Offset(frameLeft + cornerLength, frameTop),
            strokeWidth = cornerStroke
        )
        
        // Coin haut-droit
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft + frameSize - cornerLength, frameTop),
            end = Offset(frameLeft + frameSize, frameTop),
            strokeWidth = cornerStroke
        )
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft + frameSize, frameTop),
            end = Offset(frameLeft + frameSize, frameTop + cornerLength),
            strokeWidth = cornerStroke
        )
        
        // Coin bas-gauche
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft, frameTop + frameSize - cornerLength),
            end = Offset(frameLeft, frameTop + frameSize),
            strokeWidth = cornerStroke
        )
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft, frameTop + frameSize),
            end = Offset(frameLeft + cornerLength, frameTop + frameSize),
            strokeWidth = cornerStroke
        )
        
        // Coin bas-droit
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft + frameSize - cornerLength, frameTop + frameSize),
            end = Offset(frameLeft + frameSize, frameTop + frameSize),
            strokeWidth = cornerStroke
        )
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft + frameSize, frameTop + frameSize - cornerLength),
            end = Offset(frameLeft + frameSize, frameTop + frameSize),
            strokeWidth = cornerStroke
        )
    }
}

@Composable
private fun ScanResultContent(
    result: TicketValidationResult,
    onScanAgain: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = if (result.isValid) {
                        listOf(
                            Color(0xFF4CAF50).copy(alpha = 0.1f),
                            Color(0xFF2E7D32).copy(alpha = 0.05f)
                        )
                    } else {
                        listOf(
                            Color(0xFFF44336).copy(alpha = 0.1f),
                            Color(0xFFD32F2F).copy(alpha = 0.05f)
                        )
                    }
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icône de statut
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = if (result.isValid) Color(0xFF4CAF50) else Color(0xFFF44336),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (result.isValid) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = if (result.isValid) "Succès" else "Erreur",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
            
            Spacer(Modifier.height(32.dp))
            
            // Titre principal
            Text(
                text = if (result.isValid) "Ticket Validé" else "Validation Échouée",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (result.isValid) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Message
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = result.message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp
                    )
                    
                    if (result.isValid && result.ticketInfo != null) {
                        Spacer(Modifier.height(24.dp))
                        
                        androidx.compose.material3.HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Text(
                            text = "Détails du Ticket",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        TicketInfoRow("Événement", result.ticketInfo.eventTitle)
                        TicketInfoRow("Participant", result.ticketInfo.participantName)
                        TicketInfoRow("Type", result.ticketInfo.ticketType)
                    }
                }
            }
            
            Spacer(Modifier.height(40.dp))
            
            // Boutons d'action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, 
                        MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text(
                        "Fermer",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                        
                    )
                }
                
                Button(
                    onClick = onScanAgain,
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (result.isValid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "Scanner Encore",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TicketInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f, false)
        )
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    onQRCodeDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()
        
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    when (barcode.valueType) {
                        Barcode.TYPE_TEXT, Barcode.TYPE_URL -> {
                            barcode.rawValue?.let { qrCode ->
                                onQRCodeDetected(qrCode)
                            }
                        }
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

// Modèles de données pour le résultat du scan
data class TicketValidationResult(
    val isValid: Boolean,
    val message: String,
    val ticketInfo: TicketInfo? = null
)

data class TicketInfo(
    val eventTitle: String,
    val participantName: String,
    val ticketType: String
)

@Composable
private fun CameraPermissionContent(
    onRequestPermission: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "📷 Permission Caméra",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(Modifier.height(16.dp))
                
                Text(
                    "Cette application a besoin d'accéder à votre caméra pour scanner les QR codes des tickets.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(24.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onClose,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler")
                    }
                    
                    Button(
                        onClick = onRequestPermission,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Autoriser")
                    }
                }
            }
        }
    }
}

@Composable
private fun QRConfirmationDialog(
    qrCode: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "📱 QR Code Détecté",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(Modifier.height(16.dp))
                
                Text(
                    "Voulez-vous valider ce ticket ?",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(8.dp))
                
                Text(
                    qrCode.take(20) + if (qrCode.length > 20) "..." else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(24.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler")
                    }
                    
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Valider")
                    }
                }
            }
        }
    }
}