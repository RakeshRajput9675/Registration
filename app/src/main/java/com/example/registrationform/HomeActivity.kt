package com.example.registrationform

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.registrationform.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private val binding:ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnAddUser.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))

        }
        binding.btnShowUser.setOnClickListener {
            startActivity(Intent(this, AlluserActivity::class.java))

        }

    }
}