package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ChatMessageEntity::class, VoiceNoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AlexDatabase : RoomDatabase() {
    abstract fun alexDao(): AlexDao

    companion object {
        @Volatile
        private var INSTANCE: AlexDatabase? = null

        fun getInstance(context: Context): AlexDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlexDatabase::class.java,
                    "alex_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
