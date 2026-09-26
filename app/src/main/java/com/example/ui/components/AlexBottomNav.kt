package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.TextMuted
import com.example.viewmodel.AppTab

@Composable
fun AlexBottomNav(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SpaceBlack.copy(alpha = 0.95f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .navigationBarsPadding()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavButton(
                label = "HOME",
                icon = Icons.Default.Home,
                selected = currentTab == AppTab.HOME,
                onClick = { onTabSelected(AppTab.HOME) },
                testTag = "nav_home"
            )
            NavButton(
                label = "APPS HUB",
                icon = Icons.Default.Apps,
                selected = currentTab == AppTab.APPS,
                onClick = { onTabSelected(AppTab.APPS) },
                testTag = "nav_apps"
            )
            NavButton(
                label = "COMMANDS",
                icon = Icons.Default.RecordVoiceOver,
                selected = currentTab == AppTab.COMMANDS,
                onClick = { onTabSelected(AppTab.COMMANDS) },
                testTag = "nav_commands"
            )
            NavButton(
                label = "CHAT",
                icon = Icons.AutoMirrored.Filled.Chat,
                selected = currentTab == AppTab.CHAT,
                onClick = { onTabSelected(AppTab.CHAT) },
                testTag = "nav_chat"
            )
            NavButton(
                label = "SETTINGS",
                icon = Icons.Default.Settings,
                selected = currentTab == AppTab.SETTINGS,
                onClick = { onTabSelected(AppTab.SETTINGS) },
                testTag = "nav_settings"
            )
        }
    }
}

@Composable
private fun NavButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val activeColor = NeonCyan
    val inactiveColor = TextMuted

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) activeColor else inactiveColor,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = label,
            color = if (selected) activeColor else inactiveColor,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = 0.5.sp
        )
    }
}
