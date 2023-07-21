package com.ngurah.storyapp.ui.viewmodelfactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ngurah.storyapp.data.repository.StoryAppRepository
import com.ngurah.storyapp.di.Injection
import com.ngurah.storyapp.ui.viewmodel.*

class ViewModelFactory private constructor(private val storyAppRepository: StoryAppRepository) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(storyAppRepository) as T
                modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(storyAppRepository) as T
                modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(storyAppRepository) as T
                modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(storyAppRepository) as T
                modelClass.isAssignableFrom(AddStoryAppViewModel::class.java) -> AddStoryAppViewModel(storyAppRepository) as T
                modelClass.isAssignableFrom(MapsViewModel::class.java) -> MapsViewModel(storyAppRepository) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}