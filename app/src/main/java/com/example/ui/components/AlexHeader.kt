package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SpaceCard
import com.example.ui.theme.TextMain
import com.example.voice.AssistantState

@Composable
fun AlexHeader(
    state: AssistantState,
    languageCode: String,
    isMuted: Boolean,
    onLanguageToggle: () -> Unit,
    onMuteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCard)
                    .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ALEX AI",
                    color = NeonCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Text(
                text = "v2.5 NEURAL",
                color = NeonCyan.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        // Status Badge & Controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Language Toggle Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(SpaceCard)
                    .border(1.dp, NeonBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .clickable { onLanguageToggle() }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("language_toggle_button")
            ) {
                Text(
                    text = if (languageCode.startsWith("bn")) "🇧🇩 BN" else "🇺🇸 EN",
                    color = TextMain,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Status Badge
            val (badgeText, badgeColor) = when (state) {
                AssistantState.READY -> "READY" to NeonCyan
                AssistantState.LISTENING -> "LISTENING" to NeonCyan
                AssistantState.THINKING -> "THINKING" to NeonPurple
                AssistantState.SPEAKING -> "SPEAKING" to NeonPink
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(badgeColor.copy(alpha = 0.12f))
                    .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Mute / Unmute Button
            IconButton(
                onClick = onMuteToggle,
                modifier = Modifier.testTag("mute_toggle_button")
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                    contentDescription = if (isMuted) "Unmute Alex" else "Mute Alex",
                    tint = if (isMuted) Color.Gray else NeonCyan
                )
            }
        }
    }
}
