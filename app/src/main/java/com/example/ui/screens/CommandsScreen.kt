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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceCard
import com.example.ui.theme.SpaceCardElevated
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextMuted
import com.example.viewmodel.AlexViewModel
import com.example.viewmodel.AppTab

data class VoiceCommandItem(
    val iconEmoji: String,
    val commandEn: String,
    val commandBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val category: String,
    val isStopAction: Boolean = false
)

@Composable
fun CommandsScreen(
    viewModel: AlexViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    val commandsList = remember {
        listOf(
            VoiceCommandItem(
                iconEmoji = "📺",
                commandEn = "Open YouTube",
                commandBn = "ইউটিউব খোলো",
                descriptionEn = "Opens YouTube video player & streams",
                descriptionBn = "ইউটিউব ওপেন করবে।",
                category = "APPS"
            ),
            VoiceCommandItem(
                iconEmoji = "📘",
                commandEn = "Open Facebook",
                commandBn = "ফেসবুক খোলো",
                descriptionEn = "Opens Facebook feed and notifications",
                descriptionBn = "ফেসবুক ওপেন করবে।",
                category = "APPS"
            ),
            VoiceCommandItem(
                iconEmoji = "💬",
                commandEn = "Open Messenger",
                commandBn = "মেসেঞ্জার খোলো",
                descriptionEn = "Opens Facebook Messenger chats",
                descriptionBn = "মেসেঞ্জার ওপেন করবে।",
                category = "APPS"
            ),
            VoiceCommandItem(
                iconEmoji = "📷",
                commandEn = "Open Instagram",
                commandBn = "ইনস্টাগ্রাম খোলো",
                descriptionEn = "Opens Instagram photos and reels",
                descriptionBn = "ইন্সটাগ্রাম ওপেন করবে।",
                category = "APPS"
            ),
            VoiceCommandItem(
                iconEmoji = "🎵",
                commandEn = "Open TikTok",
                commandBn = "টিকটক খোলো",
                descriptionEn = "Opens TikTok short video stream",
                descriptionBn = "টিকটক ওপেন করবে।",
                category = "APPS"
            ),
            VoiceCommandItem(
                iconEmoji = "💚",
                commandEn = "Open WhatsApp",
                commandBn = "হোয়াটসঅ্যাপ খোলো",
                descriptionEn = "Opens WhatsApp encrypted messenger",
                descriptionBn = "হোয়াটসঅ্যাপ ওপেন করবে।",
                category = "APPS"
            ),
            VoiceCommandItem(
                iconEmoji = "🌐",
                commandEn = "Open Google",
                commandBn = "গুগল খোলো",
                descriptionEn = "Opens Google web search engine",
                descriptionBn = "গুগল সার্চ ওপেন করবে।",
                category = "APPS"
            ),
            VoiceCommandItem(
                iconEmoji = "📧",
                commandEn = "Open Gmail",
                commandBn = "জিমেইল খোলো",
                descriptionEn = "Opens Gmail inbox & messages",
                descriptionBn = "জিমেইল ওপেন করবে।",
                category = "APPS"
            ),
            VoiceCommandItem(
                iconEmoji = "🌤",
                commandEn = "Weather",
                commandBn = "আবহাওয়া কেমন",
                descriptionEn = "Gives instant planetary atmospheric report",
                descriptionBn = "আবহাওয়ার বর্তমান তথ্য জানাবে।",
                category = "SYSTEM"
            ),
            VoiceCommandItem(
                iconEmoji = "🛑",
                commandEn = "Stop",
                commandBn = "থামো",
                descriptionEn = "Immediately halts current voice speech",
                descriptionBn = "কথা বলা বা সাউন্ড বন্ধ করবে।",
                category = "CONTROL",
                isStopAction = true
            ),
            VoiceCommandItem(
                iconEmoji = "💻",
                commandEn = "Write Code",
                commandBn = "কোড লেখো",
                descriptionEn = "Synthesizes Kotlin, Python, or Web functions",
                descriptionBn = "প্রোগ্রামিং কোড তৈরি করে দিবে।",
                category = "AI"
            ),
            VoiceCommandItem(
                iconEmoji = "🗣",
                commandEn = "Translate Hello into Bengali",
                commandBn = "ইংরেজি অনুবাদ করো",
                descriptionEn = "Performs neural real-time language translation",
                descriptionBn = "বাংলা ও ইংরেজি সরাসরি অনুবাদ করবে।",
                category = "AI"
            ),
            VoiceCommandItem(
                iconEmoji = "🤖",
                commandEn = "Who are you",
                commandBn = "তুমি কে",
                descriptionEn = "Alex identifies systems, neural core, & specs",
                descriptionBn = "অ্যালেক্সের বিস্তারিত পরিচয় জানাবে।",
                category = "SYSTEM"
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.setTab(AppTab.HOME) },
                    modifier = Modifier.testTag("commands_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = primaryColor
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Voice Commands Guide",
                        color = TextMain,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Say these commands to control Alex hands-free",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Summary Banner Card (matching user HTML style)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SpaceCard),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(primaryColor.copy(alpha = 0.5f), secondaryColor.copy(alpha = 0.3f)))
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = primaryColor)
                        Text(
                            text = "HOW TO COMMAND ALEX",
                            color = primaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "• Tap the Mic or activate 'Always-On Continuous Listening' on Home.\n• Speak clearly in either English or Bengali (বাংলা).\n• Tap any card below to test run that command instantly!",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Interactive Command List
        items(commandsList) { item ->
            CommandCard(
                item = item,
                primaryColor = primaryColor,
                onExecute = {
                    if (item.isStopAction) {
                        viewModel.stopSpeaking()
                        Toast.makeText(context, "Voice stopped", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.processQuery(item.commandEn)
                        Toast.makeText(context, "Executed: ${item.commandEn}", Toast.LENGTH_SHORT).show()
                    }
                },
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Command", "${item.commandEn} / ${item.commandBn}"))
                    Toast.makeText(context, "Command copied", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun CommandCard(
    item: VoiceCommandItem,
    primaryColor: Color,
    onExecute: () -> Unit,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onExecute)
            .testTag("command_card_${item.commandEn.replace(" ", "_").lowercase()}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SpaceCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                if (item.isStopAction) listOf(Color.Red.copy(alpha = 0.5f), Color.Transparent)
                else listOf(primaryColor.copy(alpha = 0.25f), Color.Transparent)
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.isStopAction) Color.Red.copy(alpha = 0.15f)
                        else primaryColor.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.iconEmoji,
                    fontSize = 22.sp
                )
            }

            // Command info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "\"${item.commandEn}\"",
                        color = TextMain,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "/",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "\"${item.commandBn}\"",
                        color = primaryColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${item.descriptionBn} (${item.descriptionEn})",
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            // Action Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onExecute,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (item.isStopAction) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = "Run Command",
                        tint = if (item.isStopAction) Color.Red else primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Phrase",
                        tint = TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
