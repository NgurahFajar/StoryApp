package com.ngurah.storyapp

import com.ngurah.storyapp.data.remote.response.StoryAppItem

object DataStoryDummy {

    fun generateDummyStoryAppResponse(): List<StoryAppItem> {
        val items: MutableList<StoryAppItem> = arrayListOf()
        for (i in 0..100) {
            val story = StoryAppItem(
                i.toString(),
                i.toString(),
                i.toString(),
                i.toString(),
                i.toString(),
                i.toDouble(),
                i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}