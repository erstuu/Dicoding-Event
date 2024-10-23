package com.restugedepurnama.event.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.restugedepurnama.event.data.local.entity.EventEntity
import com.restugedepurnama.event.databinding.ItemCardBannerBinding

class EventUpcomingAdapter(
    private val onClickedItem: (EventEntity) -> Unit
) : ListAdapter<EventEntity, EventUpcomingAdapter.ViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickedItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemCardBannerBinding,
        private val onClickedItem: (EventEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EventEntity) {
            binding.tvTitle.text = item.name

            Glide.with(binding.root)
                .load(item.mediaCover)
                .into(binding.ivEvent)

            binding.clBanner.setOnClickListener {
                onClickedItem(item)
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