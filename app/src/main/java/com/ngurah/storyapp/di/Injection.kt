package com.ngurah.storyapp.di

import android.content.Context
import com.ngurah.storyapp.data.remote.retrofit.ApiConfig
import com.ngurah.storyapp.data.local.StoryAppDatabase
import com.ngurah.storyapp.data.local.UserAppPreference
import com.ngurah.storyapp.data.repository.ApiDataCallback
import com.ngurah.storyapp.data.repository.StoryAppRepository

object Injection {
    fun provideRepository(context: Context): StoryAppRepository {
        val apiService = ApiConfig.getApiService()
        val database = StoryAppDatabase.getInstance(context)
        val apiDataCallback = ApiDataCallback.getInstance(context)
        val userAppPreferences = UserAppPreference.getInstance(context)
        return StoryAppRepository.getInstance(apiService,
            database,
            apiDataCallback,
            userAppPreferences)
    }
}