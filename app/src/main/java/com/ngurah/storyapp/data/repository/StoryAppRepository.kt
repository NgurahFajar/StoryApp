package com.ngurah.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.ngurah.storyapp.data.local.StoryAppDatabase
import com.ngurah.storyapp.data.local.UserAppPreference
import com.ngurah.storyapp.data.remote.response.*
import com.ngurah.storyapp.data.remote.retrofit.ApiService
import com.ngurah.storyapp.mediator.RemoteMediator
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryAppRepository private constructor(
    private val apiService: ApiService,
    private val storyAppDatabase: StoryAppDatabase,
    private val apiDataCallback: ApiDataCallback,
    private val userAppPreference: UserAppPreference
) {

    fun register(name: String, email: String, password: String): LiveData<RegisterResponse> {
        val registerResponse = MutableLiveData<RegisterResponse>()

        apiDataCallback.register(name, email, password) { register ->
            registerResponse.postValue(register)
        }
        return registerResponse
    }

    fun login(email: String, password: String): LiveData<LoginResponse> {
        val loginResponse = MutableLiveData<LoginResponse>()
        apiDataCallback.login(email, password) { login ->
            loginResponse.postValue(login)
            if (!login.error!!) {
                login.loginResult?.let { userAppPreference.setUserStory(it) }
            }
        }
        return loginResponse
    }

    fun getUserStory(): LiveData<LoginResult> {
        val user = MutableLiveData<LoginResult>()
        user.postValue(userAppPreference.getUserStory())
        return user
    }

    fun deleteUser() {
        userAppPreference.deleteUser()
    }

    fun getStories(bearer: String): LiveData<PagingData<StoryAppItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = RemoteMediator(storyAppDatabase, apiService, bearer),
            pagingSourceFactory = {
                storyAppDatabase.storyAppDao().getStoriesUser()
            }
        ).liveData
    }

    fun uploadFile(
        bearer: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): LiveData<UploadStoryAppResponse> {
        val uploadResponseStatus = MutableLiveData<UploadStoryAppResponse>()

        apiDataCallback.uploadStory(bearer, file, description) { response ->
            uploadResponseStatus.postValue(response)
        }

        return uploadResponseStatus
    }


    fun getStoryMap(bearer: String): LiveData<StoryAppMapsResponse> {
        val storiesMap = MutableLiveData<StoryAppMapsResponse>()

        apiDataCallback.getStoryMap(bearer) { stories ->
            storiesMap.postValue(stories)
        }
        return storiesMap
    }


    companion object {
        @Volatile
        private var instance: StoryAppRepository? = null

        @JvmStatic
        fun getInstance(
            apiService: ApiService,
            storyAppDatabase: StoryAppDatabase,
            apiDataCallback: ApiDataCallback,
            userAppPreferences: UserAppPreference
        ): StoryAppRepository =
            instance ?: synchronized(this) {
                instance ?: StoryAppRepository(
                    apiService,
                    storyAppDatabase,
                    apiDataCallback,
                    userAppPreferences
                )
            }.also { instance = it }
    }
}
