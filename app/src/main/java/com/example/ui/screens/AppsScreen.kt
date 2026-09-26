package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppHubItem
import com.example.data.AppHubRepository
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceCard
import com.example.ui.theme.SpaceCardElevated
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted
import com.example.util.AppLauncherHelper
import com.example.viewmodel.AlexViewModel

@Composable
fun AppsScreen(
    viewModel: AlexViewModel,
    modifier: Modifier = Modifier
) {
    var selectedMainTab by remember { mutableIntStateOf(0) }
    val mainTabs = listOf("APPS HUB", "AI TOOLKIT")
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceBlack)
    ) {
        // Main Navigation Tabs
        TabRow(
            selectedTabIndex = selectedMainTab,
            containerColor = SpaceCard,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedMainTab]),
                    color = NeonCyan
                )
            }
        ) {
            mainTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedMainTab == index,
                    onClick = { selectedMainTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedMainTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    },
                    modifier = Modifier.testTag("app_tab_$index")
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (selectedMainTab == 0) {
                EntertainmentAppsHub(
                    onAppClick = { app ->
                        viewModel.voiceEngine.speak("Opening ${app.name} for you.")
                        AppLauncherHelper.launchApp(context, app)
                    }
                )
            } else {
                AiToolkitSection(viewModel = viewModel, context = context)
            }
        }
    }
}

