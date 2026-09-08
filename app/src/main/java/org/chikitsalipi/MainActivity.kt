package org.chikitsalipi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.chikitsalipi.model.HealthRecord
import org.chikitsalipi.ui.*
import org.chikitsalipi.ui.theme.ChikitsaLipiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChikitsaLipiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChikitsaLipiApp()
                }
            }
        }
    }
}

@Composable
fun ChikitsaLipiApp() {
    val navState = remember { AppNavigationState() }
    val savedRecordsList = remember { mutableStateListOf<HealthRecord>() }

    when (navState.currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                selectedLanguage = navState.selectedLanguage,
                onLanguageSelected = { navState.selectedLanguage = it },
                onNavigateToCapture = { navState.currentScreen = Screen.CAPTURE },
                onNavigateToDirectory = { navState.currentScreen = Screen.DIRECTORY }
            )
        }
        Screen.CAPTURE -> {
            CaptureScreen(
                selectedLanguage = navState.selectedLanguage,
                onRecordCaptured = { record ->
                    navState.activeRecord = record
                    navState.currentScreen = Screen.VERIFICATION
                },
                onBack = { navState.currentScreen = Screen.HOME }
            )
        }
        Screen.VERIFICATION -> {
            navState.activeRecord?.let { record ->
                VerificationScreen(
                    record = record,
                    selectedLanguage = navState.selectedLanguage,
                    onSaveRecord = { rec ->
                        if (!savedRecordsList.any { it.recordId == rec.recordId }) {
                            savedRecordsList.add(rec)
                        }
                    },
                    onBack = { navState.currentScreen = Screen.HOME }
                )
            }
        }
        Screen.DIRECTORY -> {
            DirectoryScreen(
                savedRecords = savedRecordsList,
                selectedLanguage = navState.selectedLanguage,
                onBack = { navState.currentScreen = Screen.HOME }
            )
        }
    }
}
