package com.ssafy.popcon.ui.add

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.ItemAddImgBinding
import com.ssafy.popcon.dto.GifticonImg
import com.ssafy.popcon.ui.common.MainActivity
import com.ssafy.popcon.ui.home.HomeFragment

class AddImgAdapter(
    var OriginalImgUriList: ArrayList<GifticonImg>
    , var cropXyImgUriList: ArrayList<GifticonImg>
    , var barcodeImgUriList: ArrayList<GifticonImg>
    , _onItemClick: onItemClick
):
    RecyclerView.Adapter<AddImgAdapter.AddImgViewHolder>() {
    private lateinit var binding: ItemAddImgBinding
    private lateinit var mainActivity: MainActivity
    private val onItemClick = _onItemClick
    private var nowClick = 0
    private val positionSet = mutableSetOf<Int>()

    inner class AddImgViewHolder(private val binding: ItemAddImgBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(gifticonImg: GifticonImg){
            binding.ivChkClick.bringToFront()
            if (bindingAdapterPosition == 0){
                binding.ivChkClick.visibility = View.GONE
                positionSet.add(0)
            }

            if (bindingAdapterPosition == nowClick){
                binding.ivChkClick.visibility = View.GONE
                binding.cvCouponImg.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.popcon_orange))
            } else{
                binding.cvCouponImg.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.transparent))
            }

            binding.cvCouponImg.setOnClickListener {
                onItemClick.onClick(bindingAdapterPosition)
                binding.ivChkClick.visibility = View.GONE

                notifyItemChanged(nowClick)
                notifyItemChanged(bindingAdapterPosition)
                nowClick = bindingAdapterPosition

                positionSet.add(bindingAdapterPosition)
                AddFragment.chkCnt = positionSet.size
            }

            binding.gifticonImg = gifticonImg
            binding.btnRemove.setOnClickListener {
                OriginalImgUriList.removeAt(bindingAdapterPosition)
                cropXyImgUriList.removeAt(bindingAdapterPosition)
                barcodeImgUriList.removeAt(bindingAdapterPosition)
                notifyItemRemoved(bindingAdapterPosition)

                if (OriginalImgUriList.size == 0){
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
            bind(OriginalImgUriList[position])
        }
    }

    override fun getItemCount(): Int {
        return OriginalImgUriList.size
    }
}