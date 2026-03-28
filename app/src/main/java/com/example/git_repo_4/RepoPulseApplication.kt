package com.example.git_repo_4

import android.app.Application

class RepoPulseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseInitializer.initialize(this)
    }
}

