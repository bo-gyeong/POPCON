package com.ssafy.popcon.ui.map

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.popcon.databinding.ItemMapGiftconBinding
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.ui.popup.GifticonPreviewFragment.Companion.newInstance
import com.ssafy.popcon.ui.popup.GifticonViewFragment.Companion.newInstance
import com.ssafy.popcon.util.Utils

private const val TAG = "GifticonMap_싸피"

class MapGifticonAdpater() :
    ListAdapter<Gifticon, MapGifticonAdpater.GifticonMapViewHolder>(BannerDiffCallback()) {
    private lateinit var binding: ItemMapGiftconBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifticonMapViewHolder {
        binding = ItemMapGiftconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GifticonMapViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: GifticonMapViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GifticonMapViewHolder(private val binding: ItemMapGiftconBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(gifticon: Gifticon) {
            binding.gifticon = gifticon
            binding.badge = Utils.calDday(gifticon)
            binding.executePendingBindings()
        }
    }
}

class BannerDiffCallback : DiffUtil.ItemCallback<Gifticon>() {
    override fun areItemsTheSame(oldItem: Gifticon, newItem: Gifticon): Boolean {
        return oldItem.barcodeNum == newItem.barcodeNum
    }

    override fun areContentsTheSame(oldItem: Gifticon, newItem: Gifticon): Boolean {
        return oldItem == newItem
    }

}
