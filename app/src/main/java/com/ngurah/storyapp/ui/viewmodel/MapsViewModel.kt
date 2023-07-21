package com.ngurah.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ngurah.storyapp.data.repository.StoryAppRepository

class MapsViewModel(private val storyAppRepository: StoryAppRepository) : ViewModel() {
    fun getStoryMap(bearer: String) =
        storyAppRepository.getStoryMap(bearer)
}