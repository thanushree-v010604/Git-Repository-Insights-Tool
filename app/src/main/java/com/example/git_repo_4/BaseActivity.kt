package com.example.git_repo_4

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.git_repo_4.utils.SessionManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NAV_ITEM_ID = "nav_item_id"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_home -> {
                navigateToHome()
                true
            }
            R.id.menu_insights -> {
                navigateToPage(R.id.nav_insights)
                true
            }
            R.id.menu_activity -> {
                navigateToPage(R.id.nav_activity)
                true
            }
            R.id.menu_files -> {
                navigateToPage(R.id.nav_files)
                true
            }
            R.id.menu_settings -> {
                navigateToPage(R.id.nav_settings)
                true
            }
            R.id.menu_sign_out -> {
                handleSignOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToHome() {
        navigateToPage(R.id.nav_home)
    }

    private fun navigateToPage(navItemId: Int) {
        if (this is HomeActivity) {
            if (isCurrentNavigationItem(navItemId)) {
                return
            }
            selectNavigationItem(navItemId)
            return
        }

        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(EXTRA_NAV_ITEM_ID, navItemId)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun handleSignOut() {
        try {
            // Sign out from Google Sign-In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            GoogleSignIn.getClient(this, gso).signOut()

            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut()

            // Clear session
            SessionManager(this).logout()

            // Navigate to login screen
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Sign out error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
