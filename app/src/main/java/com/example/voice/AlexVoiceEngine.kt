package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class AssistantState {
    READY,
    LISTENING,
    THINKING,
    SPEAKING
}

class AlexVoiceEngine(
    private val context: Context,
    private val onTranscriptReady: (String) -> Unit,
    private val onWakeWordDetected: () -> Unit,
    private val onSpeechFinishedCallback: (() -> Unit)? = null
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _assistantState = MutableStateFlow(AssistantState.READY)
    val assistantState: StateFlow<AssistantState> = _assistantState.asStateFlow()

    private val _liveVolume = MutableStateFlow(0f)
    val liveVolume: StateFlow<Float> = _liveVolume.asStateFlow()

    private val _liveRmsDb = MutableStateFlow(-2f)
    val liveRmsDb: StateFlow<Float> = _liveRmsDb.asStateFlow()

    private val _partialTranscript = MutableStateFlow("")
    val partialTranscript: StateFlow<String> = _partialTranscript.asStateFlow()

    var isContinuousListeningEnabled = false
    var isWakeWordModeEnabled = false
    var currentLanguageCode = "en-US" // or "bn-BD"
    var speechRate = 1.0f
    var speechPitch = 1.0f
    var isMuted = false
    var micSensitivity = 1.4f

    private var currentEnvelope = 0f
    private var speechPulseRunnable: Runnable? = null
    private var activeSpeechCallback: (() -> Unit)? = null

    init {
        initTts()
    }

    private fun startSpeechPulse() {
        stopSpeechPulse()
        var step = 0
        speechPulseRunnable = object : Runnable {
            override fun run() {
                if (_assistantState.value == AssistantState.SPEAKING) {
                    step++
                    val mockVol = 0.35f + 0.32f * kotlin.math.sin(step * 0.38).toFloat() + 0.18f * kotlin.math.cos(step * 0.72).toFloat()
                    val clamped = mockVol.coerceIn(0.12f, 0.95f)
                    _liveVolume.value = clamped
                    _liveRmsDb.value = (clamped * 12f) - 2f
                    mainHandler.postDelayed(this, 50)
                } else {
                    _liveVolume.value = 0f
                    _liveRmsDb.value = -2f
                }
            }
        }
        mainHandler.post(speechPulseRunnable!!)
    }

    private fun stopSpeechPulse() {
        speechPulseRunnable?.let { mainHandler.removeCallbacks(it) }
        speechPulseRunnable = null
    }

    fun updateVolume(rmsdB: Float) {
        _liveRmsDb.value = rmsdB
        val normalized = (((rmsdB + 2f) / 12f).coerceIn(0f, 1.2f) * micSensitivity).coerceIn(0f, 1f)
        if (normalized > currentEnvelope) {
            currentEnvelope = normalized
        } else {
            currentEnvelope = (currentEnvelope * 0.82f).coerceAtLeast(0f)
        }
        _liveVolume.value = currentEnvelope
    }

    fun injectSimulatedVolume(level: Float) {
        _liveRmsDb.value = (level * 14f) - 2f
        _liveVolume.value = level.coerceIn(0f, 1f)
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
                applyTtsLocale()
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        mainHandler.post {
                            _assistantState.value = AssistantState.SPEAKING
                            startSpeechPulse()
                        }
                    }

                    override fun onDone(utteranceId: String?) {
                        mainHandler.post {
                            stopSpeechPulse()
                            _assistantState.value = AssistantState.READY
                            activeSpeechCallback?.invoke()
                            activeSpeechCallback = null
                            onSpeechFinishedCallback?.invoke()

                            if (isContinuousListeningEnabled) {
                                mainHandler.postDelayed({
                                    if (isContinuousListeningEnabled && _assistantState.value == AssistantState.READY) {
                                        startListening()
                                    }
                                }, 500)
                            }
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        mainHandler.post {
                            stopSpeechPulse()
                            _assistantState.value = AssistantState.READY
                            activeSpeechCallback?.invoke()
                            activeSpeechCallback = null

                            if (isContinuousListeningEnabled) {
                                mainHandler.postDelayed({
                                    if (isContinuousListeningEnabled && _assistantState.value == AssistantState.READY) {
                                        startListening()
                                    }
                                }, 600)
                            }
                        }
                    }
                })
            } else {
                Log.e("AlexVoiceEngine", "TextToSpeech init failed")
            }
        }
    }

    private fun applyTtsLocale() {
        val tts = textToSpeech ?: return
        val locale = if (currentLanguageCode.startsWith("bn")) {
            Locale("bn", "BD")
        } else {
            Locale.US
        }
        val result = tts.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.language = Locale.US
        }
        tts.setPitch(speechPitch)
        tts.setSpeechRate(speechRate)
    }

    fun setLanguage(langCode: String) {
        currentLanguageCode = langCode
        applyTtsLocale()
    }

    fun setVoicePitchAndRate(pitch: Float, rate: Float) {
        speechPitch = pitch
        speechRate = rate
        textToSpeech?.setPitch(pitch)
        textToSpeech?.setSpeechRate(rate)
    }

    fun setContinuousListening(enabled: Boolean) {
        isContinuousListeningEnabled = enabled
        if (enabled) {
            if (_assistantState.value == AssistantState.READY) {
                startListening()
            }
        } else {
            if (_assistantState.value == AssistantState.LISTENING) {
                stopListening()
            }
        }
    }

    fun startListening() {
        stopSpeaking()
        stopSpeechPulse()
        currentEnvelope = 0f
        _liveVolume.value = 0f
        _liveRmsDb.value = -2f
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            destroyRecognizer()
            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(createRecognitionListener())
                }
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLanguageCode)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                }
                speechRecognizer?.startListening(intent)
                _assistantState.value = AssistantState.LISTENING
                _partialTranscript.value = ""
            } catch (e: Exception) {
                _assistantState.value = AssistantState.READY
            }
        } else {
            _assistantState.value = AssistantState.READY
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            // ignore
        }
        if (_assistantState.value == AssistantState.LISTENING) {
            _assistantState.value = AssistantState.READY
        }
        currentEnvelope = 0f
        _liveVolume.value = 0f
        _liveRmsDb.value = -2f
    }

    fun setState(state: AssistantState) {
        _assistantState.value = state
    }

    fun speak(text: String, onFinished: (() -> Unit)? = null) {
        activeSpeechCallback = onFinished
        if (isMuted) {
            _assistantState.value = AssistantState.READY
            onFinished?.invoke()
            if (isContinuousListeningEnabled) {
                mainHandler.postDelayed({
                    if (isContinuousListeningEnabled && _assistantState.value == AssistantState.READY) {
                        startListening()
                    }
                }, 500)
            }
            return
        }
        if (!isTtsInitialized) {
            initTts()
        }
        applyTtsLocale()
        _assistantState.value = AssistantState.SPEAKING
        startSpeechPulse()
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "alex_speech_${System.currentTimeMillis()}")
        }
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "alex_speech_id")
    }

    fun stopSpeaking() {
        stopSpeechPulse()
        try {
            textToSpeech?.stop()
        } catch (e: Exception) {
            // ignore
        }
        if (_assistantState.value == AssistantState.SPEAKING) {
            _assistantState.value = AssistantState.READY
        }
        currentEnvelope = 0f
        _liveVolume.value = 0f
        _liveRmsDb.value = -2f
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _assistantState.value = AssistantState.LISTENING
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {
                updateVolume(rmsdB)
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                currentEnvelope = 0f
                _liveVolume.value = 0f
                _liveRmsDb.value = -2f
            }

            override fun onError(error: Int) {
                currentEnvelope = 0f
                _liveVolume.value = 0f
                _liveRmsDb.value = -2f
                _assistantState.value = AssistantState.READY

                if (isContinuousListeningEnabled) {
                    mainHandler.postDelayed({
                        if (isContinuousListeningEnabled && _assistantState.value == AssistantState.READY) {
                            startListening()
                        }
                    }, 800)
                } else if (isWakeWordModeEnabled) {
                    mainHandler.postDelayed({
                        if (isWakeWordModeEnabled && _assistantState.value == AssistantState.READY) {
                            startListening()
                        }
                    }, 500)
                }
            }

            override fun onResults(results: Bundle?) {
                currentEnvelope = 0f
                _liveVolume.value = 0f
                _liveRmsDb.value = -2f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val transcript = matches?.firstOrNull() ?: ""
                _partialTranscript.value = transcript

                if (isWakeWordModeEnabled) {
                    val lower = transcript.lowercase()
                    if (lower.contains("hey alex") || lower.contains("alex") || lower.contains("শোনো অ্যালেক্স")) {
                        onWakeWordDetected()
                        val command = lower.substringAfter("alex").trim()
                        if (command.isNotBlank()) {
                            onTranscriptReady(command)
                        } else {
                            speak("Yes Master, I am listening.")
                        }
                    } else if (transcript.isNotBlank()) {
                        onTranscriptReady(transcript)
                    } else {
                        handleNoTranscript()
                    }
                } else if (transcript.isNotBlank()) {
                    onTranscriptReady(transcript)
                } else {
                    handleNoTranscript()
                }
            }

            private fun handleNoTranscript() {
                _assistantState.value = AssistantState.READY
                if (isContinuousListeningEnabled) {
                    mainHandler.postDelayed({
                        if (isContinuousListeningEnabled && _assistantState.value == AssistantState.READY) {
                            startListening()
                        }
                    }, 600)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partial = matches?.firstOrNull() ?: ""
                _partialTranscript.value = partial
                if (isWakeWordModeEnabled) {
                    val lower = partial.lowercase()
                    if (lower.contains("hey alex") || lower.contains("alex")) {
                        onWakeWordDetected()
                    }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    private fun destroyRecognizer() {
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            // ignore
        }
        speechRecognizer = null
    }

    fun release() {
        stopSpeechPulse()
        destroyRecognizer()
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (e: Exception) {
            // ignore
        }
    }
}
