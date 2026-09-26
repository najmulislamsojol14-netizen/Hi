package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AlexDao
import com.example.data.AlexDatabase
import com.example.data.AppHubItem
import com.example.data.AppHubRepository
import com.example.data.ChatMessageEntity
import com.example.data.VoiceNoteEntity
import com.example.network.AlexRepository
import com.example.voice.AlexVoiceEngine
import com.example.voice.AssistantState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    HOME,
    APPS,
    CHAT,
    BROWSER,
    FILES,
    SETTINGS,
    COMMANDS
}

class AlexViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: AlexDao = AlexDatabase.getInstance(application).alexDao()
    private val repository = AlexRepository()

    val chatHistory: StateFlow<List<ChatMessageEntity>> = dao.getAllMessagesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val voiceNotes: StateFlow<List<VoiceNoteEntity>> = dao.getAllVoiceNotesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _greetingTitle = MutableStateFlow("Hello, Master.")
    val greetingTitle: StateFlow<String> = _greetingTitle.asStateFlow()

    private val _greetingSubtitle = MutableStateFlow("How can I help you today?")
    val greetingSubtitle: StateFlow<String> = _greetingSubtitle.asStateFlow()

    private val _lastQuery = MutableStateFlow("")
    val lastQuery: StateFlow<String> = _lastQuery.asStateFlow()

    private val _lastResponse = MutableStateFlow("Hello! I am Alex. Speak your command or open the Apps Hub.")
    val lastResponse: StateFlow<String> = _lastResponse.asStateFlow()

    // Continuous Listening State
    private val _continuousListeningEnabled = MutableStateFlow(false)
    val continuousListeningEnabled: StateFlow<Boolean> = _continuousListeningEnabled.asStateFlow()

    // Event emitted when voice command requests opening an app
    private val _appLaunchEvent = MutableSharedFlow<AppHubItem>(extraBufferCapacity = 1)
    val appLaunchEvent: SharedFlow<AppHubItem> = _appLaunchEvent.asSharedFlow()

    // Settings
    private val _languageCode = MutableStateFlow("en-US") // "en-US" or "bn-BD"
    val languageCode: StateFlow<String> = _languageCode.asStateFlow()

    private val _wakeWordEnabled = MutableStateFlow(false)
    val wakeWordEnabled: StateFlow<Boolean> = _wakeWordEnabled.asStateFlow()

    private val _userName = MutableStateFlow("Master")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _ttsRate = MutableStateFlow(1.0f)
    val ttsRate: StateFlow<Float> = _ttsRate.asStateFlow()

    private val _ttsPitch = MutableStateFlow(1.0f)
    val ttsPitch: StateFlow<Float> = _ttsPitch.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    // Apps & Tools sub-states
    private val _translatorInput = MutableStateFlow("")
    val translatorInput: StateFlow<String> = _translatorInput.asStateFlow()

    private val _translatorResult = MutableStateFlow("")
    val translatorResult: StateFlow<String> = _translatorResult.asStateFlow()

    private val _codeSnippetPrompt = MutableStateFlow("Create a Kotlin coroutine flow counter")
    val codeSnippetPrompt: StateFlow<String> = _codeSnippetPrompt.asStateFlow()

    private val _codeOutput = MutableStateFlow("""
        // Alex Neural Code Synthesizer
        fun counterFlow(max: Int): Flow<Int> = flow {
            for (i in 1..max) {
                emit(i)
                delay(500)
            }
        }
    """.trimIndent())
    val codeOutput: StateFlow<String> = _codeOutput.asStateFlow()

    // Voice Engine
    val voiceEngine = AlexVoiceEngine(
        context = application,
        onTranscriptReady = { text ->
            processQuery(text)
        },
        onWakeWordDetected = {
            _greetingTitle.value = "Wake Word Detected!"
            _greetingSubtitle.value = "Hey Alex active. Listening for command..."
        },
        onSpeechFinishedCallback = {
            _greetingTitle.value = "Hello, Master."
            _greetingSubtitle.value = "How can I help you today?"
        }
    )

    val assistantState: StateFlow<AssistantState> = voiceEngine.assistantState
    val liveVolume: StateFlow<Float> = voiceEngine.liveVolume
    val liveRmsDb: StateFlow<Float> = voiceEngine.liveRmsDb
    val partialTranscript: StateFlow<String> = voiceEngine.partialTranscript

    private val _micSensitivity = MutableStateFlow(voiceEngine.micSensitivity)
    val micSensitivity: StateFlow<Float> = _micSensitivity.asStateFlow()

    private val _isSimulatingVoice = MutableStateFlow(false)
    val isSimulatingVoice: StateFlow<Boolean> = _isSimulatingVoice.asStateFlow()
    private var simulationJob: kotlinx.coroutines.Job? = null

    fun setMicSensitivity(sensitivity: Float) {
        _micSensitivity.value = sensitivity
        voiceEngine.micSensitivity = sensitivity
    }

    fun toggleSimulateVoice() {
        if (_isSimulatingVoice.value) {
            _isSimulatingVoice.value = false
            simulationJob?.cancel()
            simulationJob = null
            voiceEngine.injectSimulatedVolume(0f)
        } else {
            _isSimulatingVoice.value = true
            simulationJob?.cancel()
            simulationJob = viewModelScope.launch {
                var tick = 0
                while (true) {
                    tick++
                    val s1 = kotlin.math.sin(tick * 0.18).toFloat()
                    val s2 = kotlin.math.sin(tick * 0.45).toFloat()
                    val s3 = kotlin.math.cos(tick * 0.95).toFloat()
                    val burst = if (tick % 40 in 0..15) 0.35f else 0f
                    val simLevel = (0.22f + 0.32f * s1 + 0.22f * s2 + 0.12f * s3 + burst).coerceIn(0.05f, 0.98f)
                    voiceEngine.injectSimulatedVolume(simLevel)
                    kotlinx.coroutines.delay(40)
                }
            }
        }
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun toggleContinuousListening(enabled: Boolean) {
        _continuousListeningEnabled.value = enabled
        voiceEngine.setContinuousListening(enabled)
        if (enabled) {
            _greetingTitle.value = if (languageCode.value.startsWith("bn")) "সব সময় শুনছি..." else "Continuous Listening..."
            _greetingSubtitle.value = if (languageCode.value.startsWith("bn")) "যেকোনো সময় কথা বলুন বা নির্দেশ দিন..." else "Speak any command anytime..."
        } else {
            _greetingTitle.value = "Hello, Master."
            _greetingSubtitle.value = "How can I help you today?"
        }
    }

    fun toggleListening() {
        if (assistantState.value == AssistantState.LISTENING) {
            voiceEngine.stopListening()
            _greetingTitle.value = "Hello, Master."
            _greetingSubtitle.value = "How can I help you today?"
        } else {
            voiceEngine.startListening()
            _greetingTitle.value = if (languageCode.value.startsWith("bn")) "শুনছি..." else "Listening..."
            _greetingSubtitle.value = if (languageCode.value.startsWith("bn")) "এখন বলুন..." else "Speak your command..."
        }
    }

    fun stopSpeaking() {
        voiceEngine.stopSpeaking()
        _greetingTitle.value = "Hello, Master."
        _greetingSubtitle.value = "Ready for your next instruction."
    }

    fun processQuery(query: String) {
        if (query.isBlank()) return
        _lastQuery.value = query
        voiceEngine.setState(AssistantState.THINKING)
        _greetingTitle.value = if (languageCode.value.startsWith("bn")) "চিন্তাভাবনা করছি..." else "Thinking..."
        _greetingSubtitle.value = if (languageCode.value.startsWith("bn")) "প্রক্রিয়া করছি..." else "Processing..."

        viewModelScope.launch {
            // Check if this is an app launch voice command
            val matchedApp = AppHubRepository.findAppByVoiceQuery(query)

            // Save user message to Room
            val userMsg = ChatMessageEntity(
                role = "user",
                text = query,
                category = detectCategory(query)
            )
            dao.insertMessage(userMsg)

            val response: String
            if (matchedApp != null) {
                // Formulate app open voice response
                val isBn = languageCode.value.startsWith("bn") || query.any { it in '\u0980'..'\u09FF' }
                response = if (isBn) {
                    "আপনার জন্য ${matchedApp.name} চালু করা হচ্ছে।"
                } else {
                    "Opening ${matchedApp.name} for you, Master."
                }
                // Trigger app launch event
                _appLaunchEvent.tryEmit(matchedApp)
            } else {
                // Gather recent history for conversation context
                val historyPairs = chatHistory.value.takeLast(4).map { it.role to it.text }
                response = repository.queryAlex(query, historyPairs, languageCode.value)
            }

            _lastResponse.value = response

            // Save assistant message to Room
            val alexMsg = ChatMessageEntity(
                role = "alex",
                text = response,
                category = detectCategory(query)
            )
            dao.insertMessage(alexMsg)

            // Update UI state & Speak response
            _greetingTitle.value = if (languageCode.value.startsWith("bn")) "কথা বলছি..." else "Speaking..."
            _greetingSubtitle.value = response

            voiceEngine.speak(response) {
                _greetingTitle.value = "Hello, Master."
                _greetingSubtitle.value = "How can I help you today?"
            }
        }
    }

    private fun detectCategory(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.contains("code") || lower.contains("fun ") || lower.contains("class ") -> "code"
            lower.contains("weather") || lower.contains("rain") || lower.contains("temperature") -> "weather"
            lower.contains("translate") || lower.contains("অনুবাদ") -> "translate"
            lower.contains("who") || lower.contains("what") || lower.contains("search") -> "search"
            AppHubRepository.findAppByVoiceQuery(text) != null -> "apps"
            else -> "general"
        }
    }

    fun toggleMute() {
        val newMute = !_isMuted.value
        _isMuted.value = newMute
        voiceEngine.isMuted = newMute
        if (newMute) {
            voiceEngine.stopSpeaking()
        }
    }

    fun setLanguage(lang: String) {
        _languageCode.value = lang
        voiceEngine.setLanguage(lang)
    }

    fun toggleWakeWord(enabled: Boolean) {
        _wakeWordEnabled.value = enabled
        voiceEngine.isWakeWordModeEnabled = enabled
        if (enabled) {
            voiceEngine.startListening()
        } else {
            voiceEngine.stopListening()
        }
    }

    fun updateTtsSettings(pitch: Float, rate: Float) {
        _ttsPitch.value = pitch
        _ttsRate.value = rate
        voiceEngine.setVoicePitchAndRate(pitch, rate)
    }

    fun setUserName(name: String) {
        _userName.value = name
    }

    fun clearChat() {
        viewModelScope.launch {
            dao.clearAllMessages()
            _lastResponse.value = "Memory cleared. Ready for fresh queries."
            _greetingSubtitle.value = "Chat history cleared."
        }
    }

    fun deleteMessage(id: Long) {
        viewModelScope.launch {
            dao.deleteMessage(id)
        }
    }

    fun saveVoiceNote(title: String, content: String) {
        viewModelScope.launch {
            val note = VoiceNoteEntity(
                title = title.ifBlank { "Voice Note ${System.currentTimeMillis() % 10000}" },
                content = content,
                language = languageCode.value
            )
            dao.insertVoiceNote(note)
        }
    }

    fun deleteVoiceNote(id: Long) {
        viewModelScope.launch {
            dao.deleteVoiceNote(id)
        }
    }

    // Apps & Tools actions
    fun setTranslatorInput(text: String) {
        _translatorInput.value = text
    }

    fun translateCurrentInput() {
        if (_translatorInput.value.isBlank()) return
        viewModelScope.launch {
            val prompt = "Translate the following text into Bengali if it's English, or into English if it's Bengali. Provide only the direct translation: \"${_translatorInput.value}\""
            val result = repository.queryAlex(prompt, emptyList(), languageCode.value)
            _translatorResult.value = result
            voiceEngine.speak(result)
        }
    }

    fun generateCodeSnippet(prompt: String) {
        _codeSnippetPrompt.value = prompt
        viewModelScope.launch {
            val codePrompt = "Write clean, production-ready code with concise explanation for: $prompt"
            val result = repository.queryAlex(codePrompt, emptyList(), languageCode.value)
            _codeOutput.value = result
        }
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
        voiceEngine.release()
    }
}
