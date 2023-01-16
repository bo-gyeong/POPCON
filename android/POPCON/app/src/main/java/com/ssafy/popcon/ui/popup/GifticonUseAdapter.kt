package com.ssafy.popcon.ui.popup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.ItemGifticonSliderBinding
import com.ssafy.popcon.dto.Gifticon

class GifticonUseAdapter :
    ListAdapter<Gifticon, GifticonUseAdapter.GifticonViewHolder>(GifticonDiffCallback()) {
    private lateinit var binding: ItemGifticonSliderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifticonViewHolder {
        binding =
            ItemGifticonSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GifticonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GifticonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GifticonViewHolder(private val binding: ItemGifticonSliderBinding) :
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
