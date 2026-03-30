package com.example.git_repo_4

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
import com.example.git_repo_4.utils.PreferencesManager

class ProfileActivity : BaseActivity() {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSave: Button
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        preferencesManager = PreferencesManager(this)

        // Initialize views
        etName = findViewById(R.id.etProfileName)
        etEmail = findViewById(R.id.etProfileEmail)
        btnSave = findViewById(R.id.btnSaveProfile)
        backButton = findViewById(R.id.ivProfileBack)

        // Load current user data
        loadUserData()

        // Setup save button
        btnSave.setOnClickListener {
            saveProfile()
        }

        // Setup back button
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadUserData() {
        etName.setText(preferencesManager.getUserName())
        etEmail.setText(preferencesManager.getUserEmail())
    }

    private fun saveProfile() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()

        // Basic validation
        if (name.isEmpty()) {
            Snackbar.make(
                findViewById(R.id.profileContainer),
                "Name cannot be empty",
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        if (email.isEmpty()) {
            Snackbar.make(
                findViewById(R.id.profileContainer),
                "Email cannot be empty",
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        // Basic email validation
        if (!email.contains("@")) {
            Snackbar.make(
                findViewById(R.id.profileContainer),
                "Please enter a valid email",
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        // Save data
        preferencesManager.setUserName(name)
        preferencesManager.setUserEmail(email)

        // Show success message
        Snackbar.make(
            findViewById(R.id.profileContainer),
            "Profile updated successfully",
            Snackbar.LENGTH_SHORT
        ).show()

        // Go back
        finish()
    }
}
