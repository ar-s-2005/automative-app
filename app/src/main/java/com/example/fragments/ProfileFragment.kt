package com.example.fragments

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.R
import com.example.activities.AuthActivity
import com.example.databinding.FragmentProfileBinding
import com.example.viewmodels.AuthViewModel
import com.example.viewmodels.CarViewModel
import com.example.viewmodels.UiState

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel
    private lateinit var carViewModel: CarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        carViewModel = ViewModelProvider(requireActivity())[CarViewModel::class.java]

        setupUserMetadata()
        observeData()

        // Trigger loading of bookings when profile is accessed
        carViewModel.loadBookings()

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            Toast.makeText(context, "Logged out from Elite Hub", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, AuthActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun setupUserMetadata() {
        val user = authViewModel.currentUser.value
        binding.profileName.text = user?.name ?: "Elite Guest Member"
        binding.profileEmail.text = user?.email ?: "guest@automarkethub.com"
    }

    private fun observeData() {
        // Observe reserved test drive sessions
        carViewModel.bookingsState.observe(viewLifecycleOwner) { state ->
            binding.containerBookingsList.removeAllViews()
            if (state is UiState.Success) {
                val bookings = state.data
                if (bookings.isNotEmpty()) {
                    binding.tvNoBookings.visibility = View.GONE
                    binding.containerBookingsList.visibility = View.VISIBLE

                    bookings.forEach { booking ->
                        val card = createBookingCard(booking.carModel, booking.date, booking.time)
                        binding.containerBookingsList.addView(card)
                    }
                } else {
                    binding.tvNoBookings.visibility = View.VISIBLE
                    binding.containerBookingsList.visibility = View.GONE
                }
            } else {
                binding.tvNoBookings.visibility = View.VISIBLE
                binding.containerBookingsList.visibility = View.GONE
            }
        }

        // Observe custom user published listings
        carViewModel.carsState.observe(viewLifecycleOwner) { state ->
            binding.containerListingsList.removeAllViews()
            if (state is UiState.Success) {
                val userListings = state.data.filter { it.isCustom }
                if (userListings.isNotEmpty()) {
                    binding.tvNoListings.visibility = View.GONE
                    binding.containerListingsList.visibility = View.VISIBLE

                    userListings.forEach { car ->
                        val card = createListingCard(car.brand, car.model, car.price)
                        binding.containerListingsList.addView(card)
                    }
                } else {
                    binding.tvNoListings.visibility = View.VISIBLE
                    binding.containerListingsList.visibility = View.GONE
                }
            } else {
                binding.tvNoListings.visibility = View.VISIBLE
                binding.containerListingsList.visibility = View.GONE
            }
        }
    }

    private fun createBookingCard(carName: String, date: String, time: String): View {
        val container = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 24)
            }
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.edittext_border)
            setPadding(24, 24, 24, 24)
        }

        val titleTv = TextView(context).apply {
            text = carName
            setTextColor(resources.getColor(R.color.white, null))
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
        }

        val dateTv = TextView(context).apply {
            text = "Scheduled: $date at $time"
            setTextColor(resources.getColor(R.color.cyan, null))
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 0)
            }
        }

        val statusTv = TextView(context).apply {
            text = "RESERVED & CONFIRMED"
            setTextColor(resources.getColor(R.color.bg_black, null))
            setBackgroundColor(resources.getColor(R.color.gold, null))
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(16, 6, 16, 6)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 0)
            }
        }

        container.addView(titleTv)
        container.addView(dateTv)
        container.addView(statusTv)

        return container
    }

    private fun createListingCard(brand: String, model: String, price: String): View {
        val container = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 24)
            }
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.edittext_border)
            setPadding(24, 24, 24, 24)
        }

        val titleTv = TextView(context).apply {
            text = "$brand $model"
            setTextColor(resources.getColor(R.color.white, null))
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
        }

        val priceTv = TextView(context).apply {
            text = "Listed Price: $price"
            setTextColor(resources.getColor(R.color.gold, null))
            textSize = 14f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 0)
            }
        }

        val badgeTv = TextView(context).apply {
            text = "LIVE IN INVENTORY"
            setTextColor(resources.getColor(R.color.white, null))
            setBackgroundColor(resources.getColor(R.color.card_dark, null))
            textSize = 9f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(12, 4, 12, 4)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 0)
            }
        }

        container.addView(titleTv)
        container.addView(priceTv)
        container.addView(badgeTv)

        return container
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
