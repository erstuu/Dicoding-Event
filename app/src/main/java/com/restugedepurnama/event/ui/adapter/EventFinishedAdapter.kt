package com.restugedepurnama.event.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.restugedepurnama.event.data.local.entity.EventEntity
import com.restugedepurnama.event.databinding.ItemCardBinding

class EventFinishedAdapter(
    private val onClickedItem: (EventEntity) -> Unit
) : ListAdapter<EventEntity, EventFinishedAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickedItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemCardBinding,
        private val onClickedItem: (EventEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EventEntity) {
            binding.tvTitle.text = item.name

            Glide.with(binding.root)
                .load(item.imageLogo)
                .into(binding.ivEvent)

            binding.root.setOnClickListener {
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