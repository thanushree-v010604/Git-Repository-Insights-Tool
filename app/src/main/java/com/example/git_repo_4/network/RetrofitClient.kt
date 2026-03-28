package com.example.git_repo_4.network

import com.example.git_repo_4.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://api.github.com/"
    private const val MAX_403_RETRIES = 2
    private const val RETRY_DELAY_MS = 2000L
    private const val TAG_API = "API_RESPONSE"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        redactHeader("Authorization")
    }

    private val authHeaderInterceptor = Interceptor { chain ->
        val token = BuildConfig.GITHUB_TOKEN

        val builder = chain.request().newBuilder()
            .addHeader("User-Agent", "RepoPulse-App")
            .addHeader("Accept", "application/vnd.github.v3+json")

        if (token.isNotBlank()) {
            builder.addHeader("Authorization", "token $token")
        }

        chain.proceed(builder.build())
    }

    private val debugResponseInterceptor = Interceptor { chain ->
        val request = chain.request()
        android.util.Log.d(TAG_API, "→ ${request.method} ${request.url}")

        val response = chain.proceed(request)

        android.util.Log.d(TAG_API, "← HTTP ${response.code}")

        if (response.code == 403) {
            val remaining = response.header("X-RateLimit-Remaining")
            val limit = response.header("X-RateLimit-Limit")
            val reset = response.header("X-RateLimit-Reset")
            android.util.Log.w(TAG_API, "HTTP 403 Rate Limit - Remaining: $remaining, Limit: $limit, Reset: $reset")
        }

        response
    }

    private val retry403Interceptor = Interceptor { chain ->
        var response: Response = chain.proceed(chain.request())
        var retryCount = 0

        while (response.code == 403 && retryCount < MAX_403_RETRIES) {
            response.close()

            try {
                Thread.sleep(RETRY_DELAY_MS)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }

            retryCount++
            response = chain.proceed(chain.request())
        }

        response
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authHeaderInterceptor)
        .addInterceptor(debugResponseInterceptor)
        .addInterceptor(retry403Interceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val apiService: GitHubApiService = retrofit.create(GitHubApiService::class.java)
}
