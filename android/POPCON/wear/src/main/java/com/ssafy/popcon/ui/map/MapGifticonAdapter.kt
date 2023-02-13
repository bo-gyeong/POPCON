package com.ssafy.popcon.ui.map

import android.location.LocationManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.ItemMapGiftconBinding
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.ui.common.DragListener
import com.ssafy.popcon.util.Utils
import com.ssafy.popcon.viewmodel.WearViewModel

private const val TAG = "GifticonMap_싸피"

class MapGifticonAdpater(
    val viewModel: WearViewModel,
    val user: User,
    val lm: LocationManager
) :
    ListAdapter<Gifticon, MapGifticonAdpater.GifticonMapViewHolder>(BannerDiffCallback()) {
    private lateinit var binding: ItemMapGiftconBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifticonMapViewHolder {
        binding = ItemMapGiftconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GifticonMapViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: GifticonMapViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnDragListener(
            DragListener(
                getItem(position).barcodeNum,
                viewModel,
                user,
                lm
            )
        )
        holder.itemView.setOnLongClickListener { v ->
            longClickListener.onLongClick(v, getItem(position))
            true
        }
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

    private lateinit var longClickListener: OnLongClickListener

    interface OnLongClickListener {
        fun onLongClick(v: View, gifticon: Gifticon)
    }

    fun setOnLongClickListener(onLongClickListener: OnLongClickListener) {
        this.longClickListener = onLongClickListener
    }

    private lateinit var dragListener: DragListener

    fun setOnDragListener(onDragListener: DragListener) {
        this.dragListener = onDragListener
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
