package com.ngurah.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ngurah.storyapp.data.remote.response.RegisterResponse
import com.ngurah.storyapp.data.repository.StoryAppRepository

class RegisterViewModel(private val storyAppRepository: StoryAppRepository) : ViewModel() {
    fun registerUser(name: String, email:String, password: String): LiveData<RegisterResponse> {
        return storyAppRepository.register(name, email, password)
    }
}