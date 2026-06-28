package com.example.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.activities.AuthActivity
import com.example.activities.MainActivity
import com.example.databinding.FragmentLoginBinding
import com.example.viewmodels.AuthState
import com.example.viewmodels.AuthViewModel

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.login(email, password)
        }

        binding.tvSignupLink.setOnClickListener {
            (activity as? AuthActivity)?.replaceFragment(SignupFragment(), true)
        }

        binding.tvForgotPassword.setOnClickListener {
            (activity as? AuthActivity)?.replaceFragment(ForgotPasswordFragment(), true)
        }

        observeState()
    }

    private fun observeState() {
        authViewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.progressLogin.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is AuthState.Success -> {
                    binding.progressLogin.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(context, "Welcome, ${state.profile.name}!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(activity, MainActivity::class.java))
                    activity?.finish()
                }
                is AuthState.Error -> {
                    binding.progressLogin.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    binding.progressLogin.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
