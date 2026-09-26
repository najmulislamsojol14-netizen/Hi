package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceCard
import com.example.ui.theme.SpaceCardElevated
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted
import com.example.viewmodel.AlexViewModel

data class KnowledgeCardItem(
    val title: String,
    val category: String,
    val query: String,
    val iconColor: Color
)

@Composable
fun BrowserScreen(
    viewModel: AlexViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var activeSearchResult by remember { mutableStateOf<String?>(null) }
    var activeSearchTopic by remember { mutableStateOf<String?>(null) }
    var isSearching by remember { mutableStateOf(false) }

    val knowledgePicks = remember {
        listOf(
            KnowledgeCardItem("Quantum Computing", "PHYSICS", "Explain Quantum Computing principles and qubits concisely", NeonCyan),
            KnowledgeCardItem("Jetpack Compose", "TECH", "Summarize modern Android Jetpack Compose architecture best practices", NeonBlue),
            KnowledgeCardItem("Space & Exoplanets", "ASTRONOMY", "Tell me about the latest discoveries regarding habitable exoplanets", NeonPurple),
            KnowledgeCardItem("Bangladesh Tech", "REGIONAL", "What is the status of the IT sector and startup ecosystem in Bangladesh?", NeonCyan),
            KnowledgeCardItem("Neural AI Models", "AI & ML", "How do transformer attention mechanisms and Gemini models work?", NeonPurple)
        )
    }

    val trendingTopics = remember {
        listOf("WebAssembly", "Mars Rovers", "Kotlin Multiplatform", "Nuclear Fusion", "Dhaka Smart City")
    }

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
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "Browser",
                        tint = NeonCyan
                    )
                    Text(
                        text = "NEURAL KNOWLEDGE BROWSER",
                        color = NeonCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "AI-powered real-time research, data briefings, and planetary telemetry",
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search any topic, technology, or question...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = NeonCyan
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(
                            onClick = {
                                activeSearchTopic = searchQuery
                                isSearching = true
                                viewModel.processQuery("Research and summarize: $searchQuery")
                                activeSearchResult = "Researching '$searchQuery' via Alex neural engine... Check live audio response or chat log."
                                isSearching = false
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                                .testTag("browser_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Go",
                                tint = SpaceBlack
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("browser_search_input"),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedContainerColor = SpaceCard,
                    unfocusedContainerColor = SpaceCard
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            activeSearchTopic = searchQuery
                            viewModel.processQuery("Research and summarize: $searchQuery")
                            activeSearchResult = "Dispatched query to Alex neural core: '$searchQuery'."
                        }
                    }
                )
            )
        }

        // Trending Tags
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "POPULAR INQUIRIES",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(trendingTopics) { topic ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(SpaceCard)
                                .border(1.dp, NeonBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                .clickable {
                                    searchQuery = topic
                                    activeSearchTopic = topic
                                    viewModel.processQuery("Give me a comprehensive overview of $topic")
                                    activeSearchResult = "Alex is preparing a neural synthesis for: $topic"
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "# $topic",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Active Briefing Card if present
        if (activeSearchResult != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SpaceCardElevated),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NeonCyan, NeonPurple)))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RESEARCH DOSSIER: ${activeSearchTopic?.uppercase() ?: "RESULT"}",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            IconButton(
                                onClick = {
                                    activeSearchResult?.let { viewModel.voiceEngine.speak(it) }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Read aloud",
                                    tint = NeonCyan
                                )
                            }
                        }
                        Text(
                            text = activeSearchResult ?: "",
                            color = TextMain,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }

        // Curated Neural Knowledge Cards
        item {
            Text(
                text = "EXPLORE KNOWLEDGE ARCHIVES",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(knowledgePicks) { pick ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        searchQuery = pick.title
                        activeSearchTopic = pick.title
                        viewModel.processQuery(pick.query)
                        activeSearchResult = "Querying: ${pick.query}. Audio playback active."
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SpaceCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(pick.iconColor.copy(alpha = 0.4f), Color.Transparent))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(pick.iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = pick.iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pick.category,
                            color = pick.iconColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = pick.title,
                            color = TextMain,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = pick.query,
                            color = TextMuted,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Search",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
