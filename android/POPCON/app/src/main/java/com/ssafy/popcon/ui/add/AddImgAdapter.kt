package com.ssafy.popcon.ui.add

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.popcon.databinding.ItemAddImgBinding
import com.ssafy.popcon.dto.GifticonImg

class AddImgAdapter(var imgUriList: ArrayList<GifticonImg>, _onItemClick: onItemClick):
    RecyclerView.Adapter<AddImgAdapter.AddImgViewHolder>() {
    private lateinit var binding: ItemAddImgBinding
    private val onItemClick = _onItemClick

    inner class AddImgViewHolder(private val binding: ItemAddImgBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(gifticonImg: GifticonImg){
            binding.cvCouponImg.setOnClickListener {
                onItemClick.onClick(bindingAdapterPosition)
            }
            binding.ivCouponImg.setImageURI(gifticonImg.imgUri)
            binding.btnRemove.setOnClickListener {
                imgUriList.removeAt(bindingAdapterPosition)
                notifyItemRemoved(bindingAdapterPosition)

                if (imgUriList.size == 0){
                    //홈화면으로
                } else{
                    onItemClick.onClick(0)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddImgViewHolder {
        binding = ItemAddImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddImgViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddImgViewHolder, position: Int) {
        holder.apply {
            bind(imgUriList[position])
        }
    }

    override fun getItemCount(): Int {
        return imgUriList.size
    }
}