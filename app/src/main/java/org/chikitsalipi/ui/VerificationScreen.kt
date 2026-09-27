package org.chikitsalipi.ui

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.chikitsalipi.data.GsonProvider
import org.chikitsalipi.model.*
import org.chikitsalipi.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    record: HealthRecord,
    selectedLanguage: AppLanguage,
    onSaveRecord: (HealthRecord) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isSaved by remember { mutableStateOf(false) }

    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
            }
        }
        ttsEngine = tts
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    val extractedFields = remember(record.fieldsJson) {
        try {
            if (record.fieldsJson.isNotBlank()) {
                val itemType = object : com.google.gson.reflect.TypeToken<List<ExtractedField>>() {}.type
                GsonProvider.gson.fromJson<List<ExtractedField>>(record.fieldsJson, itemType) ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Human Verification & Multilingual Review", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ForestTeal),
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("< Back", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Dual Pane Header
            Text(
                text = "Record ID: ${record.recordId} | Category: ${record.category.name}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ForestTeal
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dual Pane Container
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Left Pane: Document Region & OCR
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(end = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Original Document Source Info", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestTeal)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(WarmOffWhite)
                                .border(1.dp, SageSlate, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Path: ${record.imagePath}", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(8.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Raw Recognized Text (ML Kit OCR):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(record.rawOcrText, fontSize = 12.sp, color = Color.DarkGray)
                    }
                }

                // Right Pane: Extracted Structured Cards & Multilingual Translation
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Extracted Entities & Translation", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestTeal)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (extractedFields.isEmpty()) {
                            Text("No structured fields extracted from text.", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(extractedFields) { field ->
                                    val bnTrans = field.translations.find { it.languageCode == "bn" }?.translatedText ?: "N/A"
                                    val hiTrans = field.translations.find { it.languageCode == "hi" }?.translatedText ?: "N/A"
                                    ExtractedFieldCard(
                                        field = field.fieldName,
                                        value = "${field.extractedValue} ${field.unit ?: ""}".trim(),
                                        confidence = "${(field.confidenceScore * 100).toInt()}% (${field.status.name})",
                                        translationBn = bnTrans,
                                        translationHi = hiTrans,
                                        statusColor = if (field.confidenceScore >= 0.8f) EmeraldHighConfidence else AmberUncertainty
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Verification Actions & Local Storage Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (isTtsReady && ttsEngine != null) {
                            val locale = when (selectedLanguage) {
                                AppLanguage.BENGALI -> Locale("bn", "IN")
                                AppLanguage.HINDI -> Locale("hi", "IN")
                                AppLanguage.ENGLISH -> Locale.US
                            }
                            ttsEngine?.language = locale
                            ttsEngine?.speak(record.rawOcrText, TextToSpeech.QUEUE_FLUSH, null, "RecordTTS")
                        } else {
                            Toast.makeText(context, "TTS Engine Initializing...", Toast.LENGTH_SHORT).show()
                        }
                    },
                    border = androidx.compose.foundation.BorderStroke(1.dp, ForestTeal)
                ) {
                    Text("🔊 TTS Narration", color = ForestTeal, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        isSaved = true
                        onSaveRecord(record)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestTeal)
                ) {
                    Text(if (isSaved) "SAVED TO LOCAL DB ✓" else "SAVE TO LOCAL STORAGE")
                }
            }
        }
    }
}

@Composable
fun ExtractedFieldCard(
    field: String,
    value: String,
    confidence: String,
    translationBn: String,
    translationHi: String,
    statusColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WarmOffWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(field, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(confidence, fontSize = 10.sp, color = statusColor, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Value: $value", fontSize = 12.sp, color = Color.Black)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
            Text("Bengali: $translationBn", fontSize = 11.sp, color = Color.DarkGray)
            Text("Hindi: $translationHi", fontSize = 11.sp, color = Color.DarkGray)
        }
    }
}
