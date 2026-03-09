package com.example.git_repo_4

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.git_repo_4.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            // Kotlin equivalent of: Intent(LoginActivity.this, HomeActivity.class)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}

