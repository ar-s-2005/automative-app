package com.example.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.R
import com.example.databinding.FragmentCarDetailsBinding
import com.example.models.Car
import com.example.viewmodels.CarViewModel
import com.example.viewmodels.UiState

class CarDetailsFragment : Fragment() {
    private var _binding: FragmentCarDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var carViewModel: CarViewModel
    private var carId: String? = null
    private var currentCar: Car? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carViewModel = ViewModelProvider(requireActivity())[CarViewModel::class.java]

        carId = arguments?.getString("carId")
        if (carId == null) {
            Toast.makeText(context, "Error: Missing car ID", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        observeCarDetails()

        binding.btnDetailBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnDetailWishlist.setOnClickListener {
            currentCar?.let { car ->
                carViewModel.toggleWishlist(car)
            }
        }

        binding.btnCalculateEmi.setOnClickListener {
            calculateEmi()
        }

        binding.btnBookTestDrive.setOnClickListener {
            bookTestDrive()
        }

        binding.btnCallSeller.setOnClickListener {
            currentCar?.let { car ->
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${car.contactNumber}"))
                startActivity(intent)
            }
        }
    }

    private fun observeCarDetails() {
        carViewModel.carsState.observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) {
                val car = state.data.find { it.id == carId }
                if (car != null) {
                    currentCar = car
                    bindCarData(car)
                }
            }
        }
    }

    private fun bindCarData(car: Car) {
        binding.detailBrand.text = car.brand.uppercase()
        binding.detailModel.text = car.model
        binding.detailPrice.text = car.price

        binding.gridHp.text = "${car.horsepower} HP"
        binding.gridSpeed.text = "${car.topSpeed} km/h"
        binding.gridTorque.text = "${car.torque} Nm"
        binding.gridFuel.text = car.fuelType

        binding.detailYear.text = "Year: ${car.year}"
        binding.detailMileage.text = "Mileage: ${String.format("%,d", car.mileage)} km"
        binding.detailEngine.text = "Engine: ${car.engineType}"
        binding.detailCondition.text = "Condition: ${car.condition}"
        binding.detailLocation.text = "Location: ${car.location}"

        binding.detailOwnerName.text = car.ownerName
        binding.detailContactNumber.text = car.contactNumber

        Glide.with(this)
            .load(car.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.detailCarImage)

        val isWishlisted = car.isWishlisted
        val icon = if (isWishlisted) {
            R.drawable.ic_heart_filled
        } else {
            R.drawable.ic_heart_empty
        }
        binding.btnDetailWishlist.setImageResource(icon)
    }

    private fun calculateEmi() {
        val car = currentCar ?: return
        val interestStr = binding.etEmiInterest.text.toString().trim()
        val downPaymentStr = binding.etEmiDown.text.toString().trim()

        val interestRate = interestStr.toDoubleOrNull() ?: 7.5
        val downPayment = downPaymentStr.replace("[^0-9]".toRegex(), "").toDoubleOrNull() ?: 2000000.0 // Default 20 Lakhs

        val carPriceRaw = extractPriceNumber(car.price)

        val principal = carPriceRaw - downPayment
        if (principal <= 0) {
            binding.tvEmiResult.text = "EMI: No loan needed!"
            return
        }

        val monthlyRate = interestRate / (12 * 100)
        val tenureMonths = 60 // 5 years tenure default

        val emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths.toDouble())) /
                (Math.pow(1 + monthlyRate, tenureMonths.toDouble()) - 1)

        val formattedEmi = String.format("%,.0f", emi)
        binding.tvEmiResult.text = "Estimated EMI: ₹$formattedEmi / month (5 Yrs)"
    }

    private fun extractPriceNumber(priceText: String): Double {
        return try {
            val cleaned = priceText.replace("₹", "").trim()
            if (cleaned.contains("Cr")) {
                val num = cleaned.replace("Cr", "").trim().toDouble()
                num * 10000000.0
            } else if (cleaned.contains("Lakh")) {
                val num = cleaned.replace("Lakh", "").trim().toDouble()
                num * 100000.0
            } else {
                cleaned.replace(",", "").toDoubleOrNull() ?: 20000000.0
            }
        } catch (e: Exception) {
            20000000.0
        }
    }

    private fun bookTestDrive() {
        val car = currentCar ?: return
        val date = binding.etBookingDate.text.toString().trim()
        val time = binding.etBookingTime.text.toString().trim()

        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(context, "Please enter scheduling date & time", Toast.LENGTH_SHORT).show()
            return
        }

        carViewModel.bookTestDrive(
            car,
            date,
            time,
            "AutoMarket Hub Showroom",
            onSuccess = {
                Toast.makeText(context, "Test Drive Scheduled Successfully!", Toast.LENGTH_LONG).show()
                binding.etBookingDate.text?.clear()
                binding.etBookingTime.text?.clear()
                findNavController().navigate(R.id.navigation_profile)
            },
            onFailure = { error ->
                Toast.makeText(context, "Booking Failed: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
