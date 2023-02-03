package com.ssafy.popcon.ui.brandtab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.ItemBrandTabBinding
import com.ssafy.popcon.dto.Brand

class BrandAdapter() :
    ListAdapter<Brand, BrandAdapter.BrandViewHolder>(BrandDiffCallback()) {
    var index: Int = 0
    private lateinit var binding: ItemBrandTabBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        binding = ItemBrandTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.bind(getItem(position))
        val brandView: ConstraintLayout = holder.itemView.findViewById(R.id.view_brand_tab)

        if (index == position) {
            brandView.setBackgroundResource(R.drawable.edge_brand_tab_select)
        } else {
            brandView.setBackgroundResource(R.drawable.edge_brand_tab)
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, getItem(position).brandName)
            index = position

            notifyDataSetChanged()
        }
    }

    inner class BrandViewHolder(private val binding: ItemBrandTabBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(brand: Brand) {
            binding.brand = brand

            binding.executePendingBindings()
        }
    }

    //리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, brandName: String)
    }

    //외부에서 클릭 이벤트
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {

        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener: OnItemClickListener
}

class BrandDiffCallback : DiffUtil.ItemCallback<Brand>() {
    override fun areItemsTheSame(oldItem: Brand, newItem: Brand): Boolean {
        return oldItem.brandName == newItem.brandName
    }

    override fun areContentsTheSame(oldItem: Brand, newItem: Brand): Boolean {
        return oldItem == newItem
    }
}
