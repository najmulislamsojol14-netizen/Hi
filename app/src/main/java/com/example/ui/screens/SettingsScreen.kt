package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceCard
import com.example.ui.theme.SpaceCardElevated
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted
import com.example.viewmodel.AlexViewModel
import com.example.viewmodel.AppTab

@Composable
fun SettingsScreen(
    viewModel: AlexViewModel,
    modifier: Modifier = Modifier
) {
    val languageCode by viewModel.languageCode.collectAsState()
    val wakeWordEnabled by viewModel.wakeWordEnabled.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val ttsRate by viewModel.ttsRate.collectAsState()
    val ttsPitch by viewModel.ttsPitch.collectAsState()
    val messages by viewModel.chatHistory.collectAsState()
    val notes by viewModel.voiceNotes.collectAsState()
    val micSensitivity by viewModel.micSensitivity.collectAsState()
    val liveVolume by viewModel.liveVolume.collectAsState()
    val liveRmsDb by viewModel.liveRmsDb.collectAsState()

    var editingUserName by remember(userName) { mutableStateOf(userName) }
    var currentPitch by remember(ttsPitch) { mutableStateOf(ttsPitch) }
    var currentRate by remember(ttsRate) { mutableStateOf(ttsRate) }
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = NeonCyan)
                Text(
                    text = "ALEX SYSTEM CONFIGURATION",
                    color = NeonCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Text(
                text = "Control speech recognition, wake word triggers, audio synthesis, and neural settings",
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Voice Commands Guide Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SpaceCard),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NeonCyan, NeonBlue))),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setTab(AppTab.COMMANDS) }
                    .testTag("settings_commands_guide_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎙️", fontSize = 20.sp)
                        }
                        Column {
                            Text(
                                text = "VOICE COMMANDS GUIDE (কমান্ড তালিকা)",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "View & test all English & Bengali voice commands",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Commands Guide",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Wake Word Detection Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SpaceCard),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NeonCyan, NeonPurple)))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = NeonCyan)
                            Text(
                                text = "WAKE WORD DETECTION",
                                color = TextMain,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Switch(
                            checked = wakeWordEnabled,
                            onCheckedChange = { viewModel.toggleWakeWord(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonCyan,
                                checkedTrackColor = NeonBlue.copy(alpha = 0.5f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = SpaceCardElevated
                            ),
                            modifier = Modifier.testTag("wake_word_switch")
                        )
                    }

                    Text(
                        text = "When active, Alex continuously listens for the phrase 'Hey Alex' or 'অ্যালেক্স' to immediately wake up and process instructions.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    if (wakeWordEnabled) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonCyan.copy(alpha = 0.1f))
                                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "● WAKE LISTENER ACTIVE — Say 'Hey Alex' anytime!",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Microphone Amplitude & Orb Scaling Settings
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SpaceCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, tint = NeonCyan)
                        Text(
                            text = "AMPLITUDE VISUALIZER & ORB SCALING",
                            color = TextMain,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "Calibrate how aggressively input microphone volume scales the neural orb and drives real-time audio spectrum rays.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Mic Sensitivity Multiplier", color = TextMain, fontSize = 13.sp)
                        Text(
                            text = String.format(java.util.Locale.US, "%.1fx", micSensitivity),
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    }

                    Slider(
                        value = micSensitivity,
                        onValueChange = { viewModel.setMicSensitivity(it) },
                        valueRange = 0.8f..2.5f,
                        steps = 16,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonBlue,
                            inactiveTrackColor = SpaceCardElevated
                        ),
                        modifier = Modifier.testTag("mic_sensitivity_slider")
                    )

                    // Live mic amplitude meter preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SpaceCardElevated)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Volume Level: ${(liveVolume * 100).toInt()}%",
                            color = if (liveVolume > 0.05f) NeonCyan else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f dB", liveRmsDb),
                            color = TextMain,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Language Selection Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SpaceCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "PRIMARY RECOGNITION LANGUAGE",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // English option
                        val isEn = languageCode == "en-US"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isEn) NeonBlue.copy(alpha = 0.25f) else SpaceCardElevated)
                                .border(
                                    1.dp,
                                    if (isEn) NeonCyan else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.setLanguage("en-US") }
                                .padding(12.dp)
                                .testTag("lang_select_en")
                        ) {
                            Column {
                                Text(text = "🇺🇸 English", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "United States", color = TextMuted, fontSize = 11.sp)
                            }
                        }

                        // Bengali option
                        val isBn = languageCode.startsWith("bn")
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isBn) NeonPurple.copy(alpha = 0.25f) else SpaceCardElevated)
                                .border(
                                    1.dp,
                                    if (isBn) NeonCyan else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.setLanguage("bn-BD") }
                                .padding(12.dp)
                                .testTag("lang_select_bn")
                        ) {
                            Column {
                                Text(text = "🇧🇩 বাংলা", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "Bangladesh", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Voice Engine Modulation (TTS)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SpaceCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, tint = NeonCyan)
                        Text(
                            text = "VOICE ENGINE FREQUENCY TUNING",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Pitch
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Voice Pitch", color = TextMain, fontSize = 12.sp)
                            Text(text = String.format("%.2fx", currentPitch), color = NeonCyan, fontSize = 12.sp)
                        }
                        Slider(
                            value = currentPitch,
                            onValueChange = {
                                currentPitch = it
                                viewModel.updateTtsSettings(currentPitch, currentRate)
                            },
                            valueRange = 0.6f..1.5f,
                            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonBlue)
                        )
                    }

                    // Speech Rate
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Speech Rate (Speed)", color = TextMain, fontSize = 12.sp)
                            Text(text = String.format("%.2fx", currentRate), color = NeonCyan, fontSize = 12.sp)
                        }
                        Slider(
                            value = currentRate,
                            onValueChange = {
                                currentRate = it
                                viewModel.updateTtsSettings(currentPitch, currentRate)
                            },
                            valueRange = 0.7f..1.6f,
                            colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
                        )
                    }

                    Button(
                        onClick = {
                            val testMsg = if (languageCode.startsWith("bn")) {
                                "নমস্কার, আমি অ্যালেক্স। আমার ভয়েস ইঞ্জিন সঠিকভাবে কাজ করছে।"
                            } else {
                                "Hello Master, this is the voice frequency calibration for Alex."
                            }
                            viewModel.voiceEngine.speak(testMsg)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_voice_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SpaceCardElevated),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(NeonCyan, NeonBlue))),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, tint = NeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Test Voice Calibration", color = NeonCyan)
                    }
                }
            }
        }

        // User Honorific / Identity
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SpaceCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NeonCyan)
                        Text(
                            text = "USER DESIGNATION / HONORIFIC",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    OutlinedTextField(
                        value = editingUserName,
                        onValueChange = {
                            editingUserName = it
                            viewModel.setUserName(it)
                        },
                        placeholder = { Text("Master, Commander, Name...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedContainerColor = SpaceCardElevated,
                            unfocusedContainerColor = SpaceCardElevated
                        )
                    )
                }
            }
        }

        // Neural AI Status
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SpaceCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = NeonCyan)
                        Text(
                            text = "AI NEURAL ENGINE: GEMINI 3.5 FLASH",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = "Direct REST client configured with 60-second timeouts and offline assistant fallback.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Storage & Cache stats
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SpaceCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = NeonCyan)
                        Text(
                            text = "LOCAL PERSISTENCE VAULT",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "Room Database: ${messages.size} chat log entries, ${notes.size} archived notes.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Button(
                        onClick = {
                            viewModel.clearChat()
                            Toast.makeText(context, "All chat memory wiped", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color.Red.copy(alpha = 0.6f), Color.Transparent))),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Reset Conversation History", color = Color(0xFFFF8080))
                    }
                }
            }
        }
    }
}
