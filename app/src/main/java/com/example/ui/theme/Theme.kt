package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

fun getAlexColorScheme(themeStyle: NeonThemeStyle) = when (themeStyle) {
    NeonThemeStyle.CYBER_CYAN -> darkColorScheme(
        primary = NeonCyan,
        onPrimary = SpaceBlack,
        primaryContainer = SpaceCard,
        onPrimaryContainer = NeonCyan,
        secondary = NeonBlue,
        onSecondary = TextMain,
        secondaryContainer = SpaceCardElevated,
        onSecondaryContainer = NeonCyan,
        tertiary = NeonPurple,
        onTertiary = TextMain,
        background = SpaceBlack,
        onBackground = TextMain,
        surface = SpaceBlack,
        onSurface = TextMain,
        surfaceVariant = SpaceCard,
        onSurfaceVariant = TextMuted,
        outline = NeonCyan.copy(alpha = 0.25f),
        outlineVariant = TextSubtle
    )
    NeonThemeStyle.EMERALD_GREEN -> darkColorScheme(
        primary = EmeraldPrimary,
        onPrimary = SpaceBlack,
        primaryContainer = SpaceCard,
        onPrimaryContainer = EmeraldPrimary,
        secondary = EmeraldSecondary,
        onSecondary = TextMain,
        secondaryContainer = SpaceCardElevated,
        onSecondaryContainer = EmeraldPrimary,
        tertiary = EmeraldTertiary,
        onTertiary = TextMain,
        background = SpaceBlack,
        onBackground = TextMain,
        surface = SpaceBlack,
        onSurface = TextMain,
        surfaceVariant = SpaceCard,
        onSurfaceVariant = TextMuted,
        outline = EmeraldPrimary.copy(alpha = 0.25f),
        outlineVariant = TextSubtle
    )
    NeonThemeStyle.PLASMA_AMBER -> darkColorScheme(
        primary = AmberPrimary,
        onPrimary = SpaceBlack,
        primaryContainer = SpaceCard,
        onPrimaryContainer = AmberPrimary,
        secondary = AmberSecondary,
        onSecondary = TextMain,
        secondaryContainer = SpaceCardElevated,
        onSecondaryContainer = AmberPrimary,
        tertiary = AmberTertiary,
        onTertiary = TextMain,
        background = SpaceBlack,
        onBackground = TextMain,
        surface = SpaceBlack,
        onSurface = TextMain,
        surfaceVariant = SpaceCard,
        onSurfaceVariant = TextMuted,
        outline = AmberPrimary.copy(alpha = 0.25f),
        outlineVariant = TextSubtle
    )
}

@Composable
fun MyApplicationTheme(
    themeStyle: NeonThemeStyle = NeonThemeStyle.CYBER_CYAN,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = getAlexColorScheme(themeStyle),
        typography = Typography,
        content = content
    )
}
