package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import com.example.util.AppLauncherHelper
import com.example.ui.components.AlexBottomNav
import com.example.ui.components.AlexHeader
import com.example.ui.screens.AppsScreen
import com.example.ui.screens.BrowserScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CommandsScreen
import com.example.ui.screens.FilesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SpaceBlack
import com.example.viewmodel.AlexViewModel
import com.example.viewmodel.AppTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val alexViewModel: AlexViewModel = viewModel()
                AlexAppRoot(alexViewModel)
            }
        }
    }
}

@Composable
fun AlexAppRoot(viewModel: AlexViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val state by viewModel.assistantState.collectAsState()
    val languageCode by viewModel.languageCode.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()

    // Handle voice app launch events
    LaunchedEffect(Unit) {
        viewModel.appLaunchEvent.collect { appItem ->
            AppLauncherHelper.launchApp(context, appItem)
        }
    }

    // Handle back button for non-home tabs
    if (currentTab != AppTab.HOME) {
        BackHandler {
            viewModel.setTab(AppTab.HOME)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBlack),
        containerColor = SpaceBlack,
        topBar = {
            AlexHeader(
                state = state,
                languageCode = languageCode,
                isMuted = isMuted,
                onLanguageToggle = {
                    val nextLang = if (languageCode.startsWith("bn")) "en-US" else "bn-BD"
                    viewModel.setLanguage(nextLang)
                },
                onMuteToggle = {
                    viewModel.toggleMute()
                }
            )
        },
        bottomBar = {
            AlexBottomNav(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SpaceBlack)
        ) {
            when (currentTab) {
                AppTab.HOME -> HomeScreen(viewModel = viewModel)
                AppTab.CHAT -> ChatScreen(viewModel = viewModel)
                AppTab.BROWSER -> BrowserScreen(viewModel = viewModel)
                AppTab.APPS -> AppsScreen(viewModel = viewModel)
                AppTab.FILES -> FilesScreen(viewModel = viewModel)
                AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                AppTab.COMMANDS -> CommandsScreen(viewModel = viewModel)
            }
        }
    }
}
