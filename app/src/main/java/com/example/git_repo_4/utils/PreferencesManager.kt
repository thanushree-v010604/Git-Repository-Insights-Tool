package com.example.git_repo_4.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "repo_pulse_prefs"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_GITHUB_USERNAME = "github_username"
        private const val KEY_PUSH_NOTIFICATIONS = "push_notifications"
        private const val KEY_TWO_FA_ENABLED = "two_fa_enabled"
        private const val KEY_THEME_MODE = "theme_mode" // "light" or "dark"
        private const val KEY_PROFILE_IMAGE_URI = "profile_image_uri"
    }

    // User Data
    fun setUserName(name: String) {
        sharedPreferences.edit { putString(KEY_USER_NAME, name) }
    }

    fun getUserName(): String {
        return sharedPreferences.getString(KEY_USER_NAME, "") ?: ""
    }

    fun setUserEmail(email: String) {
        sharedPreferences.edit { putString(KEY_USER_EMAIL, email) }
    }

    fun getUserEmail(): String {
        return sharedPreferences.getString(KEY_USER_EMAIL, "")
            ?: ""
    }

    // GitHub
    fun setGithubUsername(username: String) {
        sharedPreferences.edit { putString(KEY_GITHUB_USERNAME, username) }
    }

    fun getGithubUsername(): String {
        return sharedPreferences.getString(KEY_GITHUB_USERNAME, "thanu-dev") ?: "thanu-dev"
    }

    // Push Notifications
    fun setPushNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_PUSH_NOTIFICATIONS, enabled) }
    }

    fun isPushNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_PUSH_NOTIFICATIONS, true)
    }

    // 2FA
    fun setTwoFAEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_TWO_FA_ENABLED, enabled) }
    }

    fun isTwoFAEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_TWO_FA_ENABLED, false)
    }

    // Theme
    fun setThemeMode(mode: String) {
        sharedPreferences.edit { putString(KEY_THEME_MODE, mode) }
    }

    fun getThemeMode(): String {
        return sharedPreferences.getString(KEY_THEME_MODE, "dark") ?: "dark"
    }

    // Clear all session data
    fun clearAllData() {
        sharedPreferences.edit { clear() }
    }

    fun setProfileImageUri(uri: String?) {
        sharedPreferences.edit {
            if (uri.isNullOrBlank()) {
                remove(KEY_PROFILE_IMAGE_URI)
            } else {
                putString(KEY_PROFILE_IMAGE_URI, uri)
            }
        }
    }

    fun getProfileImageUri(): String? {
        return sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, null)
    }
}
