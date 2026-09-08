package org.chikitsalipi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.chikitsalipi.model.*
import org.chikitsalipi.ui.theme.EmeraldHighConfidence
import org.chikitsalipi.ui.theme.ForestTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureScreen(
    selectedLanguage: AppLanguage,
    onRecordCaptured: (HealthRecord) -> Unit,
    onBack: () -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }

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
            // Simulated Camera Framing Guide
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
                            text = "Performing On-Device OCR & Medical Field Extraction...",
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
                    Text("Illumination: 350 Lux (OK)", color = Color.Green, fontSize = 12.sp)
                    Text("Blur: Low", color = Color.Green, fontSize = 12.sp)
                    Text("Tilt: 2°", color = Color.Green, fontSize = 12.sp)
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
                        onClick = {
                            isProcessing = true
                            // Generate sample extracted health record
                            val sampleFields = listOf(
                                ExtractedField(
                                    fieldId = "f1",
                                    categoryName = "Medication",
                                    fieldName = "Paracetamol",
                                    extractedValue = "500",
                                    unit = "mg",
                                    confidenceScore = 0.94f,
                                    sourceText = "Paracetamol 500mg BD",
                                    sourceBoundingBox = BoundingBoxInfo(100, 200, 400, 250),
                                    status = VerificationStatus.HIGH_CONFIDENCE,
                                    translations = listOf(
                                        TranslationPair("bn", "প্যারাসিটামল ৫০০ মিগ্রা"),
                                        TranslationPair("hi", "पैरासिटामोल 500 मिग्रा")
                                    )
                                ),
                                ExtractedField(
                                    fieldId = "f2",
                                    categoryName = "Lab Result",
                                    fieldName = "Hemoglobin",
                                    extractedValue = "12.5",
                                    unit = "g/dL",
                                    confidenceScore = 0.65f,
                                    sourceText = "Hb: 12.5 g/dL",
                                    sourceBoundingBox = BoundingBoxInfo(100, 300, 350, 350),
                                    status = VerificationStatus.NEEDS_REVIEW,
                                    translations = listOf(
                                        TranslationPair("bn", "হিমোগ্লোবিন: ১২.৫ গ্রাম/ডেসিলিটার"),
                                        TranslationPair("hi", "हीमोग्लोबिन: 12.5 ग्राम/डेसिलीटर")
                                    )
                                )
                            )

                            val record = HealthRecord(
                                recordId = "REC_${System.currentTimeMillis()}",
                                timestamp = System.currentTimeMillis(),
                                category = DocumentCategory.PRESCRIPTION,
                                imagePath = "/sdcard/ChikitsaLipi/sample.jpg",
                                rawOcrText = "Rx\nParacetamol 500mg BD\nHb: 12.5 g/dL",
                                ocrConfidence = 0.85f,
                                isHandwritten = false,
                                fieldsJson = "",
                                isFullyVerified = false,
                                processingTimeMs = 420
                            )

                            onRecordCaptured(record)
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
