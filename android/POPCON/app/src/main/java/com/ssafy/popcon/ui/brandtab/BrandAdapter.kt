package com.ssafy.popcon.ui.brandtab

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.ItemBrandTabBinding
import com.ssafy.popcon.dto.Brand
import com.ssafy.popcon.dto.Gifticon
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.generated.callback.OnClickListener
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.GifticonViewModel

class BrandAdapter(private val clickListener : BrandListener) :
    ListAdapter<Brand, BrandAdapter.BrandViewHolder>(BrandDiffCallback()) {
    var index: Int = 0
    private lateinit var binding: ItemBrandTabBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        binding = ItemBrandTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)

        if (index == position) {
            binding.viewBrandTab.setBackgroundResource(R.drawable.edge_brand_tab_select)
        } else {
            binding.viewBrandTab.setBackgroundResource(R.drawable.edge_brand_tab)
        }

        holder.itemView.setOnClickListener {
            index = position
        }
    }

    inner class BrandViewHolder(private val binding: ItemBrandTabBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(brand: Brand, clickListener: BrandListener) {
            binding.brand = brand
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    class BrandListener(val clickListener: (brand: Brand) -> Unit) {
        fun onClick(brand: Brand) = clickListener(brand)
    }
}

class BrandDiffCallback : DiffUtil.ItemCallback<Brand>() {
    override fun areItemsTheSame(oldItem: Brand, newItem: Brand): Boolean {
        return oldItem.brandName == newItem.brandName
    }

    override fun areContentsTheSame(oldItem: Brand, newItem: Brand): Boolean {
        return oldItem == newItem
    }
}
