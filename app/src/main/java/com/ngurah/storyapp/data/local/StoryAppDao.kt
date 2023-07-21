package com.ngurah.storyapp.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ngurah.storyapp.data.remote.response.StoryAppItem

@Dao
interface StoryAppDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStory(story: List<StoryAppItem>)

    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    fun getStoriesUser(): PagingSource<Int, StoryAppItem>

    @Query("DELETE FROM story")
    fun deleteAll()
}