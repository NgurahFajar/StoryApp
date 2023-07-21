package com.ngurah.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ngurah.storyapp.data.remote.response.LoginResponse
import com.ngurah.storyapp.data.repository.StoryAppRepository


class LoginViewModel(private val storyAppRepository: StoryAppRepository): ViewModel() {
    fun login(email: String, password: String): LiveData<LoginResponse> {
        return storyAppRepository.login(email, password)
    }

}
