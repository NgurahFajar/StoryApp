package com.ngurah.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ngurah.storyapp.data.repository.StoryAppRepository

class SplashViewModel(private val storyAppRepository: StoryAppRepository) : ViewModel() {
    fun getUserStory() = storyAppRepository.getUserStory()
}