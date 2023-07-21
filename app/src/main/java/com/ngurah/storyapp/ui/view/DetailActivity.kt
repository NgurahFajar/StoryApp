package com.ngurah.storyapp.ui.view

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ngurah.storyapp.R
import com.ngurah.storyapp.data.remote.response.StoryAppItem
import com.ngurah.storyapp.databinding.ActivityDetailBinding
import java.text.SimpleDateFormat
import java.util.*


class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        val story = getStoryFromIntent()
        setupViews(story)
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detail_story)
    }

    private fun getStoryFromIntent(): StoryAppItem {
        return intent.getParcelableExtra<StoryAppItem>(ITEM) as StoryAppItem
    }

    private fun setupViews(story: StoryAppItem) {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            setupImageLoading(story.photoUrl)
            tvUsername.text = story.name
            tvCreated.text = formatDate(story.createdAt)
            tvPostDescription.text = story.description
        }
    }

    private fun setupImageLoading(photoUrl: String) {
        Glide.with(applicationContext)
            .load(photoUrl)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, getString(R.string.image_load_failed), Toast.LENGTH_SHORT).show()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(binding.ivImagestory)
    }

    private fun formatDate(timestamp: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date: Date? = inputFormat.parse(timestamp)
        return date?.let { outputFormat.format(it) } ?: ""
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val ITEM = "item"
    }
}