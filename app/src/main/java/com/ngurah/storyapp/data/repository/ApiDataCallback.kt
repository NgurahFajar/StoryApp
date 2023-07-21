package com.ngurah.storyapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.ngurah.storyapp.R
import com.ngurah.storyapp.data.remote.response.*
import com.ngurah.storyapp.data.remote.retrofit.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiDataCallback(private val context: Context) {
    fun login(email: String, password: String, callback: (LoginResponse) -> Unit) {
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(it) }
                } else {
                    val loginError = LoginResponse(
                        null,
                        true,
                        context.getString(R.string.login_gagal)
                    )
                    callback(loginError)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val loginError = LoginResponse(
                    null,
                    true,
                    t.message.toString()
                )
                callback(loginError)
            }
        })
    }

    fun register(name: String, email: String, password: String, callback: (RegisterResponse) -> Unit) {
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(it) }
                } else {
                    val registerError = RegisterResponse(true, context.getString(R.string.register_gagal))
                    callback(registerError)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                val registerError = RegisterResponse(true, t.message.toString())
                callback(registerError)
            }
        })
    }

    fun uploadStory(
        bearer: String,
        file: MultipartBody.Part,
        description: RequestBody,
        callback: (UploadStoryAppResponse) -> Unit,
    ) {
        val client = ApiConfig.getApiService().uploadStory(bearer, file, description)
        client.enqueue(object : Callback<UploadStoryAppResponse> {
            override fun onResponse(
                call: Call<UploadStoryAppResponse>,
                response: Response<UploadStoryAppResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error!!) {
                        callback(responseBody)
                    } else {
                        val errorMessage = context.getString(R.string.story_gagal_diunggah)
                        val errorResponse = UploadStoryAppResponse(true, errorMessage)
                        callback(errorResponse)
                    }
                } else {
                    val errorMessage = context.getString(R.string.story_gagal_diunggah)
                    val errorResponse = UploadStoryAppResponse(true, errorMessage)
                    callback(errorResponse)
                }
            }

            override fun onFailure(call: Call<UploadStoryAppResponse>, t: Throwable) {
                val errorMessage = context.getString(R.string.story_gagal_diunggah)
                val errorResponse = UploadStoryAppResponse(true, errorMessage)
                callback(errorResponse)
            }
        })
    }


    fun getStoryMap(bearer: String, callback: (StoryAppMapsResponse) -> Unit) {
        val client = ApiConfig.getApiService().getMaps(bearer)

        client.enqueue(object : Callback<StoryAppMapsResponse> {
            override fun onResponse(call: Call<StoryAppMapsResponse>, response: Response<StoryAppMapsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    response.body()?.let { callback(it) }
                } else {
                    val storiesMap = StoryAppMapsResponse(null, true, context.getString(R.string.lokasigagal))
                    callback(storiesMap)
                }
            }

            override fun onFailure(call: Call<StoryAppMapsResponse>, t: Throwable) {
                val storiesMap = StoryAppMapsResponse(null, true, context.getString(R.string.lokasigagal))
                callback(storiesMap)
            }
        })
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: ApiDataCallback? = null

        fun getInstance(context: Context): ApiDataCallback =
            instance ?: synchronized(this) {
                instance ?: ApiDataCallback(context).also { instance = it }
            }
    }
}