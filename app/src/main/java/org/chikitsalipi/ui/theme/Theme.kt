package org.chikitsalipi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val WarmOffWhite = Color(0xFFF8F9FA)
val ForestTeal = Color(0xFF0F5257)
val SageSlate = Color(0xFF4A7C59)
val EmeraldHighConfidence = Color(0xFF059669)
val AmberUncertainty = Color(0xFFD97706)
val CrimsonWarning = Color(0xFFDC2626)

private val LightColorScheme = lightColorScheme(
    primary = ForestTeal,
    secondary = SageSlate,
    background = WarmOffWhite,
    surface = WarmOffWhite,
    error = CrimsonWarning,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
)

@Composable
fun ChikitsaLipiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
