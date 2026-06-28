package com.example.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.databinding.FragmentSellBinding
import com.example.viewmodels.CarViewModel

class SellFragment : Fragment() {
    private var _binding: FragmentSellBinding? = null
    private val binding get() = _binding!!
    private lateinit var carViewModel: CarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carViewModel = ViewModelProvider(requireActivity())[CarViewModel::class.java]

        binding.btnPublishListing.setOnClickListener {
            publishListing()
        }
    }

    private fun publishListing() {
        val brand = binding.etSellBrand.text.toString().trim()
        val model = binding.etSellModel.text.toString().trim()
        val yearStr = binding.etSellYear.text.toString().trim()
        val price = binding.etSellPrice.text.toString().trim()
        val mileageStr = binding.etSellMileage.text.toString().trim()
        val fuel = binding.etSellFuel.text.toString().trim()
        val engine = binding.etSellEngine.text.toString().trim()
        val condition = binding.etSellCondition.text.toString().trim()
        val imageUrl = binding.etSellImage.text.toString().trim()
        val ownerName = binding.etSellOwnerName.text.toString().trim()
        val contact = binding.etSellContact.text.toString().trim()
        val location = binding.etSellLocation.text.toString().trim()

        if (brand.isEmpty() || model.isEmpty() || yearStr.isEmpty() || price.isEmpty() ||
            ownerName.isEmpty() || contact.isEmpty() || location.isEmpty()) {
            Toast.makeText(context, "Please complete all mandatory fields", Toast.LENGTH_SHORT).show()
            return
        }

        val year = yearStr.toIntOrNull() ?: 2024
        val mileage = mileageStr.toIntOrNull() ?: 0

        val finalImageUrl = if (imageUrl.isEmpty()) {
            "https://images.unsplash.com/photo-1617814076367-b759c7d7e738?auto=format&fit=crop&q=80&w=800"
        } else {
            imageUrl
        }

        carViewModel.sellCar(
            brand = brand,
            model = model,
            year = year,
            price = price,
            mileage = mileage,
            fuelType = if (fuel.isEmpty()) "Petrol" else fuel,
            engineType = if (engine.isEmpty()) "V8 Biturbo" else engine,
            condition = if (condition.isEmpty()) "Excellent" else condition,
            ownerName = ownerName,
            contact = contact,
            location = location,
            imageUrl = finalImageUrl,
            onSuccess = {
                Toast.makeText(context, "Luxury vehicle listed successfully!", Toast.LENGTH_LONG).show()
                clearFields()
                findNavController().navigate(R.id.navigation_profile)
            },
            onFailure = { error ->
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun clearFields() {
        binding.etSellBrand.text?.clear()
        binding.etSellModel.text?.clear()
        binding.etSellYear.text?.clear()
        binding.etSellPrice.text?.clear()
        binding.etSellMileage.text?.clear()
        binding.etSellFuel.text?.clear()
        binding.etSellEngine.text?.clear()
        binding.etSellCondition.text?.clear()
        binding.etSellImage.text?.clear()
        binding.etSellOwnerName.text?.clear()
        binding.etSellContact.text?.clear()
        binding.etSellLocation.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
