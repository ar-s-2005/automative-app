package com.example.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.databinding.ItemComparisonCardBinding
import com.example.models.Car

class ComparisonAdapter(
    private var cars: List<Car>,
    private val onRemoveClick: (Car) -> Unit
) : RecyclerView.Adapter<ComparisonAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemComparisonCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemComparisonCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val car = cars[position]
        
        holder.binding.compBrand.text = car.brand
        holder.binding.compModel.text = car.model
        holder.binding.compPrice.text = car.price
        holder.binding.compHp.text = "${car.horsepower} HP"
        holder.binding.compSpeed.text = "${car.topSpeed} km/h"
        holder.binding.compTorque.text = "${car.torque} Nm"
        holder.binding.compEngine.text = car.engineType
        holder.binding.compFuel.text = car.fuelType
        holder.binding.compYear.text = car.year.toString()

        Glide.with(holder.itemView.context)
            .load(car.imageUrl)
            .into(holder.binding.compImage)

        holder.binding.removeBtn.setOnClickListener {
            onRemoveClick(car)
        }
    }

    override fun getItemCount(): Int = cars.size

    fun submitList(newList: List<Car>) {
        cars = newList
        notifyDataSetChanged()
    }
}
