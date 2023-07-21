package com.ngurah.storyapp.ui.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ngurah.storyapp.R
import com.ngurah.storyapp.databinding.ActivityMainBinding
import com.ngurah.storyapp.ui.adapter.LoadingStateAdapter
import com.ngurah.storyapp.ui.adapter.MainAdapter
import com.ngurah.storyapp.ui.viewmodel.MainViewModel
import com.ngurah.storyapp.ui.viewmodelfactory.ViewModelFactory
import com.ngurah.storyapp.utils.PREFS_NAME
import com.ngurah.storyapp.utils.TOKEN


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var bearer: String = ""
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        val preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        bearer = "Bearer ${preferences.getString(TOKEN, "").toString()}"
        binding.apply {
            val mainAdapter = MainAdapter()
            rvStorymain.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = mainAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        mainAdapter.retry()
                    }
                )
            }

            mainViewModel.getStoriesUser(bearer).observe(this@MainActivity) { pagingData ->
                mainAdapter.submitData(lifecycle, pagingData)
                if (mainAdapter.itemCount > 0) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupActionBar() {
        supportActionBar?.title = getString(R.string.app_name)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_add_story -> {
                val intent = Intent(this@MainActivity, AddStoryAppActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.action_map -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.action_logout -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.logout)
                    .setMessage(R.string.keluar_dari_akun)
                    .setPositiveButton(R.string.ya) { _, _ ->
                        logout()
                    }
                    .setNegativeButton(R.string.tidak, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        Toast.makeText(this, R.string.anda_telah_keluar, Toast.LENGTH_SHORT).show()
        mainViewModel.deleteUser()
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}

