package com.ssafy.popcon.ui.home.brandtab

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
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.util.SharedPreferencesUtil
import com.ssafy.popcon.viewmodel.GifticonViewModel

class BrandAdapter(val viewModel: GifticonViewModel, val user: User) :
    ListAdapter<Brand, BrandAdapter.BrandViewHolder>(BrandDiffCallback()) {
    var index: Int = 0
    private lateinit var itemClickListener: OnItemClickListener
    private lateinit var binding: ItemBrandTabBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        binding = ItemBrandTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.bind(getItem(position))

        if (index == position) {
            binding.viewBrandTab.setBackgroundResource(R.drawable.edge_brand_tab_select)
        } else {
            binding.viewBrandTab.setBackgroundResource(R.drawable.edge_brand_tab)
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
            index = position
        }
    }

    inner class BrandViewHolder(private val binding: ItemBrandTabBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(brand: Brand) {
            binding.brand = brand
            binding.viewModel = viewModel
            binding.user = user
            binding.executePendingBindings()
        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
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
