package com.ssafy.popcon.ui.home

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.ItemHomeGifticonBinding
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.util.Utils
import com.ssafy.popcon.viewmodel.GifticonViewModel

class GiftconAdapter(private val viewModel: GifticonViewModel) :
    ListAdapter<Gifticon, GiftconAdapter.GifticonViewHolder>(GifticonDiffCallback()) {
    private lateinit var binding: ItemHomeGifticonBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifticonViewHolder {
        binding =
            ItemHomeGifticonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GifticonViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: GifticonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GifticonViewHolder(private val binding: ItemHomeGifticonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(gifticon: Gifticon) {
            binding.gifticon = gifticon
            binding.badge = Utils.calDday(gifticon)
            binding.viewModel = viewModel
            binding.bgBlackR.isVisible = gifticon.state == 2

            binding.executePendingBindings()
        }
    }
}

class GifticonDiffCallback : DiffUtil.ItemCallback<Gifticon>() {
    override fun areItemsTheSame(oldItem: Gifticon, newItem: Gifticon): Boolean {
        return oldItem.barcodeNum == newItem.barcodeNum
    }

    override fun areContentsTheSame(oldItem: Gifticon, newItem: Gifticon): Boolean {
        return oldItem == newItem
    }
}
