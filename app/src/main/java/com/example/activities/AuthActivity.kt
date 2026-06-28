package com.example.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.R
import com.example.databinding.ActivityAuthBinding
import com.example.fragments.LoginFragment

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show Login Fragment initially
        if (savedInstanceState == null) {
            replaceFragment(LoginFragment(), false)
        }
    }

    fun replaceFragment(fragment: Fragment, addToBackstack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.auth_container, fragment)
        
        if (addToBackstack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}
