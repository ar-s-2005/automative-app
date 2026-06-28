package com.example.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.R
import com.example.adapters.BannerAdapter
import com.example.adapters.BrandAdapter
import com.example.databinding.FragmentHomeBinding
import com.example.models.Brand
import com.example.viewmodels.CarViewModel
import com.example.viewmodels.UiState

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var carViewModel: CarViewModel
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private var bannerRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carViewModel = ViewModelProvider(requireActivity())[CarViewModel::class.java]

        setupBrandsRecyclerView()
        setupFeaturedBanner()

        // Clicking Profile Icon
        binding.btnHeaderProfile.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile)
        }

        // Consult AI Advisor
        binding.btnConsultAI.setOnClickListener {
            findNavController().navigate(R.id.navigation_research)
        }

        // Locate Showrooms
        binding.btnLocateShowrooms.setOnClickListener {
            findNavController().navigate(R.id.dealershipFragment)
        }

        // Voice search simulator
        binding.voiceIcon.setOnClickListener {
            Toast.makeText(context, "Listening for luxury car names (e.g. Porsche)...", Toast.LENGTH_LONG).show()
        }

        // Search Input Action
        binding.etHomeSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etHomeSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    carViewModel.searchCars(query)
                    findNavController().navigate(R.id.navigation_buy)
                }
                true
            } else {
                false
            }
        }
    }

    private fun setupBrandsRecyclerView() {
        val brands = listOf(
            Brand("BMW", "https://logos-world.net/wp-content/uploads/2020/04/BMW-Logo.png", "#1A1A1A", "#333333"),
            Brand("Audi", "https://logos-world.net/wp-content/uploads/2020/04/Audi-Logo.png", "#1A1A1A", "#333333"),
            Brand("Mercedes-Benz", "https://logos-world.net/wp-content/uploads/2020/04/Mercedes-Benz-Logo.png", "#1A1A1A", "#333333"),
            Brand("Porsche", "https://logos-world.net/wp-content/uploads/2020/04/Porsche-Logo.png", "#1A1A1A", "#333333")
        )
        val brandAdapter = BrandAdapter(brands) { selectedBrand ->
            carViewModel.selectBrand(selectedBrand.name)
            findNavController().navigate(R.id.navigation_buy)
        }
        binding.rvBrands.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = brandAdapter
        }
    }

    private fun setupFeaturedBanner() {
        carViewModel.carsState.observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) {
                val carList = state.data
                // Take high horsepower cars as banner
                val featuredCars = carList.filter { it.horsepower >= 500 }.take(4)
                if (featuredCars.isNotEmpty()) {
                    val bannerAdapter = BannerAdapter(featuredCars) { car ->
                        val bundle = Bundle().apply { putString("carId", car.id) }
                        findNavController().navigate(R.id.carDetailsFragment, bundle)
                    }
                    binding.viewPagerHero.adapter = bannerAdapter

                    // Setup ViewPager2 Auto Scroll every 4 seconds
                    stopAutoScroll()
                    val count = featuredCars.size
                    bannerRunnable = object : Runnable {
                        override fun run() {
                            if (_binding != null) {
                                val currentItem = binding.viewPagerHero.currentItem
                                binding.viewPagerHero.currentItem = (currentItem + 1) % count
                                autoScrollHandler.postDelayed(this, 4000)
                            }
                        }
                    }
                    autoScrollHandler.postDelayed(bannerRunnable!!, 4000)
                }
            }
        }
    }

    private fun stopAutoScroll() {
        bannerRunnable?.let { autoScrollHandler.removeCallbacks(it) }
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScroll()
        _binding = null
    }
}
