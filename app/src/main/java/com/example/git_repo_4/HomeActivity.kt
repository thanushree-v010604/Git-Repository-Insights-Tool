package com.example.git_repo_4

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.git_repo_4.databinding.ActivityHomeBinding
import com.example.git_repo_4.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)
        if (FirebaseAuth.getInstance().currentUser == null) {
            sessionManager.logout()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar as action bar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_insights -> InsightsFragment()
                R.id.nav_activity -> ActivityFragment()
                R.id.nav_files -> FilesFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> HomeFragment()
            }
            openFragment(fragment, item.itemId)
            true
        }

        if (savedInstanceState == null) {
            val navItemId = intent.getIntExtra(EXTRA_NAV_ITEM_ID, R.id.nav_home)
            binding.bottomNavigation.selectedItemId = navItemId
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val navItemId = intent.getIntExtra(EXTRA_NAV_ITEM_ID, binding.bottomNavigation.selectedItemId)
        selectNavigationItem(navItemId)
    }

    fun isCurrentNavigationItem(navItemId: Int): Boolean {
        return ::binding.isInitialized && binding.bottomNavigation.selectedItemId == navItemId
    }

    fun selectNavigationItem(navItemId: Int) {
        if (!::binding.isInitialized) return
        if (binding.bottomNavigation.selectedItemId == navItemId) return
        binding.bottomNavigation.selectedItemId = navItemId
    }

    private fun openFragment(fragment: Fragment, navItemId: Int) {
        // Set the title based on the selected navigation item
        val title = when (navItemId) {
            R.id.nav_home -> "RepoPulse"
            R.id.nav_insights -> "Contributor Insights"
            R.id.nav_activity -> "Commit Activity"
            R.id.nav_files -> "File Activity"
            R.id.nav_settings -> "Settings"
            else -> "RepoPulse"
        }
        supportActionBar?.title = title

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
