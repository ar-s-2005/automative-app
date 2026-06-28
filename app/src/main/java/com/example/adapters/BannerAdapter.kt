package com.example.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.databinding.ItemHeroBannerBinding
import com.example.models.Car

class BannerAdapter(
    private val bannerCars: List<Car>,
    private val onBannerClick: (Car) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(private val binding: ItemHeroBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(car: Car) {
            binding.bannerTitle.text = "${car.brand} ${car.model}"
            binding.bannerPrice.text = car.price
            binding.bannerHp.text = "${car.hp} HP • Turbocharged"

            Glide.with(binding.root.context)
                .load(car.imageUrl)
                .centerCrop()
                .into(binding.bannerImage)

            binding.root.setOnClickListener {
                onBannerClick(car)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemHeroBannerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(bannerCars[position])
    }

    override fun getItemCount(): Int = bannerCars.size
}
