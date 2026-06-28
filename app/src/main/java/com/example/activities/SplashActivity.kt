package com.example.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.example.databinding.ActivitySplashBinding
import com.example.firebase.FirebaseManager

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseManager first
        FirebaseManager.init(applicationContext)

        // Fade animation for logo container
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1500
            fillAfter = true
        }
        binding.logoContainer.startAnimation(fadeIn)

        // Delayed navigation (3 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
            val user = FirebaseManager.getCurrentUser(applicationContext)
            val intent = if (user != null) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, AuthActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 3000)
    }
}
