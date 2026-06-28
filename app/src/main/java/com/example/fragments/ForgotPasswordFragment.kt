package com.example.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.databinding.FragmentForgotPasswordBinding
import com.example.viewmodels.AuthState
import com.example.viewmodels.AuthViewModel

class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etForgotEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(context, "Please enter your registered email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.forgotPassword(email)
        }

        binding.tvBackToLogin.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        observeState()
    }

    private fun observeState() {
        authViewModel.forgotState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.progressForgot.visibility = View.VISIBLE
                    binding.btnResetPassword.isEnabled = false
                }
                is AuthState.Success -> {
                    binding.progressForgot.visibility = View.GONE
                    binding.btnResetPassword.isEnabled = true
                    Toast.makeText(context, "Luxury reset instructions dispatched to registered mail!", Toast.LENGTH_LONG).show()
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }
                is AuthState.Error -> {
                    binding.progressForgot.visibility = View.GONE
                    binding.btnResetPassword.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    binding.progressForgot.visibility = View.GONE
                    binding.btnResetPassword.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
