package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricIndigo,
    onPrimary = Color(0xFF1000A9),
    primaryContainer = ElectricIndigoContainer,
    onPrimaryContainer = Color(0xFF0D0096),
    secondary = SlateSecondary,
    onSecondary = Color(0xFF213145),
    secondaryContainer = Color(0xFF3A4A5F),
    onSecondaryContainer = Color(0xFFA9BAD3),
    tertiary = SkyBlue,
    onTertiary = Color(0xFF00354A),
    tertiaryContainer = SkyBlueContainer,
    onTertiaryContainer = Color(0xFF002D40),
    background = OxfordBlue,
    onBackground = OnSurfaceText,
    surface = OxfordBlue,
    onSurface = OnSurfaceText,
    surfaceVariant = SurfaceContainerHighest,
    onSurfaceVariant = OnSurfaceVariantText,
    surfaceContainerLow = SurfaceLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    outline = OutlineBorder,
    outlineVariant = OutlineVariantBorder
)

@Composable
fun MasterNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
