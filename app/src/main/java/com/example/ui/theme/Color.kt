package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Futuristic Cyberpunk Neon Base
val SpaceBlack = Color(0xFF060914)
val SpaceDark = Color(0xFF0A0F24)
val SpaceCard = Color(0xFF111E38)
val SpaceCardElevated = Color(0xFF162544)
val SpaceBorder = Color(0x3300F2FE)

val TextMain = Color(0xFFFFFFFF)
val TextMuted = Color(0xFF8A99AD)
val TextSubtle = Color(0xFF4E5D78)

// 1. Cyber Cyan (Default)
val NeonCyan = Color(0xFF00F2FE)
val NeonBlue = Color(0xFF0072FF)
val NeonPurple = Color(0xFF9D4EDD)
val NeonPink = Color(0xFFFF007F)
val NeonGreen = Color(0xFF00FF88)

// 2. Emerald Green Theme
val EmeraldPrimary = Color(0xFF00FF87)
val EmeraldSecondary = Color(0xFF60EFFF)
val EmeraldTertiary = Color(0xFF00B4D8)

// 3. Plasma Amber Theme
val AmberPrimary = Color(0xFFFFB703)
val AmberSecondary = Color(0xFFFB8500)
val AmberTertiary = Color(0xFFD90429)

enum class NeonThemeStyle(val id: String, val displayName: String) {
    CYBER_CYAN("cyan", "Cyber Cyan (Default)"),
    EMERALD_GREEN("emerald", "Emerald Green"),
    PLASMA_AMBER("amber", "Plasma Amber")
}
