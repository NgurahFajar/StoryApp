package com.ngurah.storyapp.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ngurah.storyapp.databinding.ActivitySplashscreenBinding
import com.ngurah.storyapp.ui.viewmodel.SplashViewModel
import com.ngurah.storyapp.ui.viewmodelfactory.ViewModelFactory
import com.ngurah.storyapp.utils.DELAY

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity:AppCompatActivity() {

    private lateinit var binding: ActivitySplashscreenBinding
    private val splashViewModel: SplashViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            splashViewModel.getUserStory().observe(this) {user ->
                if (user.userId?.isEmpty() == true) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }, DELAY)
    }
}