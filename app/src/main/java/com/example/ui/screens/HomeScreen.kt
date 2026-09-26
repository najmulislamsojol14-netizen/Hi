package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AlexAmplitudeVisualizer
import com.example.ui.components.AlexOrb
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
import com.example.voice.AssistantState

@Composable
fun HomeScreen(
    viewModel: AlexViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.assistantState.collectAsState()
    val volume by viewModel.liveVolume.collectAsState()
    val rmsDb by viewModel.liveRmsDb.collectAsState()
    val isSimulating by viewModel.isSimulatingVoice.collectAsState()
    val sensitivity by viewModel.micSensitivity.collectAsState()
    val partialTranscript by viewModel.partialTranscript.collectAsState()
    val greetingTitle by viewModel.greetingTitle.collectAsState()
    val greetingSubtitle by viewModel.greetingSubtitle.collectAsState()
    val lastResponse by viewModel.lastResponse.collectAsState()
    val lastQuery by viewModel.lastQuery.collectAsState()
    val languageCode by viewModel.languageCode.collectAsState()
    val continuousListening by viewModel.continuousListeningEnabled.collectAsState()

    var showTextInput by remember { mutableStateOf(false) }
    var typedQuery by remember { mutableStateOf("") }

    // Audio record permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleListening()
        }
    }

    val quickActions = remember {
        listOf(
            "Commands Guide",
            "Open YouTube",
            "Open Facebook",
            "Open Messenger",
            "Open WhatsApp",
            "Ask Alex",
            "Weather",
            "Write Code",
            "Translate",
            "বাংলা মোড"
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF111E38), SpaceBlack),
                    radius = 1200f
                )
            )
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // AI Neural Orb with real-time microphone amplitude scaling
            AlexOrb(
                state = state,
                volume = volume,
                rmsDb = rmsDb,
                onClick = {
                    if (state == AssistantState.SPEAKING) {
                        viewModel.stopSpeaking()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Real-Time Audio Amplitude Visualizer & Frequency Equalizer
            AlexAmplitudeVisualizer(
                volume = volume,
                rmsDb = rmsDb,
                state = state,
                isSimulating = isSimulating,
                sensitivity = sensitivity,
                onSensitivityChange = { viewModel.setMicSensitivity(it) },
                onToggleSimulate = { viewModel.toggleSimulateVoice() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Greeting Box
            Text(
                text = greetingTitle,
                color = TextMain,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("greeting_title")
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (state == AssistantState.LISTENING && partialTranscript.isNotBlank()) {
                    "\"$partialTranscript\""
                } else {
                    greetingSubtitle
                },
                color = if (state == AssistantState.LISTENING) NeonCyan else TextMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("greeting_subtitle")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Continuous Listening Toggle (as requested)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (continuousListening) NeonCyan.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.05f))
                    .border(
                        width = 1.dp,
                        color = if (continuousListening) NeonCyan else NeonCyan.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        viewModel.toggleContinuousListening(!continuousListening)
                    }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("continuous_listening_toggle")
            ) {
                Checkbox(
                    checked = continuousListening,
                    onCheckedChange = { checked ->
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        viewModel.toggleContinuousListening(checked)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = NeonCyan,
                        uncheckedColor = TextMuted,
                        checkmarkColor = SpaceBlack
                    ),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Always-On Continuous Listening (সব সময় শুনবে)",
                    color = if (continuousListening) NeonCyan else TextMain,
                    fontSize = 12.sp,
                    fontWeight = if (continuousListening) FontWeight.Bold else FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Mic Button
            val isListening = state == AssistantState.LISTENING
            val isSpeaking = state == AssistantState.SPEAKING

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .shadow(
                        elevation = if (isListening) 20.dp else 10.dp,
                        shape = CircleShape,
                        spotColor = if (isListening) NeonCyan else NeonBlue
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            if (isListening) listOf(NeonCyan, NeonBlue)
                            else if (isSpeaking) listOf(NeonPink, NeonPurple)
                            else listOf(NeonBlue, NeonPurple)
                        )
                    )
                    .border(
                        width = if (isListening) 2.dp else 0.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
                    .clickable {
                        if (isSpeaking) {
                            viewModel.stopSpeaking()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                    .testTag("primary_mic_button")
            ) {
                Icon(
                    imageVector = when {
                        isSpeaking -> Icons.Default.Stop
                        isListening -> Icons.Default.MicOff
                        else -> Icons.Default.Mic
                    },
                    contentDescription = if (isListening) "Stop Listening" else "Start Listening",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Chat Panel (Requested in HTML)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SpaceCard)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
                    .testTag("home_chat_panel")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (lastQuery.isNotBlank()) {
                        // User message bubble
                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 10.dp, bottomEnd = 2.dp))
                                .background(NeonBlue.copy(alpha = 0.2f))
                                .border(1.dp, NeonBlue.copy(alpha = 0.4f), RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 10.dp, bottomEnd = 2.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = lastQuery,
                                color = TextMain,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Alex response bubble
                    Box(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 10.dp, bottomStart = 2.dp))
                            .background(NeonPurple.copy(alpha = 0.15f))
                            .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 10.dp, bottomStart = 2.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = lastResponse,
                            color = TextMain,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Actions Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickActions) { action ->
                    QuickActionChip(
                        title = action,
                        onClick = {
                            when (action) {
                                "Commands Guide" -> {
                                    viewModel.setTab(AppTab.COMMANDS)
                                }
                                "বাংলা মোড" -> {
                                    val nextLang = if (languageCode.startsWith("bn")) "en-US" else "bn-BD"
                                    viewModel.setLanguage(nextLang)
                                    viewModel.processQuery(if (nextLang.startsWith("bn")) "হ্যালো অ্যালেক্স" else "Hello Alex")
                                }
                                else -> {
                                    viewModel.processQuery(action)
                                }
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Text input field when toggled
        AnimatedVisibility(
            visible = showTextInput,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = typedQuery,
                    onValueChange = { typedQuery = it },
                    placeholder = { Text("Type query or command for Alex...", color = TextMuted, fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("text_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = NeonCyan.copy(alpha = 0.3f),
                        focusedContainerColor = SpaceCardElevated,
                        unfocusedContainerColor = SpaceCard
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (typedQuery.isNotBlank()) {
                                viewModel.processQuery(typedQuery)
                                typedQuery = ""
                                showTextInput = false
                            }
                        }
                    )
                )

                IconButton(
                    onClick = {
                        if (typedQuery.isNotBlank()) {
                            viewModel.processQuery(typedQuery)
                            typedQuery = ""
                            showTextInput = false
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(NeonBlue, NeonPurple)))
                        .testTag("submit_text_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }

        // Bottom secondary controls (Keyboard toggle & Language switch)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            // Keyboard toggle
            IconButton(
                onClick = { showTextInput = !showTextInput },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SpaceCardElevated)
                    .border(1.dp, NeonCyan.copy(alpha = 0.3f), CircleShape)
                    .testTag("toggle_keyboard_button")
            ) {
                Icon(
                    imageVector = if (showTextInput) Icons.Default.Close else Icons.Default.Keyboard,
                    contentDescription = "Keyboard input",
                    tint = NeonCyan
                )
            }

            // Language switch
            IconButton(
                onClick = {
                    val nextLang = if (languageCode.startsWith("bn")) "en-US" else "bn-BD"
                    viewModel.setLanguage(nextLang)
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SpaceCardElevated)
                    .border(1.dp, NeonPurple.copy(alpha = 0.3f), CircleShape)
                    .testTag("language_switch_button")
            ) {
                Text(
                    text = if (languageCode.startsWith("bn")) "🇧🇩 BN" else "🇺🇸 EN",
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, NeonCyan.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag("chip_${title.replace(" ", "_").lowercase()}")
    ) {
        Text(
            text = title,
            color = TextMain,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
