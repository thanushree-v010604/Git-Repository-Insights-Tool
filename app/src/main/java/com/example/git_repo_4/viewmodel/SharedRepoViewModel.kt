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
    val commitCount = MutableLiveData(0)

    // Simple derived weekly growth metric based on commit volume
    val weeklyGrowth = MutableLiveData(0)

    // Simple derived review speed metric for Insights page
    val reviewSpeed = MutableLiveData(0)

    // Additional repository metadata for AI analysis
    val stars = MutableLiveData(0)
    val forks = MutableLiveData(0)
    val lastUpdated = MutableLiveData("")

    // AI-based repository health analysis
    val repoHealth = MutableLiveData("")
    val risks = MutableLiveData<List<String>>(emptyList())
    val suggestions = MutableLiveData<List<String>>(emptyList())

    // RepoPulse Activity metrics and AI report
    val repoScore = MutableLiveData(0)
    val confidence = MutableLiveData(0)
    val readmeQuality = MutableLiveData("")
    val fullReport = MutableLiveData("")

    // Aggregated activity metrics
    val dailyCommits = MutableLiveData<List<Int>>(emptyList())
    val weeklyCommits = MutableLiveData<List<Int>>(emptyList())
    val peakDay = MutableLiveData(0)
    val avgPerDay = MutableLiveData(0.0)

    // Real date-based analytics
    val dailyCommits30 = MutableLiveData<List<Int>>(emptyList())
    val dailyCommits90 = MutableLiveData<List<Int>>(emptyList())
    val peakDate = MutableLiveData("")
    val dateRange = MutableLiveData("")

    // Files activity analytics
    data class FileUpdate(
        val name: String,
        val time: String,
        val change: String
    )

    val totalChanges = MutableLiveData(0)
    val topFiles = MutableLiveData<List<Map.Entry<String, Int>>>(emptyList())
    val recentFiles = MutableLiveData<List<FileUpdate>>(emptyList())

    fun markRepoLoaded(fullRepoName: String) {
        repoName.value = fullRepoName
        isRepoLoaded.value = true
    }

    fun clearRepoState() {
        isRepoLoaded.value = false
        repoName.value = ""
        contributors.value = emptyList()
        languageStats.value = emptyMap()
        commitCount.value = 0
        weeklyGrowth.value = 0
        reviewSpeed.value = 0
        stars.value = 0
        forks.value = 0
        lastUpdated.value = ""
        repoHealth.value = ""
        risks.value = emptyList()
        suggestions.value = emptyList()
        repoScore.value = 0
        confidence.value = 0
        readmeQuality.value = ""
        fullReport.value = ""
        dailyCommits.value = emptyList()
        weeklyCommits.value = emptyList()
        peakDay.value = 0
        avgPerDay.value = 0.0
        dailyCommits30.value = emptyList()
        dailyCommits90.value = emptyList()
        peakDate.value = ""
        dateRange.value = ""
        totalChanges.value = 0
        topFiles.value = emptyList()
        recentFiles.value = emptyList()
    }
}
