package org.chikitsalipi.ui

import android.widget.Toast
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
import org.chikitsalipi.export.JsonExporter
import org.chikitsalipi.export.PdfExporter
import org.chikitsalipi.model.HealthRecord
import org.chikitsalipi.ui.theme.ForestTeal
import org.chikitsalipi.ui.theme.WarmOffWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(
    savedRecords: List<HealthRecord>,
    selectedLanguage: AppLanguage,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredRecords = remember(savedRecords, searchQuery) {
        if (searchQuery.isBlank()) {
            savedRecords
        } else {
            savedRecords.filter { rec ->
                rec.recordId.contains(searchQuery, ignoreCase = true) ||
                        rec.category.name.contains(searchQuery, ignoreCase = true) ||
                        rec.rawOcrText.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Health Records & Directory", color = Color.White) },
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search records by ID, category, or text...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (savedRecords.isEmpty())
                            "No saved records found in local database.\nDigitize a record from the Home screen."
                        else "No records match search: \"$searchQuery\"",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredRecords) { rec ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = WarmOffWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ForestTeal.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(rec.recordId, fontWeight = FontWeight.Bold, color = ForestTeal)
                                    Text("Category: ${rec.category.name}", fontSize = 11.sp, color = Color.DarkGray)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(rec.rawOcrText, fontSize = 12.sp, maxLines = 2)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = {
                                        val jsonFile = JsonExporter.exportRecordToJson(context, rec)
                                        val pdfFile = PdfExporter.exportRecordToPdf(context, rec)
                                        if (jsonFile != null && pdfFile != null) {
                                            Toast.makeText(context, "Exported JSON & PDF successfully", Toast.LENGTH_SHORT).show()
                                        }
                                    }) {
                                        Text("EXPORT RECORD (JSON/PDF)", fontSize = 11.sp, color = ForestTeal)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
