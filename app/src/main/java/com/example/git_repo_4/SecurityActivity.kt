package com.example.git_repo_4

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.example.git_repo_4.utils.PreferencesManager

class SecurityActivity : BaseActivity() {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var etCurrentPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnChangePassword: Button
    private lateinit var switch2FA: SwitchMaterial
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)

        preferencesManager = PreferencesManager(this)

        // Initialize views
        etCurrentPassword = findViewById(R.id.etCurrentPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        switch2FA = findViewById(R.id.switch2FA)
        backButton = findViewById(R.id.ivSecurityBack)

        // Load 2FA state
        switch2FA.isChecked = preferencesManager.isTwoFAEnabled()

        // Setup change password button
        btnChangePassword.setOnClickListener {
            changePassword()
        }

        // Setup 2FA toggle
        switch2FA.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.setTwoFAEnabled(isChecked)
            Snackbar.make(
                findViewById(R.id.securityContainer),
                if (isChecked) "Two-Factor Authentication enabled" else "Two-Factor Authentication disabled",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        // Setup back button
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun changePassword() {
        val currentPassword = etCurrentPassword.text.toString().trim()
        val newPassword = etNewPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validation
        if (currentPassword.isEmpty()) {
            showError("Please enter your current password")
            return
        }

        if (newPassword.isEmpty()) {
            showError("Please enter a new password")
            return
        }

        if (confirmPassword.isEmpty()) {
            showError("Please confirm your new password")
            return
        }

        if (newPassword != confirmPassword) {
            showError("Passwords do not match")
            return
        }

        if (newPassword.length < 6) {
            showError("Password must be at least 6 characters long")
            return
        }

        // In a real app, you would verify the current password and update it
        // For now, just show success
        Snackbar.make(
            findViewById(R.id.securityContainer),
            "Password changed successfully",
            Snackbar.LENGTH_SHORT
        ).show()

        // Clear fields
        etCurrentPassword.text.clear()
        etNewPassword.text.clear()
        etConfirmPassword.text.clear()
    }

    private fun showError(message: String) {
        Snackbar.make(
            findViewById(R.id.securityContainer),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}
