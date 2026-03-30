import groovy.json.JsonSlurper
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

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

        val googleServicesFile = project.file("google-services.json")
        if (!googleServicesFile.exists()) {
            throw GradleException("Missing app/google-services.json. Firebase auth cannot be configured.")
        }

        @Suppress("UNCHECKED_CAST")
        val googleServices = JsonSlurper().parseText(googleServicesFile.readText()) as Map<String, Any?>

        val projectInfo = googleServices["project_info"] as? Map<String, Any?>
            ?: throw GradleException("Invalid google-services.json: missing project_info")

        val clients = (googleServices["client"] as? List<*>)
            ?.filterIsInstance<Map<String, Any?>>()
            .orEmpty()

        val matchedClient = clients.firstOrNull { client ->
            val clientInfo = client["client_info"] as? Map<String, Any?>
            val androidClientInfo = clientInfo?.get("android_client_info") as? Map<String, Any?>
            (androidClientInfo?.get("package_name") as? String) == applicationId
        } ?: throw GradleException("No client with package_name=$applicationId in google-services.json")

        val apiKey = ((matchedClient["api_key"] as? List<*>)
            ?.firstOrNull() as? Map<String, Any?>)
            ?.get("current_key") as? String ?: ""

        val appId = ((matchedClient["client_info"] as? Map<String, Any?>)
            ?.get("mobilesdk_app_id") as? String).orEmpty()

        val projectId = (projectInfo["project_id"] as? String).orEmpty()
        val storageBucket = (projectInfo["storage_bucket"] as? String).orEmpty()
        val senderId = (projectInfo["project_number"] as? String).orEmpty()

        val webClientId = ((matchedClient["oauth_client"] as? List<*>)
            ?.filterIsInstance<Map<String, Any?>>()
            ?.firstOrNull { (it["client_type"] as? Number)?.toInt() == 3 }
            ?.get("client_id") as? String).orEmpty()

        if (apiKey.isBlank() || appId.isBlank() || projectId.isBlank()) {
            throw GradleException("google-services.json is missing required Firebase fields")
        }

        buildConfigField("String", "FIREBASE_API_KEY", "\"${escapeForBuildConfig(apiKey)}\"")
        buildConfigField("String", "FIREBASE_APP_ID", "\"${escapeForBuildConfig(appId)}\"")
        buildConfigField("String", "FIREBASE_PROJECT_ID", "\"${escapeForBuildConfig(projectId)}\"")
        buildConfigField("String", "FIREBASE_STORAGE_BUCKET", "\"${escapeForBuildConfig(storageBucket)}\"")
        buildConfigField("String", "FIREBASE_GCM_SENDER_ID", "\"${escapeForBuildConfig(senderId)}\"")

        val webClientIdValue = if (webClientId.isBlank()) "" else webClientId
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

    // Firebase Auth + Google Sign-In (configured through google-services.json)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
