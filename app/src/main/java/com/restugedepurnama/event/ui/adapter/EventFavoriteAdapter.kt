package com.restugedepurnama.event.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.restugedepurnama.event.R
import com.restugedepurnama.event.data.local.entity.EventEntity
import com.restugedepurnama.event.databinding.ItemCardFavoriteBinding
import com.restugedepurnama.event.ui.viewModel.MainViewModel

class EventFavoriteAdapter(
    private val onClickedItem: (EventEntity) -> Unit,
    private val viewModel: MainViewModel
) : ListAdapter<EventEntity, EventFavoriteAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventFavoriteAdapter.ViewHolder {
        val binding = ItemCardFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventFavoriteAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemCardFavoriteBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val btnFavorite: Button = binding.btnFavorite

        fun bind(event: EventEntity) {
            binding.tvTitle.text = event.name

            Glide.with(binding.root)
                .load(event.imageLogo)
                .into(binding.ivEvent)

            binding.root.setOnClickListener {
                onClickedItem(event)
            }

            if (event.favorite) {
                btnFavorite.background = AppCompatResources.getDrawable(itemView.context, R.drawable.baseline_favorite_24)
            } else {
                btnFavorite.background = AppCompatResources.getDrawable(itemView.context, R.drawable.baseline_favorite_border_24)
            }

            btnFavorite.setOnClickListener {
                event.favorite = !event.favorite
                btnFavorite.background = if (event.favorite) {
                    AppCompatResources.getDrawable(itemView.context, R.drawable.baseline_favorite_24)
                } else {
                    AppCompatResources.getDrawable(itemView.context, R.drawable.baseline_favorite_border_24)
                }

                viewModel.updateFavoriteStatus(event, event.favorite)

                val message = if (event.favorite) "Added to favorites" else "Removed from favorites"
                Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<EventEntity>() {
        override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
            return oldItem == newItem
        }
    }
}