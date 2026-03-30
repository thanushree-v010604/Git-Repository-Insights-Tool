package com.example.git_repo_4.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLogin(email: String? = null) {
        sharedPreferences.edit {
            putBoolean(KEY_IS_LOGGED_IN, true)
            if (!email.isNullOrBlank()) {
                putString(KEY_LOGGED_IN_EMAIL, email)
            }
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout() {
        sharedPreferences.edit {
            putBoolean(KEY_IS_LOGGED_IN, false)
            remove(KEY_LOGGED_IN_EMAIL)
        }
    }

    fun getLoggedInEmail(): String? {
        return sharedPreferences.getString(KEY_LOGGED_IN_EMAIL, null)
    }

    companion object {
        private const val PREFS_NAME = "repo_pulse_session"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_LOGGED_IN_EMAIL = "loggedInEmail"
    }
}

