package com.ngurah.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ngurah.storyapp.data.remote.response.StoryAppItem
import com.ngurah.storyapp.data.repository.StoryAppRepository

class MainViewModel(private val storyAppRepository: StoryAppRepository) : ViewModel() {
    fun getStoriesUser(bearer:String) : LiveData<PagingData<StoryAppItem>> =
        storyAppRepository.getStories(bearer).cachedIn(viewModelScope)
    fun deleteUser() = storyAppRepository.deleteUser()
}