package com.example.git_repo_4

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.git_repo_4.model.RepoResponse
import com.example.git_repo_4.network.RetrofitClient
import com.example.git_repo_4.repository.GitHubRepository
import com.example.git_repo_4.utils.GitHubUrlParser
import com.example.git_repo_4.utils.RecentRepoManager
import com.example.git_repo_4.viewmodel.SharedRepoViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.git_repo_4.model.CommitResponse

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recentRepoAdapter: RecentRepoAdapter
    private lateinit var analyzeButton: Button
    private val repoViewModel: SharedRepoViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repoUrlInput = view.findViewById<EditText>(R.id.etRepoUrl)
        analyzeButton = view.findViewById(R.id.btnAnalyze)
        val recentRecycler = view.findViewById<RecyclerView>(R.id.rvRecentRepos)

        // Setup RecyclerView for Recently Analyzed list using existing card design
        recentRepoAdapter = RecentRepoAdapter(emptyList())
        recentRecycler.layoutManager = LinearLayoutManager(requireContext())
        recentRecycler.adapter = recentRepoAdapter
        addRecentListSpacingIfNeeded(recentRecycler)

        // Initial load of history
        loadRecentRepos()

        repoUrlInput.doAfterTextChanged { text ->
            if (text.isNullOrBlank()) {
                resetAnalyzedState()
            }
        }

        analyzeButton.setOnClickListener {
            startRepositoryAnalysis(repoUrlInput.text.toString())
        }
    }

    private fun addRecentListSpacingIfNeeded(recyclerView: RecyclerView) {
        val spacingPx = (14f * resources.displayMetrics.density).toInt()
        val alreadyAdded = (0 until recyclerView.itemDecorationCount)
            .map { recyclerView.getItemDecorationAt(it) }
            .any { it is ItemSpacingDecoration }

        if (!alreadyAdded) {
            recyclerView.addItemDecoration(ItemSpacingDecoration(spacingPx))
        }
    }

    private fun loadRecentRepos() {
        val recent = RecentRepoManager.getRepos(requireContext())
        recentRepoAdapter.submitList(recent)
    }

    private fun startRepositoryAnalysis(url: String) {
        val parsed = GitHubUrlParser.parse(url)
        if (parsed == null) {
            resetAnalyzedState()
            Toast.makeText(context, getString(R.string.invalid_repo_url_message), Toast.LENGTH_SHORT).show()
            return
        }

        val owner = parsed.first
        val repo = parsed.second
        val repoKey = "$owner/$repo"

        // Optional cache optimization: avoid an API call for the same repository in this session.
        val cachedRepo = GitHubRepository.getCachedRepo(repoKey)
        if (cachedRepo != null) {
            applyRepoData(owner, repo, repoKey, cachedRepo, saveCache = false)
            Toast.makeText(context, getString(R.string.loaded_cached_repo_message), Toast.LENGTH_SHORT).show()
            return
        }

        // Start a new analysis cycle with a clean UI state when fresh fetch is required.
        resetAnalyzedState()

        if (!isNetworkAvailable()) {
            Toast.makeText(context, getString(R.string.error_no_internet_connection), Toast.LENGTH_SHORT).show()
            return
        }

        setAnalyzeLoading(true)
        fetchRepositoryData(owner, repo, repoKey)
    }

    private fun setAnalyzeLoading(isLoading: Boolean) {
        if (this::analyzeButton.isInitialized) {
            analyzeButton.isEnabled = !isLoading
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    private fun applyRepoData(
        owner: String,
        repo: String,
        repoKey: String,
        repoData: RepoResponse,
        saveCache: Boolean
    ) {
        if (saveCache) {
            GitHubRepository.cacheRepo(repoKey, repoData)
            RecentRepoManager.saveRepo(requireContext(), repoKey)
            loadRecentRepos()
        }

        repoViewModel.markRepoLoaded("$owner/$repo")
        repoViewModel.stars.value = repoData.stargazersCount
        repoViewModel.forks.value = repoData.forksCount
        repoViewModel.lastUpdated.value = repoData.updatedAt
    }

    private fun resetAnalyzedState() {
        repoViewModel.clearRepoState()
    }

    private fun showCachedFallbackIfAvailable(owner: String, repo: String, repoKey: String): Boolean {
        val cachedRepo = GitHubRepository.getCachedRepo(repoKey) ?: return false
        applyRepoData(owner, repo, repoKey, cachedRepo, saveCache = false)
        Toast.makeText(context, getString(R.string.loaded_cached_repo_message), Toast.LENGTH_SHORT).show()
        return true
    }

    private fun fetchRepositoryData(owner: String, repo: String, repoKey: String) {
        RetrofitClient.apiService.getRepository(owner, repo)
            .enqueue(object : Callback<RepoResponse> {
                override fun onResponse(
                    call: Call<RepoResponse>,
                    response: Response<RepoResponse>
                ) {
                    if (response.isSuccessful) {
                        val repoData = response.body()
                        if (repoData != null) {
                            applyRepoData(owner, repo, repoKey, repoData, saveCache = true)

                            // Fetch contributors for Insights page
                            RetrofitClient.apiService.getContributors(owner, repo)
                                .enqueue(object : Callback<List<com.example.git_repo_4.model.Contributor>> {
                                    override fun onResponse(
                                        call: Call<List<com.example.git_repo_4.model.Contributor>>,
                                        response: Response<List<com.example.git_repo_4.model.Contributor>>
                                    ) {
                                        if (response.isSuccessful) {
                                            val contributors = response.body() ?: emptyList()
                                            repoViewModel.contributors.value = contributors
                                            // keep repository cache updated as well
                                            GitHubRepository.contributors = contributors
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<List<com.example.git_repo_4.model.Contributor>>,
                                        t: Throwable
                                    ) {
                                        // Silent failure for now; UI will keep previous data or fall back
                                    }
                                })

                            // Fetch language statistics for impact distribution
                            RetrofitClient.apiService.getLanguages(owner, repo)
                                .enqueue(object : Callback<Map<String, Int>> {
                                    override fun onResponse(
                                        call: Call<Map<String, Int>>,
                                        response: Response<Map<String, Int>>
                                    ) {
                                        if (response.isSuccessful) {
                                            repoViewModel.languageStats.value = response.body()
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<Map<String, Int>>,
                                        t: Throwable
                                    ) {
                                        // Ignore failure; UI will fall back to static data
                                    }
                                })

                            // Fetch commits from GitHub for Insights metrics
                            RetrofitClient.apiService.getCommitsForInsights(owner, repo)
                                .enqueue(object : Callback<List<CommitResponse>> {

                                    override fun onResponse(
                                        call: Call<List<CommitResponse>>,
                                        response: Response<List<CommitResponse>>
                                    ) {
                                        if (response.isSuccessful) {
                                            val commits = response.body() ?: emptyList()

                                            val dateCountMap = mutableMapOf<String, Int>()
                                            for (commit in commits) {
                                                val raw = commit.commit.author.date
                                                val date = if (raw.length >= 10) raw.substring(0, 10) else raw
                                                dateCountMap[date] = dateCountMap.getOrDefault(date, 0) + 1
                                            }

                                            val sortedDates = dateCountMap.keys.sorted()

                                            val last30 = if (sortedDates.size >= 30) {
                                                sortedDates.takeLast(30)
                                            } else {
                                                sortedDates
                                            }

                                            val last90 = if (sortedDates.size >= 90) {
                                                sortedDates.takeLast(90)
                                            } else {
                                                sortedDates
                                            }

                                            val data30 = last30.map { dateCountMap[it] ?: 0 }
                                            val data90 = last90.map { dateCountMap[it] ?: 0 }

                                            val safeData30 = if (data30.isEmpty()) listOf(0) else data30
                                            val safeData90 = if (data90.isEmpty()) listOf(0) else data90

                                            android.util.Log.d("DEBUG_30", safeData30.toString())
                                            android.util.Log.d("DEBUG_90", safeData90.toString())

                                            repoViewModel.dailyCommits30.value = safeData30
                                            repoViewModel.dailyCommits90.value = safeData90

                                            // Also keep a default daily series for the current chart
                                            repoViewModel.dailyCommits.value = safeData30

                                            // DATE RANGE TEXT (safe)
                                            val start = last30.firstOrNull() ?: "N/A"
                                            val end = last30.lastOrNull() ?: "N/A"
                                            repoViewModel.dateRange.value = "$start to $end"

                                            // PEAK ACTIVITY (value and date)
                                            val peakEntry = dateCountMap.maxByOrNull { it.value }
                                            repoViewModel.peakDay.value = peakEntry?.value ?: 0
                                            repoViewModel.peakDate.value = peakEntry?.key ?: "N/A"

                                            // AVERAGE over last 30 entries (safe)
                                            repoViewModel.avgPerDay.value =
                                                if (safeData30.isNotEmpty()) safeData30.average() else 0.0

                                            // WEEKLY TRENDS based on safe 30-day data
                                            val weekly = safeData30.chunked(7).map { it.sum() }
                                            repoViewModel.weeklyCommits.value = weekly

                                            // SCORE CALCULATION remains similar but based on real commit count
                                            val totalCommits = commits.size
                                            val contributors = repoViewModel.contributors.value?.size ?: 1
                                            val score = (totalCommits / 50 + contributors * 5).coerceAtMost(100)
                                            repoViewModel.repoScore.value = score

                                            val confidence = (score * 0.9).toInt()
                                            repoViewModel.confidence.value = confidence

                                            // Mirror some of the previous simple metrics
                                            repoViewModel.commitCount.value = totalCommits
                                            val weeklyGrowth = (totalCommits % 20) + 5
                                            repoViewModel.weeklyGrowth.value = weeklyGrowth
                                            val reviewSpeed = (totalCommits % 5) + 1
                                            repoViewModel.reviewSpeed.value = reviewSpeed

                                            // Run AI-style repository analysis once we have metrics
                                            runAiAnalysis()
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<List<CommitResponse>>,
                                        t: Throwable
                                    ) {
                                        // Silent failure for now; keep previous metrics
                                    }
                                })

                            // README analysis
                            RetrofitClient.apiService.getReadme(owner, repo)
                                .enqueue(object : Callback<String> {
                                    override fun onResponse(
                                        call: Call<String>,
                                        response: Response<String>
                                    ) {
                                        val readme = response.body() ?: ""
                                        val quality = when {
                                            readme.length > 5000 -> "Excellent Documentation"
                                            readme.length > 1000 -> "Good Documentation"
                                            readme.isNotEmpty() -> "Basic Documentation"
                                            else -> "No Documentation"
                                        }
                                        repoViewModel.readmeQuality.value = quality
                                    }

                                    override fun onFailure(call: Call<String>, t: Throwable) {
                                        android.util.Log.e("API_README", "Failed: ${t.localizedMessage}")
                                    }
                                })

                            Toast.makeText(
                                context,
                                getString(
                                    R.string.repo_summary_toast,
                                    repoData.name,
                                    repoData.stargazersCount,
                                    repoData.forksCount,
                                    repoData.language
                                ),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                getString(R.string.error_empty_response),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        if (response.code() == 403) {
                            if (!showCachedFallbackIfAvailable(owner, repo, repoKey)) {
                                Toast.makeText(
                                    context,
                                    getString(R.string.error_too_many_requests),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                getString(R.string.error_repo_request_failed, response.code()),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    setAnalyzeLoading(false)
                }

                override fun onFailure(call: Call<RepoResponse>, t: Throwable) {
                    if (!showCachedFallbackIfAvailable(owner, repo, repoKey)) {
                        resetAnalyzedState()
                        Toast.makeText(
                            context,
                            getString(R.string.error_repo_request_exception, t.localizedMessage ?: ""),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    setAnalyzeLoading(false)
                }
            })
    }

    override fun onResume() {
        super.onResume()
        loadRecentRepos()
    }

    private fun runAiAnalysis() {
        val commitCount = repoViewModel.commitCount.value ?: 0
        val contributorCount = repoViewModel.contributors.value?.size ?: 0
        val stars = repoViewModel.stars.value ?: 0
        val lastUpdated = repoViewModel.lastUpdated.value ?: ""

        // HEALTH CALCULATION (SMART)
        val healthResId = when {
            commitCount > 2000 && contributorCount > 20 && stars > 5000 -> R.string.insights_health_excellent
            commitCount > 500 && contributorCount > 5 -> R.string.insights_health_healthy_smart
            commitCount > 100 -> R.string.insights_health_moderate_smart
            else -> R.string.insights_health_needs_improvement_smart
        }
        repoViewModel.repoHealth.value = getString(healthResId)

        // RISK ANALYSIS (DYNAMIC)
        val riskList = mutableListOf<String>()

        if (contributorCount < 3) {
            riskList.add(getString(R.string.insights_risk_single_or_low_contributors))
        }

        if (commitCount < 100) {
            riskList.add(getString(R.string.insights_risk_low_activity_dynamic))
        }

        if (stars < 50) {
            riskList.add(getString(R.string.insights_risk_low_adoption))
        }

        if (lastUpdated.contains("2020") || lastUpdated.contains("2021")) {
            riskList.add(getString(R.string.insights_risk_outdated))
        }

        repoViewModel.risks.value = riskList

        // SUGGESTIONS (BASED ON RISKS)
        val suggestionList = mutableListOf<String>()

        if (contributorCount < 5) {
            suggestionList.add(getString(R.string.insights_suggestion_add_contributors))
        }

        if (commitCount < 200) {
            suggestionList.add(getString(R.string.insights_suggestion_increase_commits))
        }

        if (stars < 100) {
            suggestionList.add(getString(R.string.insights_suggestion_improve_visibility))
        }

        if (lastUpdated.contains("2020") || lastUpdated.contains("2021")) {
            suggestionList.add(getString(R.string.insights_suggestion_resume_development))
        }

        if (suggestionList.isEmpty()) {
            suggestionList.add(getString(R.string.insights_suggestion_well_maintained))
        }

        repoViewModel.suggestions.value = suggestionList

        // Generate full AI report text for download
        val report = """
            Repository Analysis Report

            Score: ${repoViewModel.repoScore.value ?: 0}/100
            Confidence: ${repoViewModel.confidence.value ?: 0}%

            Peak Activity: ${repoViewModel.peakDay.value ?: 0}
            Average Commits/Day: ${repoViewModel.avgPerDay.value ?: 0.0}

            README Quality: ${repoViewModel.readmeQuality.value ?: "Unknown"}

            This repository shows ${repoViewModel.repoHealth.value ?: "Unknown"} performance.

            Risks:
            ${repoViewModel.risks.value?.joinToString("\n") ?: "None"}

            Suggestions:
            ${repoViewModel.suggestions.value?.joinToString("\n") ?: "None"}
        """.trimIndent()

        repoViewModel.fullReport.value = report
    }
}
