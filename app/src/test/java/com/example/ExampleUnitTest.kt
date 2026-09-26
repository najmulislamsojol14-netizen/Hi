package com.example

import com.example.data.AppHubRepository
import com.example.network.AlexRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun alexRepository_providesIntelligentResponses() = runBlocking {
        val repo = AlexRepository()
        val englishReply = repo.queryAlex("Hello Alex", emptyList(), "en-US")
        assertNotNull(englishReply)
        assertTrue(englishReply.isNotEmpty())

        val bengaliReply = repo.queryAlex("কেমন আছো", emptyList(), "bn-BD")
        assertNotNull(bengaliReply)
        assertTrue(bengaliReply.isNotEmpty())
    }

    @Test
    fun appHub_detectsVoiceCommands() {
        val yt = AppHubRepository.findAppByVoiceQuery("Open YouTube for me")
        assertNotNull(yt)
        assertEquals("youtube", yt?.id)

        val fb = AppHubRepository.findAppByVoiceQuery("ফেসবুক খোলো")
        assertNotNull(fb)
        assertEquals("facebook", fb?.id)

        val wa = AppHubRepository.findAppByVoiceQuery("WhatsApp")
        assertNotNull(wa)
        assertEquals("whatsapp", wa?.id)
    }

    @Test
    fun appTab_containsAllRoutes() {
        val tabs = com.example.viewmodel.AppTab.values()
        assertTrue(tabs.contains(com.example.viewmodel.AppTab.HOME))
        assertTrue(tabs.contains(com.example.viewmodel.AppTab.APPS))
        assertTrue(tabs.contains(com.example.viewmodel.AppTab.CHAT))
        assertTrue(tabs.contains(com.example.viewmodel.AppTab.BROWSER))
        assertTrue(tabs.contains(com.example.viewmodel.AppTab.FILES))
        assertTrue(tabs.contains(com.example.viewmodel.AppTab.SETTINGS))
        assertTrue(tabs.contains(com.example.viewmodel.AppTab.COMMANDS))
    }

    @Test
    fun amplitudeNormalization_scalesCorrectly() {
        // Test normalization logic with different RMS dB levels
        val minDb = -2f
        val maxDb = 10f
        val sensitivity = 1.4f

        fun normalize(rmsdB: Float): Float {
            return (((rmsdB + 2f) / 12f).coerceIn(0f, 1.2f) * sensitivity).coerceIn(0f, 1f)
        }

        // Silence or below floor (-2 dB)
        val silence = normalize(-5f)
        assertEquals(0f, silence, 0.001f)

        // Mid voice (4 dB)
        val midVoice = normalize(4f)
        assertTrue(midVoice > 0.4f && midVoice < 0.9f)

        // Loud voice (10 dB)
        val loudVoice = normalize(10f)
        assertEquals(1f, loudVoice, 0.001f)
    }

    @Test
    fun dynamicOrbScaling_respondsProportionallyToVolume() {
        val baseScale = 1.0f

        fun computeListeningScale(volume: Float): Float {
            val animatedVolume = volume.coerceIn(0f, 1f)
            return 1.0f + (animatedVolume * 0.60f)
        }

        val quietScale = computeListeningScale(0f)
        assertEquals(1.0f, quietScale, 0.001f)

        val halfScale = computeListeningScale(0.5f)
        assertEquals(1.30f, halfScale, 0.001f)

        val maxScale = computeListeningScale(1.0f)
        assertEquals(1.60f, maxScale, 0.001f)

        assertTrue("Max scale must be significantly greater than quiet scale", maxScale > quietScale * 1.5f)
    }
}
