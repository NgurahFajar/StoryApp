package com.ngurah.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class UploadStoryAppResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)
