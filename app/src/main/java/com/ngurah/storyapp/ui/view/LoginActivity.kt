package com.ngurah.storyapp.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ngurah.storyapp.R
import com.ngurah.storyapp.databinding.ActivityLoginBinding
import com.ngurah.storyapp.ui.viewmodel.LoginViewModel
import com.ngurah.storyapp.ui.viewmodelfactory.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            tvRegister.setOnClickListener {
                navigateToRegister()
            }

            btnLogin.setOnClickListener {
                if (areFieldsValid()) {
                    disableViews()
                    showProgressBar()
                    performLogin()
                }
            }
        }
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    private fun areFieldsValid(): Boolean {
        val emailValid = binding.emailEditText.error.isNullOrEmpty()
        val passwordValid = binding.passwordEditText.error.isNullOrEmpty()
        return emailValid && passwordValid
    }

    private fun disableViews() {
        binding.apply {
            btnLogin.isEnabled = false
            tvRegister.isEnabled = false
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun performLogin() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        loginViewModel.login(email, password).observe(this) { login ->
            if (login.error == true) {
                showToast(getString(R.string.login_gagal))
            } else {
                showToast(getString(R.string.login_berhasil))
                navigateToMain()
            }
            enableViews()
            hideProgressBar()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun enableViews() {
        binding.apply {
            btnLogin.isEnabled = true
            tvRegister.isEnabled = true
        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

}
