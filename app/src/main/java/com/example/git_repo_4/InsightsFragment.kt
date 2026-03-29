package com.example.git_repo_4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.git_repo_4.repository.GitHubRepository
import com.example.git_repo_4.viewmodel.SharedRepoViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class InsightsFragment : Fragment() {

    private lateinit var donutChart: PieChart
    private lateinit var rvContributors: RecyclerView

    private lateinit var totalCommitsText: TextView
    private lateinit var weeklyGrowthText: TextView
    private lateinit var reviewSpeedText: TextView

    private lateinit var corePercentText: TextView
    private lateinit var uiPercentText: TextView
    private lateinit var apiPercentText: TextView
    private lateinit var docsPercentText: TextView

    private lateinit var healthText: TextView
    private lateinit var risksText: TextView
    private lateinit var suggestionsText: TextView

    private val repoViewModel: SharedRepoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_insights, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        donutChart    = view.findViewById(R.id.donutChart)
        rvContributors = view.findViewById(R.id.rvContributors)

        // Metric TextViews – IDs must already exist in the layout
        totalCommitsText = view.findViewById(R.id.tvTotalCommits)
        weeklyGrowthText = view.findViewById(R.id.tvWeeklyGrowth)
        reviewSpeedText  = view.findViewById(R.id.tvReviewSpeed)

        corePercentText = view.findViewById(R.id.corePercent)
        uiPercentText   = view.findViewById(R.id.uiPercent)
        apiPercentText  = view.findViewById(R.id.apiPercent)
        docsPercentText = view.findViewById(R.id.docsPercent)

        healthText = view.findViewById(R.id.healthText)
        risksText = view.findViewById(R.id.risksText)
        suggestionsText = view.findViewById(R.id.suggestionsText)

        setupDonutChart()
        setupContributors()

        // Observe contributors so the list updates automatically when Home analyzes a repo
        repoViewModel.contributors.observe(viewLifecycleOwner) { apiContributors ->
            if (apiContributors.isNotEmpty()) {
                val mapped = apiContributors
                    .sortedByDescending { it.contributions }
                    .take(4)
                    .mapIndexed { index, c ->
                        Contributor(
                            rank = index + 1,
                            name = c.login,
                            role = getString(R.string.insights_role_top_contributor),
                            commitCount = getString(R.string.insights_commit_count_format, c.contributions),
                            weeklyChange = ""
                        )
                    }
                rvContributors.adapter = ContributorAdapter(mapped)
            }
        }

        // Observe language stats for dynamic impact distribution and percentages
        repoViewModel.languageStats.observe(viewLifecycleOwner) { languages ->
            if (languages != null) {
                val total = languages.values.sum()
                if (total == 0) return@observe
                val sorted = languages.entries.sortedByDescending { it.value }

                val core = (sorted.getOrNull(0)?.value ?: 0) * 100 / total
                val ui = (sorted.getOrNull(1)?.value ?: 0) * 100 / total
                val api = (sorted.getOrNull(2)?.value ?: 0) * 100 / total
                val docs = (sorted.getOrNull(3)?.value ?: 0) * 100 / total

                corePercentText.text = getString(R.string.insights_core_percent_labeled, core)
                uiPercentText.text   = getString(R.string.insights_ui_percent_labeled, ui)
                apiPercentText.text  = getString(R.string.insights_api_percent_labeled, api)
                docsPercentText.text = getString(R.string.insights_docs_percent_labeled, docs)

                // Also update the donut chart sections based on the same percentages
                val entries = mutableListOf<PieEntry>()
                if (core > 0) entries.add(PieEntry(core.toFloat(), sorted.getOrNull(0)?.key ?: getString(R.string.insights_language_core_label)))
                if (ui > 0) entries.add(PieEntry(ui.toFloat(), sorted.getOrNull(1)?.key ?: getString(R.string.insights_language_ui_label)))
                if (api > 0) entries.add(PieEntry(api.toFloat(), sorted.getOrNull(2)?.key ?: getString(R.string.insights_language_api_label)))
                if (docs > 0) entries.add(PieEntry(docs.toFloat(), sorted.getOrNull(3)?.key ?: getString(R.string.insights_language_docs_label)))

                updateDonutChart(entries)
            }
        }

        // Observe total commits
        repoViewModel.commitCount.observe(viewLifecycleOwner) { total ->
            totalCommitsText.text = total?.toString() ?: getString(R.string.insights_total_commits_placeholder)

            // Derive a simple review speed metric from commit volume
            if (total != null && total > 0) {
                val reviewSpeed = (total / 50).coerceAtLeast(1)
                reviewSpeedText.text = getString(R.string.insights_review_speed_format, reviewSpeed)
            }
        }

        // Observe weekly growth percentage
        repoViewModel.weeklyGrowth.observe(viewLifecycleOwner) { growth ->
            if (growth != null) {
                weeklyGrowthText.text = getString(R.string.insights_weekly_growth_format, growth)
            }
        }

        // Observe AI-based health, risks and suggestions
        repoViewModel.repoHealth.observe(viewLifecycleOwner) { health ->
            healthText.text = health ?: getString(R.string.insights_health_placeholder)
        }

        repoViewModel.risks.observe(viewLifecycleOwner) { riskItems ->
            val text = if (riskItems.isNullOrEmpty()) {
                getString(R.string.insights_risks_placeholder)
            } else {
                riskItems.joinToString(separator = "\n")
            }
            risksText.text = text
        }

        repoViewModel.suggestions.observe(viewLifecycleOwner) { suggestionItems ->
            val text = if (suggestionItems.isNullOrEmpty()) {
                getString(R.string.insights_suggestions_placeholder)
            } else {
                suggestionItems.joinToString(separator = "\n")
            }
            suggestionsText.text = text
        }
    }

    private fun setupDonutChart() {
        val repo = GitHubRepository.currentRepo
        val entries = if (repo != null && repo.language.isNotBlank()) {
            listOf(
                PieEntry(70f, repo.language),
                PieEntry(30f, getString(R.string.insights_language_other_label))
            )
        } else {
            listOf(
                PieEntry(45f, getString(R.string.insights_language_core_label)),
                PieEntry(25f, getString(R.string.insights_language_ui_label)),
                PieEntry(15f, getString(R.string.insights_language_api_label)),
                PieEntry(15f, getString(R.string.insights_language_docs_label))
            )
        }
        updateDonutChart(entries)
    }

    private fun updateDonutChart(entries: List<PieEntry>) {
        val colors = listOf(
            "#00F0FF".toColorInt(),
            "#00E5FF".toColorInt(),
            "#00B8D4".toColorInt(),
            "#1F2A33".toColorInt()
        )
        val dataSet = PieDataSet(entries, "").apply {
            setColors(colors)
            valueTextColor = android.graphics.Color.WHITE
            valueTextSize  = 11f
            sliceSpace     = 3f
        }
        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(donutChart))
        }
        donutChart.apply {
            this.data              = data
            holeRadius             = 68f
            transparentCircleRadius = 72f
            setHoleColor("#101820".toColorInt())
            setTransparentCircleColor("#0A0F14".toColorInt())
            setDrawCenterText(false)
            setDrawEntryLabels(false)
            setUsePercentValues(true)
            legend.isEnabled      = false
            description.isEnabled = false
            setTouchEnabled(false)
            invalidate()
        }
    }

    private fun setupContributors() {
        val apiContributors = GitHubRepository.contributors
        val list = if (apiContributors.isNotEmpty()) {
            apiContributors.sortedByDescending { it.contributions }.take(4).mapIndexed { index, c ->
                Contributor(
                    rank = index + 1,
                    name = c.login,
                    role = getString(R.string.insights_role_top_contributor),
                    commitCount = getString(R.string.insights_commit_count_format, c.contributions),
                    weeklyChange = ""
                )
            }
        } else {
            listOf(
                Contributor(1, "Alex Rivera",     "Core Architecture", getString(R.string.insights_commit_count_static_format, 482), getString(R.string.insights_weekly_change_format, 24)),
                Contributor(2, "Sarah Chen",      "Lead Designer",     getString(R.string.insights_commit_count_static_format, 321), getString(R.string.insights_weekly_change_format, 12)),
                Contributor(3, "Marcus Wright",   "Backend API",       getString(R.string.insights_commit_count_static_format, 214), getString(R.string.insights_weekly_change_format, 5)),
                Contributor(4, "Elena Rodriguez", "DevOps &amp; CI/CD",    getString(R.string.insights_commit_count_static_format, 142), getString(R.string.insights_weekly_change_format, 31))
            )
        }
        rvContributors.apply {
            layoutManager          = LinearLayoutManager(requireContext())
            adapter                = ContributorAdapter(list)
            isNestedScrollingEnabled = false
        }
    }
}

// UI model used by the Insights contributors list

data class Contributor(
    val rank       : Int,
    val name       : String,
    val role       : String,
    val commitCount: String,
    val weeklyChange: String
)
