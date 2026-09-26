package com.example.data

data class AppHubItem(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val webUrl: String,
    val packageName: String?,
    val voiceKeywords: List<String>,
    val description: String = "",
    val category: String = "POPULAR"
)

object AppHubRepository {
    val appsList = listOf(
        AppHubItem(
            id = "youtube",
            name = "YouTube",
            iconEmoji = "📺",
            webUrl = "https://www.youtube.com",
            packageName = "com.google.android.youtube",
            voiceKeywords = listOf("youtube", "ইউটিউব", "video", "ভিডিও"),
            description = "Videos, Music, & Streams",
            category = "ENTERTAINMENT"
        ),
        AppHubItem(
            id = "facebook",
            name = "Facebook",
            iconEmoji = "📘",
            webUrl = "https://www.facebook.com",
            packageName = "com.facebook.katana",
            voiceKeywords = listOf("facebook", "ফেসবুক", "fb"),
            description = "Connect with friends",
            category = "SOCIAL"
        ),
        AppHubItem(
            id = "messenger",
            name = "Messenger",
            iconEmoji = "💬",
            webUrl = "https://m.messenger.com",
            packageName = "com.facebook.orca",
            voiceKeywords = listOf("messenger", "মেসেঞ্জার"),
            description = "Instant chats & calls",
            category = "SOCIAL"
        ),
        AppHubItem(
            id = "instagram",
            name = "Instagram",
            iconEmoji = "📷",
            webUrl = "https://www.instagram.com",
            packageName = "com.instagram.android",
            voiceKeywords = listOf("instagram", "ইনস্টাগ্রাম", "ইন্সটাগ্রাম", "insta"),
            description = "Photos, Reels & Stories",
            category = "SOCIAL"
        ),
        AppHubItem(
            id = "tiktok",
            name = "TikTok",
            iconEmoji = "🎵",
            webUrl = "https://www.tiktok.com",
            packageName = "com.zhiliaoapp.musically",
            voiceKeywords = listOf("tiktok", "টিকটক"),
            description = "Short viral videos",
            category = "ENTERTAINMENT"
        ),
        AppHubItem(
            id = "whatsapp",
            name = "WhatsApp",
            iconEmoji = "💚",
            webUrl = "https://web.whatsapp.com",
            packageName = "com.whatsapp",
            voiceKeywords = listOf("whatsapp", "হোয়াটসঅ্যাপ", "হোয়াটসঅ্যাপ"),
            description = "Encrypted messaging",
            category = "SOCIAL"
        ),
        AppHubItem(
            id = "google",
            name = "Google",
            iconEmoji = "🌐",
            webUrl = "https://www.google.com",
            packageName = "com.google.android.googlequicksearchbox",
            voiceKeywords = listOf("google", "গুগল", "search", "সার্চ"),
            description = "Search the world's info",
            category = "BROWSER"
        ),
        AppHubItem(
            id = "gmail",
            name = "Gmail",
            iconEmoji = "📧",
            webUrl = "https://mail.google.com",
            packageName = "com.google.android.gm",
            voiceKeywords = listOf("gmail", "জিমেইল", "mail", "মেইল", "ইমেইল"),
            description = "Email communication",
            category = "PRODUCTIVITY"
        ),
        AppHubItem(
            id = "playstore",
            name = "Play Store",
            iconEmoji = "🎮",
            webUrl = "https://play.google.com",
            packageName = "com.android.vending",
            voiceKeywords = listOf("play store", "প্লে স্টোর", "প্লেস্টোর", "playstore", "apps"),
            description = "Android Apps & Games",
            category = "TOOLS"
        ),
        AppHubItem(
            id = "spotify",
            name = "Spotify",
            iconEmoji = "🎧",
            webUrl = "https://open.spotify.com",
            packageName = "com.spotify.music",
            voiceKeywords = listOf("spotify", "স্পটিফাই", "music", "গান"),
            description = "Stream songs & podcasts",
            category = "ENTERTAINMENT"
        ),
        AppHubItem(
            id = "twitter",
            name = "X (Twitter)",
            iconEmoji = "🐦",
            webUrl = "https://x.com",
            packageName = "com.twitter.android",
            voiceKeywords = listOf("twitter", "টুইটার", "x"),
            description = "Real-time updates",
            category = "SOCIAL"
        ),
        AppHubItem(
            id = "telegram",
            name = "Telegram",
            iconEmoji = "✈️",
            webUrl = "https://web.telegram.org",
            packageName = "org.telegram.messenger",
            voiceKeywords = listOf("telegram", "টেলিগ্রাম"),
            description = "Cloud-based chat channels",
            category = "SOCIAL"
        )
    )

    fun findAppByVoiceQuery(query: String): AppHubItem? {
        val lower = query.lowercase().trim()
        return appsList.firstOrNull { app ->
            app.voiceKeywords.any { keyword ->
                lower.contains(keyword)
            }
        }
    }
}
