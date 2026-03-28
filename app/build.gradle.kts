import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Keep intermediates outside OneDrive to prevent processDebugJavaRes delete/snapshot races.
val localAppData: String? = System.getenv("LOCALAPPDATA")
if (!localAppData.isNullOrBlank()) {
    layout.buildDirectory.set(file("$localAppData/Gradle/RepoPulse/build/app"))
}

val localBuildProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

fun readBuildProperty(key: String): String {
    val projectValue = (project.findProperty(key) as? String).orEmpty()
    return projectValue.ifBlank { localBuildProperties.getProperty(key).orEmpty() }
}

android {
    namespace = "com.example.git_repo_4"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.git_repo_4"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val escapeForBuildConfig: (String) -> String = { raw ->
            raw.replace("\\", "\\\\").replace("\"", "\\\"")
        }

        val rawGitHubToken: String = readBuildProperty("github.token")
        buildConfigField("String", "GITHUB_TOKEN", "\"${escapeForBuildConfig(rawGitHubToken)}\"")

        val firebaseApiKey = readBuildProperty("firebase.apiKey")
        val firebaseAppId = readBuildProperty("firebase.appId")
        val firebaseProjectId = readBuildProperty("firebase.projectId")
        val firebaseStorageBucket = readBuildProperty("firebase.storageBucket")
        val firebaseSenderId = readBuildProperty("firebase.gcmSenderId")

        buildConfigField("String", "FIREBASE_API_KEY", "\"${escapeForBuildConfig(firebaseApiKey)}\"")
        buildConfigField("String", "FIREBASE_APP_ID", "\"${escapeForBuildConfig(firebaseAppId)}\"")
        buildConfigField("String", "FIREBASE_PROJECT_ID", "\"${escapeForBuildConfig(firebaseProjectId)}\"")
        buildConfigField("String", "FIREBASE_STORAGE_BUCKET", "\"${escapeForBuildConfig(firebaseStorageBucket)}\"")
        buildConfigField("String", "FIREBASE_GCM_SENDER_ID", "\"${escapeForBuildConfig(firebaseSenderId)}\"")
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
    }
}

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

    // Firebase dependencies (manual runtime initialization; no Google Services plugin)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// Note: any cleanup of build intermediates on Windows/OneDrive is now handled by
// the external PowerShell script build_fix.ps1 to avoid file-lock related
// AccessDeniedException during Gradle tasks like dexBuilderDebug.
