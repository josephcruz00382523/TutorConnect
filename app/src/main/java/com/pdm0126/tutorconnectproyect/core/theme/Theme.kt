package com.pdm0126.tutorconnectproyect.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary          = UcaNavy,
    onPrimary        = UcaOnNavy,
    primaryContainer = UcaBlueLight,
    onPrimaryContainer = UcaNavyDark,
    secondary        = UcaAccent,
    onSecondary      = UcaOnNavy,
    secondaryContainer = UcaAccent.copy(alpha = 0.15f),
    onSecondaryContainer = UcaNavyDark,
    tertiary         = UcaMint,
    onTertiary       = UcaOnNavy,
    background       = UcaSurface,
    onBackground     = UcaNavyDark,
    surface          = UcaCard,
    onSurface        = UcaNavyDark,
    surfaceVariant   = Color(0xFFE4EDFF),
    onSurfaceVariant = UcaBlue,
    error            = StatusBusy,
    outline          = UcaBlue.copy(alpha = 0.3f),
)

private val DarkColors = darkColorScheme(
    primary          = UcaBlueLight,
    onPrimary        = DarkNavy,
    primaryContainer = UcaBlue,
    onPrimaryContainer = UcaOnNavy,
    secondary        = DarkAccent,
    onSecondary      = DarkNavy,
    tertiary         = UcaMint,
    onTertiary       = DarkNavy,
    background       = DarkNavy,
    onBackground     = UcaOnNavy,
    surface          = DarkCard,
    onSurface        = UcaOnNavy,
    surfaceVariant   = DarkSurface,
    onSurfaceVariant = UcaBlueLight,
    error            = StatusBusy,
    outline          = UcaBlueLight.copy(alpha = 0.3f),
)

@Composable
fun TutorConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Dynamic color desactivado — usamos siempre la paleta UCA
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TutorTypography,
        content = content,
    )
}
