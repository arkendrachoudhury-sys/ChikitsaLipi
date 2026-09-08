package org.chikitsalipi.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.chikitsalipi.model.HealthRecord

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    BENGALI("bn", "বাংলা"),
    HINDI("hi", "हिन्दी")
}

enum class Screen {
    HOME,
    CAPTURE,
    VERIFICATION,
    DIRECTORY
}

class AppNavigationState {
    var currentScreen by mutableStateOf(Screen.HOME)
    var selectedLanguage by mutableStateOf(AppLanguage.ENGLISH)
    var activeRecord by mutableStateOf<HealthRecord?>(null)
}