@Composable
private fun EntertainmentAppsHub(
    onAppClick: (AppHubItem) -> Unit
) {
    val apps = remember { AppHubRepository.appsList }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Greeting & Header Box (as in HTML)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Entertainment & Apps Hub",
                color = TextMain,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Quick access to your favorite platforms",
                color = TextMuted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }

        // 3-Column Grid (matches HTML: repeat(3, 1fr))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(apps, key = { it.id }) { app ->
                AppCardItem(
                    app = app,
                    onClick = { onAppClick(app) }
                )
            }
        }

        // Voice Command Hint
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SpaceCard)
                .border(1.dp, NeonCyan.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "💡", fontSize = 16.sp)
                Text(
                    text = "Tip: You can also say \"Open YouTube\" or \"ফেসবুক খোলো\" by voice anytime!",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun AppCardItem(
    app: AppHubItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(
                width = 1.dp,
                color = NeonCyan.copy(alpha = 0.2f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .testTag("app_card_${app.id}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = app.iconEmoji,
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = app.name,
                color = TextMain,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AiToolkitSection(
    viewModel: AlexViewModel,
    context: Context
) {
    var selectedToolTab by remember { mutableIntStateOf(0) }
    val toolTabs = listOf("TRANSLATOR", "CODE LAB", "WEATHER", "VOICE MEMO")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedToolTab,
            containerColor = SpaceCardElevated,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedToolTab]),
                    color = NeonPurple
                )
            }
        ) {
            toolTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedToolTab == index,
                    onClick = { selectedToolTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 10.sp,
                            fontWeight = if (selectedToolTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (selectedToolTab) {
                0 -> TranslatorTool(viewModel, context)
                1 -> CodeLabTool(viewModel, context)
                2 -> WeatherTool(viewModel)
                3 -> VoiceMemoTool(viewModel, context)
            }
        }
    }
}

@Composable
private fun TranslatorTool(viewModel: AlexViewModel, context: Context) {
    val input by viewModel.translatorInput.collectAsState()
    val result by viewModel.translatorResult.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SpaceCard),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NeonCyan, NeonBlue)))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ENGLISH ⇄ BENGALI TRANSLATION MATRIX",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    OutlinedTextField(
                        value = input,
                        onValueChange = { viewModel.setTranslatorInput(it) },
                        placeholder = { Text("Enter text in English or Bengali (বাংলা)...", color = TextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("translator_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedContainerColor = SpaceCardElevated,
                            unfocusedContainerColor = SpaceCardElevated
                        )
                    )

                    Button(
                        onClick = { viewModel.translateCurrentInput() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("translate_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                    ) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Translate & Speak", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (result.isNotBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpaceCardElevated),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NeonPurple, NeonPink)))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TRANSLATION OUTPUT",
                                color = NeonPurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Row {
                                IconButton(
                                    onClick = { viewModel.voiceEngine.speak(result) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Speak", tint = NeonCyan)
                                }
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Translation", result))
                                        Toast.makeText(context, "Copied translation", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextMuted)
                                }
                            }
                        }
                        Text(
                            text = result,
                            color = TextMain,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeLabTool(viewModel: AlexViewModel, context: Context) {
    val prompt by viewModel.codeSnippetPrompt.collectAsState()
    val codeOutput by viewModel.codeOutput.collectAsState()
    var inputPrompt by remember { mutableStateOf(prompt) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
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
                    Text(
                        text = "NEURAL CODE GENERATOR",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    OutlinedTextField(
                        value = inputPrompt,
                        onValueChange = { inputPrompt = it },
                        placeholder = { Text("Describe function, algorithm, or Android component...", color = TextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("code_prompt_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedContainerColor = SpaceCardElevated,
                            unfocusedContainerColor = SpaceCardElevated
                        )
                    )

                    Button(
                        onClick = { viewModel.generateCodeSnippet(inputPrompt) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_code_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                    ) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Synthesize Code", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF070B18)),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.4f), Color.Transparent)))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SYNTAX CONSOLE",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Row {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Alex Code", codeOutput))
                                    Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Code", tint = NeonCyan, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = {
                                    viewModel.saveVoiceNote("Code: $inputPrompt", codeOutput)
                                    Toast.makeText(context, "Saved to Files vault", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = "Save Snippet", tint = NeonPurple, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Text(
                        text = codeOutput,
                        color = Color(0xFF80FFEA),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherTool(viewModel: AlexViewModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SpaceCard),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NeonCyan, NeonBlue))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    Icon(imageVector = Icons.Default.WbSunny, contentDescription = null, tint = NeonCyan)
                    Text(
                        text = "PLANETARY ATMOSPHERE INTEL",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                IconButton(
                    onClick = {
                        viewModel.processQuery("Give me full weather status telemetry")
                    }
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = NeonCyan)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "28°C", color = TextMain, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Clear & Optimal Skies", color = NeonCyan, fontSize = 14.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SpaceCardElevated)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(text = "Barometric: 1014 hPa\nWind: 9 km/h NE\nHumidity: 52%", color = TextMuted, fontSize = 11.sp, lineHeight = 16.sp)
                }
            }

            Button(
                onClick = {
                    viewModel.voiceEngine.speak("Satellite weather telemetry: Conditions are clear and pleasant at 28 degrees Celsius, 52 percent humidity, and light breeze.")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Listen to Voice Forecast", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun VoiceMemoTool(viewModel: AlexViewModel, context: Context) {
    var memoTitle by remember { mutableStateOf("") }
    var memoContent by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SpaceCard),
            shape = RoundedCornerShape(14.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NeonCyan, NeonPurple)))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "RECORD AUDIO MEMO / NOTE",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                OutlinedTextField(
                    value = memoTitle,
                    onValueChange = { memoTitle = it },
                    placeholder = { Text("Note Title (e.g., Project Roadmap)", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedContainerColor = SpaceCardElevated,
                        unfocusedContainerColor = SpaceCardElevated
                    )
                )

                OutlinedTextField(
                    value = memoContent,
                    onValueChange = { memoContent = it },
                    placeholder = { Text("Memo content or dictated transcription...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedContainerColor = SpaceCardElevated,
                        unfocusedContainerColor = SpaceCardElevated
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (memoContent.isNotBlank()) {
                                viewModel.saveVoiceNote(memoTitle, memoContent)
                                Toast.makeText(context, "Saved to Files vault", Toast.LENGTH_SHORT).show()
                                memoTitle = ""
                                memoContent = ""
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Note")
                    }
                }
            }
        }
    }
}
