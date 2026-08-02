package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class AppStyleTheme(val displayName: String, val description: String) {
    OXFORD_BLUE("Oxford Indigo", "Dark navy blue with electric indigo accents"),
    NORDIC_CYAN("Nordic Slate", "Cool slate midnight with electric cyan highlights"),
    EMERALD_NEON("Emerald Cyber", "Deep emerald green with vibrant mint accents"),
    AMBER_OBSIDIAN("Amber Obsidian", "Dark charcoal with warm sunset amber glow"),
    SOLAR_LIGHT("Solar Pure Light", "Crisp high-contrast daylight theme")
}

val OxfordBlueColorScheme = darkColorScheme(
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

val NordicCyanColorScheme = darkColorScheme(
    primary = Color(0xFF00F2FE),
    onPrimary = Color(0xFF00363A),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF80F8FF),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF334155),
    onSecondaryContainer = Color(0xFFCBD5E1),
    tertiary = Color(0xFF38BDF8),
    onTertiary = Color(0xFF00324B),
    tertiaryContainer = Color(0xFF004D71),
    onTertiaryContainer = Color(0xFFC2E8FF),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0F172A),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    surfaceContainerLow = Color(0xFF1E293B),
    surfaceContainer = Color(0xFF1E293B),
    surfaceContainerHigh = Color(0xFF334155),
    surfaceContainerHighest = Color(0xFF475569),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155)
)

val EmeraldNeonColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = Color(0xFF003822),
    primaryContainer = Color(0xFF005234),
    onPrimaryContainer = Color(0xFF6FFBBE),
    secondary = Color(0xFFA7F3D0),
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = Color(0xFF047857),
    onSecondaryContainer = Color(0xFFD1FAE5),
    tertiary = Color(0xFF34D399),
    onTertiary = Color(0xFF003825),
    tertiaryContainer = Color(0xFF005238),
    onTertiaryContainer = Color(0xFF85F8C8),
    background = Color(0xFF064E3B),
    onBackground = Color(0xFFECFDF5),
    surface = Color(0xFF064E3B),
    onSurface = Color(0xFFECFDF5),
    surfaceVariant = Color(0xFF047857),
    onSurfaceVariant = Color(0xFFA7F3D0),
    surfaceContainerLow = Color(0xFF065F46),
    surfaceContainer = Color(0xFF047857),
    surfaceContainerHigh = Color(0xFF059669),
    surfaceContainerHighest = Color(0xFF10B981),
    outline = Color(0xFF34D399),
    outlineVariant = Color(0xFF065F46)
)

val AmberObsidianColorScheme = darkColorScheme(
    primary = Color(0xFFF59E0B),
    onPrimary = Color(0xFF451A03),
    primaryContainer = Color(0xFF78350F),
    onPrimaryContainer = Color(0xFFFDE68A),
    secondary = Color(0xFFFDE68A),
    onSecondary = Color(0xFF27272A),
    secondaryContainer = Color(0xFF3F3F46),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = Color(0xFFF97316),
    onTertiary = Color(0xFF431407),
    tertiaryContainer = Color(0xFF7C2D12),
    onTertiaryContainer = Color(0xFFFFEDD5),
    background = Color(0xFF18181B),
    onBackground = Color(0xFFFAFAFA),
    surface = Color(0xFF18181B),
    onSurface = Color(0xFFFAFAFA),
    surfaceVariant = Color(0xFF3F3F46),
    onSurfaceVariant = Color(0xFFA1A1AA),
    surfaceContainerLow = Color(0xFF27272A),
    surfaceContainer = Color(0xFF27272A),
    surfaceContainerHigh = Color(0xFF3F3F46),
    surfaceContainerHighest = Color(0xFF52525B),
    outline = Color(0xFF71717A),
    outlineVariant = Color(0xFF3F3F46)
)

val SolarLightColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = Color(0xFF475569),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = Color(0xFF1E293B),
    tertiary = Color(0xFF0284C7),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE0F2FE),
    onTertiaryContainer = Color(0xFF0369A1),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    surfaceContainerLow = Color(0xFFF1F5F9),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFE2E8F0),
    surfaceContainerHighest = Color(0xFFCBD5E1),
    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFFCBD5E1)
)

fun AppStyleTheme.toColorScheme(): ColorScheme = when (this) {
    AppStyleTheme.OXFORD_BLUE -> OxfordBlueColorScheme
    AppStyleTheme.NORDIC_CYAN -> NordicCyanColorScheme
    AppStyleTheme.EMERALD_NEON -> EmeraldNeonColorScheme
    AppStyleTheme.AMBER_OBSIDIAN -> AmberObsidianColorScheme
    AppStyleTheme.SOLAR_LIGHT -> SolarLightColorScheme
}
