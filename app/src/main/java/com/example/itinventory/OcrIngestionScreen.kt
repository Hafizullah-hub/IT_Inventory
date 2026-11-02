package com.example.itinventory

import com.example.itinventory.ui.theme.*

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors

val OcrGreen = Color(0xFF00BFA5)
val SurfaceLight = Color(0xFFF8FAFC)

@Composable
fun OcrIngestionScreen(
    onBackClick: () -> Unit = {},
    onProceed: (assetNo: String, serial: String, capex: String, date: String) -> Unit = {_,_,_,_ ->}
) {
    var isScanned by remember { mutableStateOf(false) }
    var scannedAssetNo by remember { mutableStateOf("") }
    var scannedSerial by remember { mutableStateOf("") }
    var scannedCapex by remember { mutableStateOf("") }
    var scannedDate by remember { mutableStateOf("") }

    Scaffold(
        topBar = { OcrTopBar(onBackClick) },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Camera Area (Top Half)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF2C3E50)) // Placeholder for camera feed
            ) {
                // OCR Overlay UI
                OcrCameraOverlay(
                    isScanned = isScanned,
                    onScanSuccess = { asset, serial, capex, date ->
                        scannedAssetNo = asset
                        scannedSerial = serial
                        scannedCapex = capex
                        scannedDate = date
                        isScanned = true
                    }
                )
            }

            // Bottom Sheet Area (Bottom Half)
            if (isScanned) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        // Drag Handle
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(2.dp))
                                .align(Alignment.CenterHorizontally)
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Extracted Metadata",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Live Sync", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Config", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Extracted Data Cards
                        ExtractedDataCard(
                            label = "CAPEX NO",
                            value = scannedCapex.ifEmpty { "110/14/IT" },
                            subValue = "",
                            icon = Icons.Outlined.ReceiptLong
                        )
                        
                        ExtractedDataCard(
                            label = "ASSET NO",
                            value = scannedAssetNo.ifEmpty { "1001946" },
                            isPrimaryKey = true,
                            icon = Icons.Outlined.Numbers
                        )
                        
                        ExtractedDataCard(
                            label = "SERIAL NUMBER",
                            value = scannedSerial.ifEmpty { "CNC037QRD(1/2)" },
                            icon = Icons.Outlined.Pin
                        )
                        
                        ExtractedDataCard(
                            label = "DATE OF PURCHASE",
                            value = scannedDate.ifEmpty { "01/10/14" },
                            subValue = "",
                            icon = Icons.Outlined.CalendarToday
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Action Buttons
                        Button(
                            onClick = { onProceed(scannedAssetNo, scannedSerial, scannedCapex, scannedDate) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Proceed to New Asset", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { /*TODO*/ },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Outlined.Edit, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Manual Correction", fontWeight = FontWeight.Medium)
                            }
                            
                            Button(
                                onClick = { isScanned = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Outlined.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Retake", fontWeight = FontWeight.Medium)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else {
                 Box(
                     modifier = Modifier
                         .fillMaxWidth()
                         .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                         .padding(24.dp),
                     contentAlignment = Alignment.Center
                 ) {
                     Text("Align asset tag within frame to scan...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
                 }
            }
        }
    }
}

@Composable
fun OcrTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = "Scan Asset Tag",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun OcrCameraOverlay(isScanned: Boolean, onScanSuccess: (String, String, String, String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            
                        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(ctx), OcrAnalyzer { text ->
                            if (isScanned) return@OcrAnalyzer
                            
                            val assetMatch = Regex("""ASSET NO:\s*(.+)""").find(text)
                            val serialMatch = Regex("""SERIAL NUMBER:\s*(.+)""").find(text)
                            val capexMatch = Regex("""CAPEX NO:\s*(.+)""").find(text)
                            val dateMatch = Regex("""DATE OF PURCHASE:\s*(.+)""").find(text)
                            
                            if (assetMatch != null && serialMatch != null) {
                                val assetNo = assetMatch.groupValues[1]
                                val serial = serialMatch.groupValues[1]
                                val capex = capexMatch?.groupValues?.get(1) ?: ""
                                val date = dateMatch?.groupValues?.get(1) ?: ""
                                onScanSuccess(assetNo, serial, capex, date)
                            }
                        })

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (exc: Exception) {
                            // Handle exceptions
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text("Camera permission required", color = Color.White)
            }
        }
        
        // Top Action Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.FlashOn, contentDescription = "Flash", tint = Color.White)
            
            Surface(
                color = Color(0x66000000),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(OcrGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("OCR CONF. 98.6%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Surface(
                color = Color(0x66000000),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Manual", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // Viewfinder Brackets
        Canvas(modifier = Modifier.fillMaxSize()) {
            val bracketLength = 40.dp.toPx()
            val strokeWidth = 4.dp.toPx()
            val padding = 40.dp.toPx()
            
            // Top Left
            drawLine(Color.White, Offset(padding, padding), Offset(padding + bracketLength, padding), strokeWidth = strokeWidth, cap = StrokeCap.Round)
            drawLine(Color.White, Offset(padding, padding), Offset(padding, padding + bracketLength), strokeWidth = strokeWidth, cap = StrokeCap.Round)
            
            // Top Right
            drawLine(Color.White, Offset(size.width - padding, padding), Offset(size.width - padding - bracketLength, padding), strokeWidth = strokeWidth, cap = StrokeCap.Round)
            drawLine(Color.White, Offset(size.width - padding, padding), Offset(size.width - padding, padding + bracketLength), strokeWidth = strokeWidth, cap = StrokeCap.Round)
            
            // Bottom Left
            drawLine(Color.White, Offset(padding, size.height - padding - 80.dp.toPx()), Offset(padding + bracketLength, size.height - padding - 80.dp.toPx()), strokeWidth = strokeWidth, cap = StrokeCap.Round)
            drawLine(Color.White, Offset(padding, size.height - padding - 80.dp.toPx()), Offset(padding, size.height - padding - bracketLength - 80.dp.toPx()), strokeWidth = strokeWidth, cap = StrokeCap.Round)
            
            // Bottom Right
            drawLine(Color.White, Offset(size.width - padding, size.height - padding - 80.dp.toPx()), Offset(size.width - padding - bracketLength, size.height - padding - 80.dp.toPx()), strokeWidth = strokeWidth, cap = StrokeCap.Round)
            drawLine(Color.White, Offset(size.width - padding, size.height - padding - 80.dp.toPx()), Offset(size.width - padding, size.height - padding - bracketLength - 80.dp.toPx()), strokeWidth = strokeWidth, cap = StrokeCap.Round)
        }
    }
}

@Composable
fun ExtractedDataCard(
    label: String,
    value: String,
    subValue: String = "",
    isPrimaryKey: Boolean = false,
    icon: ImageVector
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    if (isPrimaryKey) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "PRIMARY KEY",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.DocumentScanner, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("OCR Scanned", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = value,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (subValue.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = subValue,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
class OcrAnalyzer(
    private val onTextRecognized: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    onTextRecognized(visionText.text)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}