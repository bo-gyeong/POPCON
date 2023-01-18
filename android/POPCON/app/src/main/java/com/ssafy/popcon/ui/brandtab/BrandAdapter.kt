package com.ssafy.popcon.ui.brandtab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.popcon.databinding.ItemBrandTabBinding
import com.ssafy.popcon.dto.Brand

class BrandAdapter() : ListAdapter<Brand, BrandAdapter.BrandViewHolder>(BrandDiffCallback()) {
    private lateinit var binding: ItemBrandTabBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        binding = ItemBrandTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BrandViewHolder(private val binding: ItemBrandTabBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(brand: Brand) {
            binding.brand = brand
            binding.executePendingBindings()
        }
    }
}

class BrandDiffCallback : DiffUtil.ItemCallback<Brand>() {
    override fun areItemsTheSame(oldItem: Brand, newItem: Brand): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Brand, newItem: Brand): Boolean {
        return oldItem == newItem
    }
}
