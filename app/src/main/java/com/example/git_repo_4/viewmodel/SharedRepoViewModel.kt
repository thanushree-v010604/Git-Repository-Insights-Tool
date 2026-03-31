package com.example.git_repo_4.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.git_repo_4.model.Contributor
import com.example.git_repo_4.model.CommitResponse

class SharedRepoViewModel : ViewModel() {

    val repoName = MutableLiveData<String>()
    val isRepoLoaded = MutableLiveData(false)
    val contributors = MutableLiveData<List<Contributor>>()

    // Language statistics from GitHub /languages endpoint
    val languageStats = MutableLiveData<Map<String, Int>>()

    // Total commits for the currently analyzed repository
    val commitCount = MutableLiveData<Int>()

    // Simple derived weekly growth metric based on commit volume
    val weeklyGrowth = MutableLiveData<Int>()

    // Simple derived review speed metric for Insights page
    val reviewSpeed = MutableLiveData<Int>()

    // Additional repository metadata for AI analysis
    val stars = MutableLiveData<Int>()
    val forks = MutableLiveData<Int>()
    val lastUpdated = MutableLiveData<String>()

    // AI-based repository health analysis
    val repoHealth = MutableLiveData<String>()
    val risks = MutableLiveData<List<String>>()
    val suggestions = MutableLiveData<List<String>>()

    // RepoPulse Activity metrics and AI report
    val repoScore = MutableLiveData<Int>()
    val confidence = MutableLiveData<Int>()
    val readmeQuality = MutableLiveData<String>()
    val fullReport = MutableLiveData<String>()

    // Aggregated activity metrics
    val dailyCommits = MutableLiveData<List<Int>>()
    val weeklyCommits = MutableLiveData<List<Int>>()
    val peakDay = MutableLiveData<Int>()
    val avgPerDay = MutableLiveData<Double>()

    // Real date-based analytics
    val dailyCommits30 = MutableLiveData<List<Int>>()
    val dailyCommits90 = MutableLiveData<List<Int>>()
    val peakDate = MutableLiveData<String>()
    val dateRange = MutableLiveData<String>()

    // Files activity analytics
    data class FileUpdate(
        val name: String,
        val time: String,
        val change: String
    )

    val totalChanges = MutableLiveData<Int>()
    val topFiles = MutableLiveData<List<Map.Entry<String, Int>>>()
    val recentFiles = MutableLiveData<List<FileUpdate>>()

    fun markRepoLoaded(fullRepoName: String) {
        repoName.value = fullRepoName
        isRepoLoaded.value = true
    }

    fun clearRepoState() {
        isRepoLoaded.value = false
        repoName.value = ""
        contributors.value = emptyList()
        languageStats.value = emptyMap()
        commitCount.value = null
        weeklyGrowth.value = null
        reviewSpeed.value = null
        stars.value = null
        forks.value = null
        lastUpdated.value = null
        repoHealth.value = null
        risks.value = emptyList()
        suggestions.value = emptyList()
        repoScore.value = null
        confidence.value = null
        readmeQuality.value = null
        fullReport.value = null
        dailyCommits.value = emptyList()
        weeklyCommits.value = emptyList()
        peakDay.value = null
        avgPerDay.value = null
        dailyCommits30.value = emptyList()
        dailyCommits90.value = emptyList()
        peakDate.value = null
        dateRange.value = null
        totalChanges.value = 0
        topFiles.value = emptyList()
        recentFiles.value = emptyList()
    }
}
