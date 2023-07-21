package com.ngurah.storyapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ngurah.storyapp.data.remote.response.StoryAppItem

@Database(entities = [StoryAppItem::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class StoryAppDatabase: RoomDatabase() {
    abstract fun storyAppDao(): StoryAppDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var instance: StoryAppDatabase? = null
        fun getInstance(context: Context): StoryAppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryAppDatabase::class.java, "Story.db"
                ).build()
            }
    }
}