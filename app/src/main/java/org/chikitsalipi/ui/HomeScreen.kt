package org.chikitsalipi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.chikitsalipi.ui.theme.ForestTeal
import org.chikitsalipi.ui.theme.SageSlate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onNavigateToCapture: () -> Unit,
    onNavigateToDirectory: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ChikitsaLipi", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ForestTeal),
                actions = {
                    Row(
                        modifier = Modifier.padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppLanguage.values().forEach { lang ->
                            FilterChip(
                                selected = selectedLanguage == lang,
                                onClick = { onLanguageSelected(lang) },
                                label = { Text(lang.displayName, fontSize = 12.sp) },
                                modifier = Modifier.padding(horizontal = 2.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SageSlate,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(16.dp),
                color = ForestTeal.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ForestTeal.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (selectedLanguage) {
                            AppLanguage.ENGLISH -> "OCR & Medical Health Record Preservation"
                            AppLanguage.BENGALI -> "ওসিআর এবং চিকিৎসা সংক্রান্ত রেকর্ড ডিজিটাল সংরক্ষণ"
                            AppLanguage.HINDI -> "ओसीआर और चिकित्सा स्वास्थ्य रिकॉर्ड डिजिटल संरक्षण"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestTeal,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (selectedLanguage) {
                            AppLanguage.ENGLISH -> "On-device OCR, structured entity extraction, and multilingual translation."
                            AppLanguage.BENGALI -> "ডিভাইসেই ওসিআর, স্ট্রাকচার্ড তথ্য এক্সট্রাকশন এবং বহুভাষিক অনুবাদ।"
                            AppLanguage.HINDI -> "डिवाइस पर ओसीआर, संरचित जानकारी निष्कर्षण और बहुभाषी अनुवाद।"
                        },
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Button(
                onClick = onNavigateToCapture,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestTeal)
            ) {
                Text(
                    text = when (selectedLanguage) {
                        AppLanguage.ENGLISH -> "DIGITIZE NEW HEALTH RECORD"
                        AppLanguage.BENGALI -> "নতুন স্বাস্থ্য রেকর্ড ডিজিটাইজ করুন"
                        AppLanguage.HINDI -> "नया स्वास्थ्य रिकॉर्ड डिजिटाइज़ करें"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onNavigateToDirectory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ForestTeal)
            ) {
                Text(
                    text = when (selectedLanguage) {
                        AppLanguage.ENGLISH -> "SAVED RECORDS & DIRECTORY"
                        AppLanguage.BENGALI -> "সংরক্ষিত রেকর্ড এবং নির্দেশিকা"
                        AppLanguage.HINDI -> "सहेजे गए रिकॉर्ड और निर्देशिका"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestTeal
                )
            }
        }
    }
}
