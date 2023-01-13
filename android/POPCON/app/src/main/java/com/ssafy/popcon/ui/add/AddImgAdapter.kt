package com.ssafy.popcon.ui.add

import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.popcon.databinding.ItemAddImgBinding
import com.ssafy.popcon.dto.GifticonImg

//class AddImgAdapter:
//    ListAdapter<GifticonImg, AddImgAdapter.AddImgViewHolder>(AddImgCallback()) {
//        inner class AddImgViewHolder(private val binding: ItemAddImgBinding):
//                RecyclerView.ViewHolder(binding.root){
//                    fun bind(gifticonImg: GifticonImg){
//                        //Glide.get()
//                    }
//                }
//}

class AddImgCallback: DiffUtil.ItemCallback<GifticonImg>(){
    override fun areItemsTheSame(oldItem: GifticonImg, newItem: GifticonImg): Boolean {
        return oldItem.imgUri == newItem.imgUri
    }

    override fun areContentsTheSame(oldItem: GifticonImg, newItem: GifticonImg): Boolean {
        return oldItem == newItem
    }
}