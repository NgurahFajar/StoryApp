package com.ngurah.storyapp.ui.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ngurah.storyapp.data.remote.response.StoryAppItem
import com.ngurah.storyapp.databinding.ItemMainBinding
import com.ngurah.storyapp.ui.view.DetailActivity
import java.text.SimpleDateFormat
import java.util.*


class MainAdapter : PagingDataAdapter<StoryAppItem, MainAdapter.MainViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val itemMainBinding = ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(itemMainBinding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val data = getItem(position)
        data?.let { holder.bind(it) }
        }


    class MainViewHolder(private val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoryAppItem) = with(binding) {
            tvUsername.text = data.name
            tvCreated.text = data.createdAt?.let { formatDate(it) }
            data.photoUrl?.let { setupImageLoading(it) }

            // penerapan Shared Element
            itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(ivImagestory, "image"),
                    Pair(tvUsername, "name"),
                    Pair(tvPost, "postingan"),
                    Pair(tvCreated, "waktu")
                )
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.ITEM, data)
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }

        private fun setupImageLoading(photoUrl: String) {
            Glide.with(itemView.context)
                .load(photoUrl)
                .centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
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
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryAppItem>() {
            override fun areItemsTheSame(oldItem: StoryAppItem, newItem: StoryAppItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryAppItem, newItem: StoryAppItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}


