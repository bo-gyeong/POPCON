package com.ssafy.popcon.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.ItemHomeGifticonBinding
import com.ssafy.popcon.dto.Gifticon

class HistoryAdapter :
    ListAdapter<Gifticon, HistoryAdapter.GifticonViewHolder>(GifticonDiffCallback()) {
    private lateinit var binding: ItemHomeGifticonBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifticonViewHolder {
        binding =
            ItemHomeGifticonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GifticonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GifticonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GifticonViewHolder(private val binding: ItemHomeGifticonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(gifticon: Gifticon) {
            binding.gifticon = gifticon
            binding.executePendingBindings()
        }
    }
}

class GifticonDiffCallback : DiffUtil.ItemCallback<Gifticon>() {
    override fun areItemsTheSame(oldItem: Gifticon, newItem: Gifticon): Boolean {
        return oldItem.number == newItem.number
    }

    override fun areContentsTheSame(oldItem: Gifticon, newItem: Gifticon): Boolean {
        return oldItem == newItem
    }
}
