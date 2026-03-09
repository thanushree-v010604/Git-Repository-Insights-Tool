package com.example.git_repo_4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.git_repo_4.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_insights -> InsightsFragment()
                R.id.nav_activity -> ActivityFragment()
                R.id.nav_files -> FilesFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> HomeFragment()
            }
            openFragment(fragment)
            true
        }

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
