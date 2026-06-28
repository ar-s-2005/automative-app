package com.example.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.R
import com.example.activities.MainActivity
import com.example.databinding.FragmentSignupBinding
import com.example.viewmodels.AuthState
import com.example.viewmodels.AuthViewModel

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        binding.btnSignup.setOnClickListener {
            val name = binding.etSignupName.text.toString().trim()
            val email = binding.etSignupEmail.text.toString().trim()
            val password = binding.etSignupPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.signup(name, email, password)
        }

        binding.tvLoginLink.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        observeState()
    }

    private fun observeState() {
        authViewModel.signupState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.progressSignup.visibility = View.VISIBLE
                    binding.btnSignup.isEnabled = false
                }
                is AuthState.Success -> {
                    binding.progressSignup.visibility = View.GONE
                    binding.btnSignup.isEnabled = true
                    Toast.makeText(context, "Welcome Elite Club Member!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(activity, MainActivity::class.java))
                    activity?.finish()
                }
                is AuthState.Error -> {
                    binding.progressSignup.visibility = View.GONE
                    binding.btnSignup.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    binding.progressSignup.visibility = View.GONE
                    binding.btnSignup.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
