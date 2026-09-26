package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.theme.SpaceCardElevated
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted
import com.example.voice.AssistantState
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.sin

/**
 * AlexAmplitudeVisualizer: Real-time microphone audio amplitude dashboard featuring
 * an animated multi-band frequency waveform spectrum, live dB/percentage telemetry,
 * sensitivity calibration, and one-tap voice wave simulation.
 */
@Composable
fun AlexAmplitudeVisualizer(
    volume: Float,
    rmsDb: Float,
    state: AssistantState,
    isSimulating: Boolean,
    sensitivity: Float,
    onSensitivityChange: (Float) -> Unit,
    onToggleSimulate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "eq_bar_animation")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "eq_phase"
    )

    val isActive = state == AssistantState.LISTENING || state == AssistantState.SPEAKING || isSimulating || volume > 0.05f
    val clampedVol = volume.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceCard)
            .border(
                width = 1.dp,
                color = if (isActive) NeonCyan.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
            .testTag("alex_amplitude_visualizer")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header: Live Telemetry & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) NeonCyan else Color.Gray.copy(alpha = 0.5f)
                            )
                    )
                    Text(
                        text = if (state == AssistantState.LISTENING) "MIC ACTIVE • LISTENING"
                        else if (state == AssistantState.SPEAKING) "VOICE OUTPUT • SPEAKING"
                        else if (isSimulating) "SIMULATING AMPLITUDE"
                        else "MIC STANDBY",
                        color = if (isActive) NeonCyan else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // dB & percentage readout
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = String.format(Locale.US, "%.1f dB", rmsDb),
                        color = TextMain,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isActive) NeonBlue.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${(clampedVol * 100).toInt()}%",
                            color = if (clampedVol > 0.6f) NeonPink else NeonCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Multi-Band Real-Time Waveform Equalizer (22 frequency bars)
            val barCount = 22
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                for (i in 0 until barCount) {
                    // Harmonic wave shape based on bar index and phase
                    val harmonic1 = sin(phase + i * 0.45).absoluteValue.toFloat()
                    val harmonic2 = sin(phase * 1.5 - i * 0.3).absoluteValue.toFloat()
                    val harmonic = (harmonic1 * 0.6f + harmonic2 * 0.4f)

                    // Height calculation: base height + amplitude scaled height
                    val minHeight = 4.dp
                    val maxHeight = 34.dp
                    val activeHeight = if (isActive) {
                        val factor = (clampedVol * 0.85f + 0.15f) * harmonic
                        minHeight + (maxHeight - minHeight) * factor.coerceIn(0f, 1f)
                    } else {
                        minHeight
                    }

                    // Gradient bar based on volume
                    val barColors = if (clampedVol > 0.7f) {
                        listOf(NeonPink, NeonPurple)
                    } else if (clampedVol > 0.35f) {
                        listOf(NeonCyan, NeonBlue)
                    } else {
                        listOf(NeonBlue.copy(alpha = 0.5f), NeonCyan.copy(alpha = 0.5f))
                    }

                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(activeHeight)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Brush.verticalGradient(barColors))
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Controls Strip: Sensitivity selector & Wave simulation toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sensitivity presets
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Sens:",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    listOf(
                        1.0f to "1x",
                        1.4f to "1.4x",
                        2.0f to "2x"
                    ).forEach { (sensValue, label) ->
                        val isSelected = (sensitivity - sensValue).absoluteValue < 0.1f
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else SpaceCardElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonCyan else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onSensitivityChange(sensValue) }
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                .testTag("sens_$label")
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) NeonCyan else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Test / Simulation Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSimulating) NeonPink.copy(alpha = 0.2f) else SpaceCardElevated)
                        .border(
                            1.dp,
                            if (isSimulating) NeonPink else Color.White.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(onClick = onToggleSimulate)
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                        .testTag("toggle_simulate_mic")
                ) {
                    Icon(
                        imageVector = if (isSimulating) Icons.Default.Stop else Icons.Default.GraphicEq,
                        contentDescription = if (isSimulating) "Stop Test" else "Test Mic Wave",
                        tint = if (isSimulating) NeonPink else NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isSimulating) "Stop Wave" else "Test Wave",
                        color = if (isSimulating) NeonPink else TextMain,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
