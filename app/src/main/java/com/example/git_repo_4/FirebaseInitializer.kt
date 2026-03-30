package com.example.git_repo_4

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseInitializer {
    private const val TAG = "FirebaseInitializer"

    fun initialize(context: Context): Boolean {
        if (FirebaseApp.getApps(context).isNotEmpty()) {
            return true
        }

        if (BuildConfig.FIREBASE_API_KEY.isBlank() ||
            BuildConfig.FIREBASE_APP_ID.isBlank() ||
            BuildConfig.FIREBASE_PROJECT_ID.isBlank()
        ) {
            Log.e(TAG, "Missing Firebase values generated from google-services.json")
            return false
        }

        return try {
            val optionsBuilder = FirebaseOptions.Builder()
                .setApiKey(BuildConfig.FIREBASE_API_KEY)
                .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)

            if (BuildConfig.FIREBASE_STORAGE_BUCKET.isNotBlank()) {
                optionsBuilder.setStorageBucket(BuildConfig.FIREBASE_STORAGE_BUCKET)
            }
            if (BuildConfig.FIREBASE_GCM_SENDER_ID.isNotBlank()) {
                optionsBuilder.setGcmSenderId(BuildConfig.FIREBASE_GCM_SENDER_ID)
            }

            FirebaseApp.initializeApp(context, optionsBuilder.build())
            true
        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization failed", e)
            false
        }
    }
}
