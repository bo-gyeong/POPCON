package com.ssafy.popcon.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.ItemHistoryBinding
import com.ssafy.popcon.databinding.ItemHomeGifticonBinding
import com.ssafy.popcon.dto.Gifticon

class HistoryAdapter(private val clickListener: HistoryListener) :
    ListAdapter<Gifticon, HistoryAdapter.GifticonViewHolder>(GifticonDiffCallback()) {
    private lateinit var binding: ItemHistoryBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifticonViewHolder {
        binding =
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GifticonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GifticonViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    inner class GifticonViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(gifticon: Gifticon, clickListener: HistoryListener) {
            binding.gifticon = gifticon
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    class HistoryListener(val clickListener: (history: Gifticon) -> Unit) {
        fun onClick(history: Gifticon) = clickListener(history)
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
