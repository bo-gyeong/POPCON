package com.ssafy.popcon.ui.map

import android.annotation.SuppressLint
import android.location.LocationManager
import android.os.Build
import android.view.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.ItemMapGiftconBinding
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.ui.common.WearDragListener
import com.ssafy.popcon.util.Utils
import com.ssafy.popcon.viewmodel.WearViewModel

private const val TAG = "GifticonMap_싸피"

class MapGifticonAdpater(
    val target : View,
    val viewModel: WearViewModel,
    val user: User
) :
    ListAdapter<Gifticon, MapGifticonAdpater.GifticonMapViewHolder>(BannerDiffCallback()) {
    private lateinit var binding: ItemMapGiftconBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifticonMapViewHolder {
        binding = ItemMapGiftconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GifticonMapViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: GifticonMapViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnDragListener(
            WearDragListener(target,
                getItem(position).barcodeNum,
                viewModel,
                user
            )
        )
        holder.itemView.setOnLongClickListener { v ->
            longClickListener.onLongClick(v, getItem(position))
            true
        }
    }

    private lateinit var longClickListener: OnLongClickListener

    interface OnLongClickListener {
        fun onLongClick(v: View, gifticon: Gifticon)
    }

    fun setOnLongClickListener(onLongClickListener: OnLongClickListener) {
        this.longClickListener = onLongClickListener
    }

    private lateinit var wearDragListener: WearDragListener

    fun setOnDragListener(onWearDragListener: WearDragListener) {
        this.wearDragListener = onWearDragListener
    }

    inner class GifticonMapViewHolder(val binding: ItemMapGiftconBinding) :
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
