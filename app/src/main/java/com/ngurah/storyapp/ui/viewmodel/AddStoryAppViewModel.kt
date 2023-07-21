package com.ngurah.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ngurah.storyapp.data.remote.response.UploadStoryAppResponse
import com.ngurah.storyapp.data.repository.StoryAppRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryAppViewModel(private val storyAppRepository: StoryAppRepository) : ViewModel() {
    fun addStoryApp(bearer: String, file: MultipartBody.Part, description: RequestBody): LiveData<UploadStoryAppResponse> {
        return storyAppRepository.uploadFile(bearer,file, description)
    }
}