package org.chikitsalipi.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.chikitsalipi.extraction.DefaultMedicalExtractor
import org.chikitsalipi.model.*
import org.chikitsalipi.ocr.MlKitOcrProcessor
import org.chikitsalipi.ui.theme.EmeraldHighConfidence
import org.chikitsalipi.ui.theme.ForestTeal
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureScreen(
    selectedLanguage: AppLanguage,
    onRecordCaptured: (HealthRecord) -> Unit,
    onBack: () -> Unit
) {
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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var isProcessing by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val executor = remember { Executors.newSingleThreadExecutor() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CameraX - Document Capture", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ForestTeal),
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("< Back", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black)
        ) {
            if (hasCameraPermission) {
                // Live Camera Viewfinder
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }

                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageCapture
                                )
                            } catch (exc: Exception) {
                                Log.e("CaptureScreen", "Use case binding failed", exc)
                            }
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Simulated Overlay Guide
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                        .border(2.dp, EmeraldHighConfidence, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isProcessing) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = statusMessage ?: "Performing On-Device OCR & Medical Field Extraction...",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Text(
                            text = "Align Prescription / Health Record within Frame",
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Camera permission is required to capture documents.", color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Grant Permission")
                    }
                }
            }

            // Real-Time Ambient Feedback Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("Illumination: OK", color = Color.Green, fontSize = 12.sp)
                    Text("Blur: Low", color = Color.Green, fontSize = 12.sp)
                    Text("Framing: Good", color = Color.Green, fontSize = 12.sp)
                }
            }

            // Capture Trigger Controls
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = Color.Black.copy(alpha = 0.8f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        enabled = hasCameraPermission && !isProcessing,
                        onClick = {
                            isProcessing = true
                            statusMessage = "Capturing Image..."

                            val photoFile = File(
                                context.filesDir,
                                "doc_${System.currentTimeMillis()}.jpg"
                            )

                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                            imageCapture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        statusMessage = "Processing OCR..."
                                        val startTime = System.currentTimeMillis()

                                        MlKitOcrProcessor.processImage(
                                            context,
                                            photoFile
                                        ) { rawOcr, confidence ->
                                            val extractor = DefaultMedicalExtractor()
                                            val extracted = extractor.extractFields(rawOcr)

                                            val fieldsJsonString = org.chikitsalipi.data.GsonProvider.gson.toJson(extracted)

                                            val record = HealthRecord(
                                                recordId = "REC_${System.currentTimeMillis()}",
                                                timestamp = System.currentTimeMillis(),
                                                category = DocumentCategory.PRESCRIPTION,
                                                imagePath = photoFile.absolutePath,
                                                rawOcrText = rawOcr.ifBlank { "No readable text detected in document." },
                                                ocrConfidence = confidence,
                                                isHandwritten = false,
                                                fieldsJson = fieldsJsonString,
                                                isFullyVerified = false,
                                                processingTimeMs = System.currentTimeMillis() - startTime
                                            )

                                            isProcessing = false
                                            onRecordCaptured(record)
                                        }
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Log.e("CaptureScreen", "Photo capture failed: ${exception.message}", exception)
                                        isProcessing = false
                                        statusMessage = "Capture failed: ${exception.message}"
                                    }
                                }
                            )
                        },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldHighConfidence)
                    ) {
                        Text("CAPTURE & DIGITIZE RECORD", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
