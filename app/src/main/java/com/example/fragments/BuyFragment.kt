package com.example.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.adapters.CarAdapter
import com.example.databinding.FragmentBuyBinding
import com.example.viewmodels.CarViewModel

class BuyFragment : Fragment() {
    private var _binding: FragmentBuyBinding? = null
    private val binding get() = _binding!!
    private lateinit var carViewModel: CarViewModel
    private lateinit var carAdapter: CarAdapter
    private var showWishlistOnly = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carViewModel = ViewModelProvider(requireActivity())[CarViewModel::class.java]

        setupRecyclerView()
        setupFilters()
        observeData()

        binding.btnCompareFloatingAction.setOnClickListener {
            findNavController().navigate(R.id.navigation_research)
        }
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter(
            onCarClick = { car ->
                val bundle = Bundle().apply { putString("carId", car.id) }
                findNavController().navigate(R.id.carDetailsFragment, bundle)
            },
            onWishlistClick = { car ->
                carViewModel.toggleWishlist(car)
            },
            onCompareClick = { car ->
                carViewModel.toggleCompare(car)
            },
            isWishlisted = { car ->
                car.isWishlisted
            },
            isCompared = { car ->
                carViewModel.comparisonCars.value?.any { it.id == car.id } ?: false
            }
        )

        binding.rvBuyCars.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = carAdapter
        }
    }

    private fun setupFilters() {
        // Handle Brand Chip Group checks
        binding.chipGroupBrands.setOnCheckedStateChangeListener { _, checkedIds ->
            val brand = when (checkedIds.firstOrNull()) {
                R.id.chipBMW -> "BMW"
                R.id.chipAudi -> "Audi"
                R.id.chipBenz -> "Mercedes-Benz"
                R.id.chipPorsche -> "Porsche"
                else -> "All"
            }
            carViewModel.selectBrand(brand)
        }

        // Handle Wishlist-only filter
        binding.btnWishlistToggleFilter.setOnClickListener {
            showWishlistOnly = !showWishlistOnly
            val icon = if (showWishlistOnly) {
                R.drawable.ic_heart_filled
            } else {
                R.drawable.ic_heart_empty
            }
            binding.btnWishlistToggleFilter.setIconResource(icon)
            
            // Refresh adapters with locally filtered data
            carViewModel.filteredCars.value?.let { allFiltered ->
                val list = if (showWishlistOnly) allFiltered.filter { it.isWishlisted } else allFiltered
                carAdapter.submitList(list)
            }
        }
    }

    private fun observeData() {
        // Observe final filtered car listings
        carViewModel.filteredCars.observe(viewLifecycleOwner) { cars ->
            val listToDisplay = if (showWishlistOnly) cars.filter { it.isWishlisted } else cars
            carAdapter.submitList(listToDisplay)
            binding.tvNoCarsFound.visibility = if (listToDisplay.isEmpty()) View.VISIBLE else View.GONE
        }

        // Observe comparison items list for the floating action card
        carViewModel.comparisonCars.observe(viewLifecycleOwner) { compareList ->
            if (compareList != null && compareList.isNotEmpty()) {
                binding.cardCompareFloating.visibility = View.VISIBLE
                binding.tvCompareFloatingText.text = "${compareList.size} Car${if (compareList.size > 1) "s" else ""} Selected for Comparison"
            } else {
                binding.cardCompareFloating.visibility = View.GONE
            }
            carAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
