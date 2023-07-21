package com.ngurah.storyapp.data.local

import android.content.Context
import com.ngurah.storyapp.data.remote.response.LoginResult
import com.ngurah.storyapp.utils.NAME
import com.ngurah.storyapp.utils.PREFS_NAME
import com.ngurah.storyapp.utils.TOKEN
import com.ngurah.storyapp.utils.USER_ID

class UserAppPreference(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var editor = prefs.edit()

    fun setUserStory(value: LoginResult) {
       editor.apply {
           putString(NAME, value.name)
           putString(USER_ID, value.userId)
           putString(TOKEN, value.token)
           apply()
       }
    }

    fun getUserStory(): LoginResult {
        editor.apply {
            val name = prefs.getString(NAME, "").toString()
            val userId = prefs.getString(USER_ID, "").toString()
            val token = prefs.getString(TOKEN, "").toString()
            return LoginResult(name, userId, token)
        }
    }

    fun deleteUser() {
        editor.apply {
            clear()
            apply()
        }
    }

    companion object{
        @Volatile
        private var instance: UserAppPreference? = null

        fun getInstance(context: Context): UserAppPreference =
            instance ?: synchronized(this) {
                instance ?: UserAppPreference(context)
            }
    }
}