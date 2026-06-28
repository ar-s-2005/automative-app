package com.example.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.databinding.ItemBrandCardBinding

class BrandAdapter(
    private val brands: List<String>,
    private val onBrandSelected: (String) -> Unit
) : RecyclerView.Adapter<BrandAdapter.BrandViewHolder>() {

    private var selectedIndex: Int = -1

    inner class BrandViewHolder(private val binding: ItemBrandCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(brand: String, position: Int) {
            binding.brandName.text = brand

            // Premium visual feedback based on selection
            if (position == selectedIndex) {
                binding.rootCard.setCardBackgroundColor(0xFFD4AF37.toInt()) // Gold
                binding.brandName.setTextColor(Color.BLACK)
            } else {
                binding.rootCard.setCardBackgroundColor(0xFF1C1C24.toInt()) // Deep Card Dark
                binding.brandName.setTextColor(Color.WHITE)
            }

            binding.root.setOnClickListener {
                val previousIndex = selectedIndex
                selectedIndex = if (selectedIndex == position) -1 else position
                
                notifyItemChanged(previousIndex)
                notifyItemChanged(selectedIndex)
                
                val brandToPass = if (selectedIndex == -1) "All" else brand
                onBrandSelected(brandToPass)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val binding = ItemBrandCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.bind(brands[position], position)
    }

    override fun getItemCount(): Int = brands.size
}
