package com.ngurah.storyapp.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ngurah.storyapp.R
import com.ngurah.storyapp.databinding.ActivityRegisterBinding
import com.ngurah.storyapp.ui.viewmodel.RegisterViewModel
import com.ngurah.storyapp.ui.viewmodelfactory.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            tvLogin.setOnClickListener {
                onBackPressed()
            }

            registerButton.setOnClickListener {
                registerButton.isEnabled = false
                tvLogin.isEnabled = false
                progressBar.visibility = View.VISIBLE
                performRegistration()
            }
        }
    }

    private fun performRegistration() {
        val name = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        registerViewModel.registerUser(name, email, password).observe(this) { registerUser ->
            if (registerUser.error == true) {
                registerUser.message?.let { showToast(it) }
            } else {
                showToast(getString(R.string.registration_success))
                navigateToLogin()
            }
            enableViews()
            hideProgressBar()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun enableViews() {
        binding.apply {
            registerButton.isEnabled = true
            tvLogin.isEnabled = true
        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

}