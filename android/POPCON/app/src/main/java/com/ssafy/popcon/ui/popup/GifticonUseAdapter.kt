package com.ssafy.popcon.ui.popup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.ItemGifticonSliderBinding
import com.ssafy.popcon.databinding.ItemHomeGifticonBinding
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.home.GiftconAdapter

class GifticonUseAdapter() :
    ListAdapter<Gifticon, GifticonUseAdapter.GifticonViewHolder>(GifticonDiffCallback()) {
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
            binding.gifitcon = gifticon
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
