package com.example.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapters.ComparisonAdapter
import com.example.databinding.FragmentResearchBinding
import com.example.viewmodels.CarViewModel

class ResearchFragment : Fragment() {
    private var _binding: FragmentResearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var carViewModel: CarViewModel
    private lateinit var comparisonAdapter: ComparisonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carViewModel = ViewModelProvider(requireActivity())[CarViewModel::class.java]

        setupComparisonRecyclerView()
        observeData()

        binding.btnClearCompare.setOnClickListener {
            carViewModel.clearComparison()
        }

        binding.btnGetAiReport.setOnClickListener {
            generateAiReport()
        }
    }

    private fun setupComparisonRecyclerView() {
        comparisonAdapter = ComparisonAdapter(emptyList()) { car ->
            carViewModel.toggleCompare(car)
        }

        binding.rvComparisonList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = comparisonAdapter
        }
    }

    private fun generateAiReport() {
        val budget = binding.etAiBudget.text.toString().trim()
        val brand = binding.etAiBrand.text.toString().trim()
        val style = binding.etAiStyle.text.toString().trim()
        val requirements = binding.etAiRequirements.text.toString().trim()

        if (budget.isEmpty() && brand.isEmpty() && style.isEmpty()) {
            Toast.makeText(context, "Please enter at least one preference", Toast.LENGTH_SHORT).show()
            return
        }

        binding.cardAiResult.visibility = View.VISIBLE
        binding.tvAiResponse.text = "Consulting Advisor. Retrieving market intelligence..."

        carViewModel.getAIRecommendations(budget, brand, style, requirements)
    }

    private fun observeData() {
        // Observe Car comparison stack LiveData
        carViewModel.comparisonCars.observe(viewLifecycleOwner) { list ->
            if (!list.isNullOrEmpty()) {
                binding.tvCompareInstructions.visibility = View.GONE
                binding.rvComparisonList.visibility = View.VISIBLE
                binding.btnClearCompare.visibility = View.VISIBLE
                comparisonAdapter.submitList(list)
            } else {
                binding.tvCompareInstructions.visibility = View.VISIBLE
                binding.rvComparisonList.visibility = View.GONE
                binding.btnClearCompare.visibility = View.GONE
                comparisonAdapter.submitList(emptyList())
            }
        }

        // Observe AI recommendations output LiveData
        carViewModel.aiRecommendation.observe(viewLifecycleOwner) { report ->
            if (!report.isNullOrBlank()) {
                binding.cardAiResult.visibility = View.VISIBLE
                binding.tvAiResponse.text = report
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
