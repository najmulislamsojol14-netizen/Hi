package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.voice.AssistantState
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

/**
 * AlexOrb: Interactive AI Neural Orb featuring a real-time amplitude visualizer
 * that dynamically scales the orb core and renders audio spectrum rays based on
 * microphone volume levels.
 */
@Composable
fun AlexOrb(
    state: AssistantState,
    volume: Float = 0f,
    rmsDb: Float = -2f,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val infiniteTransition = rememberInfiniteTransition(label = "orb_continuous_animations")

    // Gentle breathing pulse for READY state
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Spin rotations for THINKING & orbiting nodes
    val spinRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_rotation"
    )

    // Counter-spin rotation
    val reverseSpinRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reverse_spin"
    )

    // Voice harmonic wave ripples for SPEAKING state
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    // Smooth physics-based interpolation of microphone volume levels
    // Spring physics provides snappy attack and bouncy organic decay
    val animatedVolume by animateFloatAsState(
        targetValue = volume.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = 0.58f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "mic_amplitude_spring"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(240.dp)
            .testTag("alex_ai_orb")
            .semantics {
                contentDescription = "AI Neural Orb, state: ${state.name}, amplitude: ${(volume * 100).toInt()}%"
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(236.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.width * 0.27f

            // Dynamic scaling calculation:
            // In LISTENING state, the orb scales up dramatically (up to 1.62x!) based on microphone volume
            val dynamicScale = when (state) {
                AssistantState.READY -> pulseScale
                AssistantState.LISTENING -> {
                    // Base 1.0 + amplitude expansion (up to +0.60) + subtle ambient pulse
                    1.0f + (animatedVolume * 0.60f) + ((pulseScale - 1f) * 0.25f)
                }
                AssistantState.THINKING -> {
                    0.96f + sin(spinRotation * Math.PI.toFloat() / 90f) * 0.05f
                }
                AssistantState.SPEAKING -> {
                    1.0f + (animatedVolume * 0.38f) + sin(wavePhase * Math.PI.toFloat() * 2f) * 0.09f
                }
            }

            val currentRadius = baseRadius * dynamicScale

            // -------------------------------------------------------------
            // LAYER 1: Deep Outer Glow Aura (expands and brightens with volume)
            // -------------------------------------------------------------
            val glowRadius = currentRadius * (1.85f + animatedVolume * 0.75f)
            val glowColor = when (state) {
                AssistantState.READY -> primary.copy(alpha = 0.16f)
                AssistantState.LISTENING -> primary.copy(alpha = (0.28f + animatedVolume * 0.45f).coerceIn(0f, 0.85f))
                AssistantState.THINKING -> tertiary.copy(alpha = 0.30f)
                AssistantState.SPEAKING -> secondary.copy(alpha = (0.25f + animatedVolume * 0.35f).coerceIn(0f, 0.75f))
            }
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor, Color.Transparent),
                    center = center,
                    radius = glowRadius
                ),
                radius = glowRadius,
                center = center
            )

            // -------------------------------------------------------------
            // LAYER 2: Real-Time Dynamic Radial Amplitude Equalizer Spikes
            // -------------------------------------------------------------
            if (state == AssistantState.LISTENING || animatedVolume > 0.05f) {
                val rayCount = 36
                val angleStep = 360f / rayCount
                val activeVol = animatedVolume

                for (i in 0 until rayCount) {
                    val angleDeg = i * angleStep
                    val angleRad = Math.toRadians(angleDeg.toDouble())

                    // Harmonic variation across rays simulating frequency bands
                    val harmonicMod = sin(angleRad * 3.0 + spinRotation * 0.03).absoluteValue.toFloat()
                    val rayHeight = (3.dp.toPx() + (harmonicMod * 28.dp.toPx()) * (activeVol * 1.55f + 0.06f))

                    val startDistance = currentRadius + 3.dp.toPx()
                    val endDistance = startDistance + rayHeight

                    val startX = (center.x + startDistance * cos(angleRad)).toFloat()
                    val startY = (center.y + startDistance * sin(angleRad)).toFloat()
                    val endX = (center.x + endDistance * cos(angleRad)).toFloat()
                    val endY = (center.y + endDistance * sin(angleRad)).toFloat()

                    // Ray color shifts from Cyan to Blue/Pink at peak amplitude
                    val rayAlpha = (0.45f + activeVol * 0.50f).coerceIn(0f, 0.95f)
                    val rayColor = if (harmonicMod > 0.65f) secondary.copy(alpha = rayAlpha) else primary.copy(alpha = rayAlpha)

                    drawLine(
                        color = rayColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = (2.2f + activeVol * 1.2f).dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // -------------------------------------------------------------
            // LAYER 3: State-Specific Animated Wave Rings / Shockwaves
            // -------------------------------------------------------------
            when (state) {
                AssistantState.LISTENING -> {
                    // Concentric acoustic wave ripples scaling with input volume
                    val ringCount = 3
                    for (i in 1..ringCount) {
                        val ringDistance = currentRadius + (i * 14.dp.toPx()) * (0.55f + animatedVolume * 0.85f)
                        val ringAlpha = ((0.80f - (i * 0.22f)) * (0.6f + animatedVolume * 0.4f)).coerceIn(0.1f, 0.85f)
                        drawCircle(
                            color = primary.copy(alpha = ringAlpha),
                            radius = ringDistance,
                            center = center,
                            style = Stroke(width = (2.4f - i * 0.4f).dp.toPx())
                        )
                    }

                    // Outer VU Meter Perimeter Arc: fills based on volume
                    val arcRadius = currentRadius + 8.dp.toPx()
                    val arcSweep = 240f
                    val arcStart = 150f
                    val activeSweep = arcSweep * animatedVolume

                    // Track background
                    drawArc(
                        color = primary.copy(alpha = 0.18f),
                        startAngle = arcStart,
                        sweepAngle = arcSweep,
                        useCenter = false,
                        topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                        size = Size(arcRadius * 2f, arcRadius * 2f),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Active meter fill
                    if (activeSweep > 2f) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(primary, secondary, tertiary, primary),
                                center = center
                            ),
                            startAngle = arcStart,
                            sweepAngle = activeSweep,
                            useCenter = false,
                            topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                            size = Size(arcRadius * 2f, arcRadius * 2f),
                            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                AssistantState.THINKING -> {
                    // Gyroscopic spinning rings
                    rotate(spinRotation, pivot = center) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                listOf(primary, tertiary, secondary, primary),
                                center = center
                            ),
                            radius = currentRadius * 1.25f,
                            center = center,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )
                    }
                    rotate(reverseSpinRotation, pivot = center) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                listOf(tertiary, Color.Transparent, primary, Color.Transparent),
                                center = center
                            ),
                            radius = currentRadius * 1.45f,
                            center = center,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )
                    }
                }

                AssistantState.SPEAKING -> {
                    // Expanding voice harmonic ripples
                    val ripple1Radius = currentRadius + (wavePhase * 42.dp.toPx())
                    val ripple2Radius = currentRadius + (((wavePhase + 0.5f) % 1f) * 42.dp.toPx())
                    val ripple1Alpha = ((1f - wavePhase) * (0.6f + animatedVolume * 0.4f)).coerceIn(0f, 0.85f)
                    val ripple2Alpha = ((1f - ((wavePhase + 0.5f) % 1f)) * (0.6f + animatedVolume * 0.4f)).coerceIn(0f, 0.85f)

                    drawCircle(
                        color = tertiary.copy(alpha = ripple1Alpha),
                        radius = ripple1Radius,
                        center = center,
                        style = Stroke(width = (2.5f + animatedVolume * 1.5f).dp.toPx())
                    )
                    drawCircle(
                        color = primary.copy(alpha = ripple2Alpha),
                        radius = ripple2Radius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                AssistantState.READY -> {
                    // Orbiting satellite neural nodes
                    rotate(spinRotation * 0.4f, pivot = center) {
                        for (angle in 0 until 360 step 60) {
                            val rad = Math.toRadians(angle.toDouble()).toFloat()
                            val nodeDistance = currentRadius * 1.28f
                            val nodePos = Offset(
                                center.x + nodeDistance * cos(rad),
                                center.y + nodeDistance * sin(rad)
                            )
                            drawCircle(
                                color = primary.copy(alpha = 0.55f),
                                radius = 2.5.dp.toPx(),
                                center = nodePos
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // LAYER 4: Central Glowing Quantum Core Sphere
            // -------------------------------------------------------------
            val coreColors = when (state) {
                AssistantState.READY -> listOf(
                    Color.White.copy(alpha = 0.95f),
                    primary,
                    secondary,
                    tertiary,
                    Color(0xFF090D24)
                )
                AssistantState.LISTENING -> {
                    // Core shifts towards brighter white/cyan as mic amplitude increases
                    val coreWhiteAlpha = (0.85f + animatedVolume * 0.15f).coerceIn(0f, 1f)
                    listOf(
                        Color.White.copy(alpha = coreWhiteAlpha),
                        primary,
                        secondary,
                        tertiary,
                        Color(0xFF051833)
                    )
                }
                AssistantState.THINKING -> listOf(
                    Color.White,
                    tertiary,
                    primary,
                    secondary,
                    Color(0xFF1B0A33)
                )
                AssistantState.SPEAKING -> listOf(
                    Color.White,
                    secondary,
                    tertiary,
                    primary,
                    Color(0xFF2B0A2E)
                )
            }

            // Radial gradient core offset slightly for 3D sphere illumination
            val lightSource = Offset(center.x - currentRadius * 0.22f, center.y - currentRadius * 0.22f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = coreColors,
                    center = lightSource,
                    radius = currentRadius * 1.15f
                ),
                radius = currentRadius,
                center = center
            )

            // -------------------------------------------------------------
            // LAYER 5: Inner Rim Highlight & Specular Energy Sheen
            // -------------------------------------------------------------
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.65f + animatedVolume * 0.25f),
                        Color.Transparent
                    ),
                    start = Offset(center.x - currentRadius, center.y - currentRadius),
                    end = center
                ),
                radius = currentRadius,
                center = center,
                style = Stroke(width = (1.5f + animatedVolume * 0.8f).dp.toPx())
            )
        }
    }
}
