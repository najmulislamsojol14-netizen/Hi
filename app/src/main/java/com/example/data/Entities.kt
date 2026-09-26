package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val role: String, // "user" or "alex"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "general", // "general", "code", "weather", "translate", "search"
    val isFavorite: Boolean = false
)

@Entity(tableName = "voice_notes")
data class VoiceNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "en-US"
)
