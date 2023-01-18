package com.ssafy.popcon.ui.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.ItemAddImgBinding
import com.ssafy.popcon.dto.GifticonImg
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.home.HomeFragment

class AddImgAdapter(var imgUriList: ArrayList<GifticonImg>, _onItemClick: onItemClick):
    RecyclerView.Adapter<AddImgAdapter.AddImgViewHolder>() {
    private lateinit var binding: ItemAddImgBinding
    private lateinit var mainActivity: MainActivity
    private val onItemClick = _onItemClick

    inner class AddImgViewHolder(private val binding: ItemAddImgBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(gifticonImg: GifticonImg){
            binding.cvCouponImg.setOnClickListener {
                onItemClick.onClick(bindingAdapterPosition)
            }
            binding.gifticonImg = gifticonImg
            binding.btnRemove.setOnClickListener {
                imgUriList.removeAt(bindingAdapterPosition)
                notifyItemRemoved(bindingAdapterPosition)

                if (imgUriList.size == 0){
                    mainActivity.changeFragment(HomeFragment())
                } else{
                    onItemClick.onClick(0)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddImgViewHolder {
        binding = ItemAddImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        mainActivity = parent.context as MainActivity

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