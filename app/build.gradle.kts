@file:Suppress("UNCHECKED_CAST")

import groovy.json.JsonSlurper
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Fix build directory issue (OneDrive conflicts)
val localAppData: String? = System.getenv("LOCALAPPDATA")
if (!localAppData.isNullOrBlank()) {
    layout.buildDirectory.set(file("$localAppData/Gradle/RepoPulse/build/app"))
}

// Load local.properties
val localBuildProperties: Properties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

// Helper function
fun readBuildProperty(key: String): String {
    val projectValue: String = (project.findProperty(key) as? String).orEmpty()
    return projectValue.ifBlank { localBuildProperties.getProperty(key).orEmpty() }
}

// Escape function
val escapeForBuildConfig: (String) -> String = { raw: String ->
    raw.replace("\\", "\\\\").replace("\"", "\\\"")
}

//
// 🔥 FIREBASE JSON PARSING (SAFE VERSION)
//
val googleServicesFile = project.file("google-services.json")
if (!googleServicesFile.exists()) {
    throw GradleException("Missing app/google-services.json")
}

val googleServices: Map<*, *> =
    JsonSlurper().parseText(googleServicesFile.readText()) as Map<*, *>

val projectInfo: Map<*, *> =
    googleServices["project_info"] as? Map<*, *>
        ?: throw GradleException("Invalid google-services.json: missing project_info")

val clients: List<Map<*, *>> =
    (googleServices["client"] as? List<*>)
        ?.mapNotNull { it as? Map<*, *> }
        .orEmpty()

val applicationIdValue = "com.example.git_repo_4"

val matchedClient: Map<*, *> =
    clients.firstOrNull { client ->
        val clientInfo = client["client_info"] as? Map<*, *>
        val androidClientInfo = clientInfo?.get("android_client_info") as? Map<*, *>
        val packageName = androidClientInfo?.get("package_name") as? String
        packageName == applicationIdValue
    } ?: throw GradleException("No matching client in google-services.json")

val apiKey: String =
    ((matchedClient["api_key"] as? List<*>)
        ?.firstOrNull() as? Map<*, *>)
        ?.get("current_key") as? String ?: ""

val appId: String =
    ((matchedClient["client_info"] as? Map<*, *>)
        ?.get("mobilesdk_app_id") as? String).orEmpty()

val projectId: String = projectInfo["project_id"] as? String ?: ""
val storageBucket: String = projectInfo["storage_bucket"] as? String ?: ""
val senderId: String = projectInfo["project_number"] as? String ?: ""

val webClientId: String =
    ((matchedClient["oauth_client"] as? List<*>)
        ?.mapNotNull { it as? Map<*, *> }
        ?.firstOrNull {
            val type = it["client_type"] as? Number
            type?.toInt() == 3
        }
        ?.get("client_id") as? String).orEmpty()

if (apiKey.isBlank() || appId.isBlank() || projectId.isBlank()) {
    throw GradleException("Firebase config missing required fields")
}

val webClientIdValue: String = webClientId.ifBlank { "" }

//
// 🔥 ANDROID CONFIG
//
android {
    namespace = "com.example.git_repo_4"
    compileSdk = 36

    defaultConfig {
        applicationId = applicationIdValue
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val rawGitHubToken: String = readBuildProperty("github.token")

        buildConfigField("String", "GITHUB_TOKEN", "\"${escapeForBuildConfig(rawGitHubToken)}\"")

        buildConfigField("String", "FIREBASE_API_KEY", "\"${escapeForBuildConfig(apiKey)}\"")
        buildConfigField("String", "FIREBASE_APP_ID", "\"${escapeForBuildConfig(appId)}\"")
        buildConfigField("String", "FIREBASE_PROJECT_ID", "\"${escapeForBuildConfig(projectId)}\"")
        buildConfigField("String", "FIREBASE_STORAGE_BUCKET", "\"${escapeForBuildConfig(storageBucket)}\"")
        buildConfigField("String", "FIREBASE_GCM_SENDER_ID", "\"${escapeForBuildConfig(senderId)}\"")

        resValue("string", "default_web_client_id", webClientIdValue)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
        resValues = true
    }
}

//
// 🔥 DEPENDENCIES
//
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.mpandroidchart)
    implementation(libs.androidx.recyclerview)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.glide)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.5.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}