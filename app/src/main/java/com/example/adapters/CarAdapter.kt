package com.example.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.R
import com.example.databinding.ItemCarCardBinding
import com.example.models.Car

class CarAdapter(
    private val onCarClick: (Car) -> Unit,
    private val onWishlistClick: (Car) -> Unit,
    private val onCompareClick: (Car) -> Unit,
    private val isWishlisted: (Car) -> Boolean,
    private val isCompared: (Car) -> Boolean
) : ListAdapter<Car, CarAdapter.CarViewHolder>(CarDiffCallback()) {

    inner class CarViewHolder(private val binding: ItemCarCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(car: Car) {
            binding.carName.text = "${car.brand} ${car.model}"
            binding.brandBadge.text = car.brand.uppercase()
            binding.carPrice.text = car.price
            binding.carSpecs.text = "${car.year} • ${car.fuelType} • ${car.hp} HP"
            binding.carMileage.text = "${String.format("%,d", car.mileage)} km"

            // Load main car image
            Glide.with(binding.root.context)
                .load(car.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.carImage)

            // Wishlist heart rendering
            val wish = isWishlisted(car)
            if (wish) {
                binding.wishlistBtn.setImageResource(R.drawable.ic_heart_filled)
            } else {
                binding.wishlistBtn.setImageResource(R.drawable.ic_heart_empty)
            }

            // Compare button status rendering
            val compared = isCompared(car)
            if (compared) {
                binding.compareBtn.text = "Added to Compare"
                binding.compareBtn.setTextColor(binding.root.context.getColor(R.color.cyan))
            } else {
                binding.compareBtn.text = "Compare Specs"
                binding.compareBtn.setTextColor(binding.root.context.getColor(R.color.white))
            }

            // Wire actions
            binding.wishlistBtn.setOnClickListener {
                onWishlistClick(car)
                notifyItemChanged(bindingAdapterPosition)
            }

            binding.compareBtn.setOnClickListener {
                onCompareClick(car)
                notifyItemChanged(bindingAdapterPosition)
            }

            binding.buyNowBtn.setOnClickListener {
                onCarClick(car)
            }

            binding.root.setOnClickListener {
                onCarClick(car)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CarDiffCallback : DiffUtil.ItemCallback<Car>() {
        override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean = oldItem == newItem
    }
}
