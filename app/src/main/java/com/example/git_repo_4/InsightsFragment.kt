package com.example.git_repo_4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.git_repo_4.viewmodel.SharedRepoViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.Locale

class InsightsFragment : Fragment() {

    private lateinit var contentView: NestedScrollView
    private lateinit var emptyStateText: TextView
    private lateinit var donutChart: PieChart
    private lateinit var rvContributors: RecyclerView

    private lateinit var weeklyGrowthText: TextView
    private lateinit var reviewSpeedText: TextView

    private lateinit var corePercentText: TextView
    private lateinit var uiPercentText: TextView
    private lateinit var apiPercentText: TextView
    private lateinit var docsPercentText: TextView

    private lateinit var coreSegment: View
    private lateinit var uiSegment: View
    private lateinit var apiSegment: View
    private lateinit var docsSegment: View

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
        contentView = view.findViewById(R.id.insightsContent)
        emptyStateText = view.findViewById(R.id.tvEmptyState)
        donutChart = view.findViewById(R.id.donutChart)
        rvContributors = view.findViewById(R.id.rvContributors)

        weeklyGrowthText = view.findViewById(R.id.tvWeeklyGrowth)
        reviewSpeedText = view.findViewById(R.id.tvReviewSpeed)

        corePercentText = view.findViewById(R.id.corePercent)
        uiPercentText = view.findViewById(R.id.uiPercent)
        apiPercentText = view.findViewById(R.id.apiPercent)
        docsPercentText = view.findViewById(R.id.docsPercent)

        coreSegment = view.findViewById(R.id.coreSegment)
        uiSegment = view.findViewById(R.id.uiSegment)
        apiSegment = view.findViewById(R.id.apiSegment)
        docsSegment = view.findViewById(R.id.docsSegment)

        healthText = view.findViewById(R.id.healthText)
        risksText = view.findViewById(R.id.risksText)
        suggestionsText = view.findViewById(R.id.suggestionsText)

        setupContributors()
        updateDonutChart(emptyList())

        repoViewModel.isRepoLoaded.observe(viewLifecycleOwner) { loaded ->
            toggleRepoState(loaded == true)
        }

        repoViewModel.contributors.observe(viewLifecycleOwner) { apiContributors ->
            val mapped = apiContributors.orEmpty()
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

        repoViewModel.languageStats.observe(viewLifecycleOwner) { languages ->
            val safeLanguages = languages.orEmpty()

            // Enhanced classification logic for commit-based analysis
            // Classify into 4 categories based on file types and content

            var coreBytes = 0L    // Backend logic, business logic, core algorithms
            var uiBytes = 0L      // UI markup, styling, frontend files
            var apiBytes = 0L     // API definitions, networking, endpoints, integrations
            var docBytes = 0L     // Documentation and comments
            var totalBytes = 0L

            // Classification based on file types and content patterns
            for ((lang, bytes) in safeLanguages) {
                totalBytes += bytes

                when (lang) {
                    // UI/UX FILES (layouts, design, frontend files)
                    in setOf(
                        "XML", "HTML", "CSS", "SCSS", "LESS", "SASS",
                        "Dart", "Vue", "XAML", "Svelte", "JSX"
                    ) -> {
                        uiBytes += bytes
                    }

                    // API FILES (networking, endpoints, integrations)
                    in setOf(
                        "JSON", "YAML", "TOML", "Protocol Buffer", "Protobuf",
                        "GraphQL", "XML Schema", "OpenAPI", "Swagger"
                    ) -> {
                        apiBytes += bytes
                    }

                    // DOCUMENTATION FILES
                    in setOf(
                        "Markdown", "ReStructuredText", "AsciiDoc", "Tex", "LaTeX",
                        "Org", "POD", "troff"
                    ) -> {
                        docBytes += bytes
                    }

                    // CORE FILES (backend / logic / main code) - everything else
                    else -> {
                        // Additional checks for API-related content
                        if (lang.contains("api", ignoreCase = true) ||
                            lang.contains("service", ignoreCase = true) ||
                            lang.contains("network", ignoreCase = true) ||
                            lang.contains("retrofit", ignoreCase = true)) {
                            apiBytes += bytes
                        } else {
                            coreBytes += bytes
                        }
                    }
                }
            }

            // If no data, show placeholders
            if (totalBytes <= 0) {
                corePercentText.text = getString(R.string.insights_percent_placeholder)
                uiPercentText.text = getString(R.string.insights_percent_placeholder)
                apiPercentText.text = getString(R.string.insights_percent_placeholder)
                docsPercentText.text = getString(R.string.insights_percent_placeholder)
                updateDonutChart(emptyList())
                return@observe
            }

            // Calculate percentage distribution: (category_commits / total_commits) * 100
            val corePercent = ((coreBytes.toDouble() / totalBytes) * 100).toInt()
            val uiPercent = ((uiBytes.toDouble() / totalBytes) * 100).toInt()
            val apiPercent = ((apiBytes.toDouble() / totalBytes) * 100).toInt()
            val docsPercent = ((docBytes.toDouble() / totalBytes) * 100).toInt()

            // Ensure total = 100% by adjusting the largest category if needed
            val totalPercent = corePercent + uiPercent + apiPercent + docsPercent
            if (totalPercent != 100) {
                val diff = 100 - totalPercent
                // Adjust core (largest category) to make total 100%
                val adjustedCore = (corePercent + diff).coerceAtLeast(0)
                // But since we use Int, and to avoid negative, perhaps just set as is
                // For precision, but since Int, it's approximate
            }

            // Update labels below chart dynamically
            corePercentText.text = getString(R.string.insights_core_percent_labeled, corePercent)
            uiPercentText.text = getString(R.string.insights_ui_percent_labeled, uiPercent)
            apiPercentText.text = getString(R.string.insights_api_percent_labeled, apiPercent)
            docsPercentText.text = getString(R.string.insights_docs_percent_labeled, docsPercent)

            // Update Impact Distribution bar
            updateImpactBar(corePercent, uiPercent, apiPercent, docsPercent)

            // Always show 4 segments in donut chart, even if value is 0%
            val entries = listOf(
                PieEntry(corePercent.toFloat(), "Core"),
                PieEntry(uiPercent.toFloat(), "UI/UX"),
                PieEntry(apiPercent.toFloat(), "API"),
                PieEntry(docsPercent.toFloat(), "Docs")
            )

            updateDonutChart(entries)
        }

        repoViewModel.commitCount.observe(viewLifecycleOwner) { total ->
            // totalCommitsText.text = total?.toString() ?: getString(R.string.insights_total_commits_placeholder)
            if (total != null && total > 0) {
                val reviewSpeed = (total / 50).coerceAtLeast(1)
                reviewSpeedText.text = getString(R.string.insights_review_speed_format, reviewSpeed)
            } else {
                reviewSpeedText.text = getString(R.string.insights_review_speed_placeholder)
            }
        }

        repoViewModel.weeklyGrowth.observe(viewLifecycleOwner) { growth ->
            weeklyGrowthText.text = if (growth != null) {
                getString(R.string.insights_weekly_growth_format, growth)
            } else {
                getString(R.string.insights_weekly_growth_placeholder)
            }
        }

        repoViewModel.repoHealth.observe(viewLifecycleOwner) { health ->
            healthText.text = health ?: getString(R.string.insights_health_placeholder)
        }

        repoViewModel.risks.observe(viewLifecycleOwner) { riskItems ->
            risksText.text = if (riskItems.isNullOrEmpty()) {
                getString(R.string.insights_risks_placeholder)
            } else {
                riskItems.joinToString(separator = "\n")
            }
        }

        repoViewModel.suggestions.observe(viewLifecycleOwner) { suggestionItems ->
            suggestionsText.text = if (suggestionItems.isNullOrEmpty()) {
                getString(R.string.insights_suggestions_placeholder)
            } else {
                suggestionItems.joinToString(separator = "\n")
            }
        }
    }

    private fun toggleRepoState(isLoaded: Boolean) {
        contentView.isVisible = isLoaded
        emptyStateText.isVisible = !isLoaded
    }

    private fun updateDonutChart(entries: List<PieEntry>) {
        if (entries.isEmpty()) {
            donutChart.clear()
            donutChart.invalidate()
            return
        }

        // Dynamic color assignment based on percentage
        val colors = entries.map { getColorForPercentage(it.value.toInt()) }

        val dataSet = PieDataSet(entries, "").apply {
            setColors(colors)

            // REMOVE ALL INSIDE LABEL CLUTTER - Disable drawing values inside slices
            setDrawValues(false)

            // Position labels OUTSIDE the slices for maximum readability
            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

            // Enable value lines connecting label to slice with optimized lengths
            setValueLinePart1Length(0.4f)
            setValueLinePart2Length(0.3f)
            setValueLineColor(android.graphics.Color.WHITE)
            setValueLineWidth(1.2f)

            // Add spacing between slices for better visibility (increased from 3f)
            sliceSpace = 4f

            // Enable selection shift for better visibility of small slices
            selectionShift = 5f

            // Value text properties for clean outside labels
            valueTextColor = android.graphics.Color.WHITE
            valueTextSize = 9f  // Reduced from 10f for less overlap
        }

        val data = PieData(dataSet).apply {
            // Custom formatter to display category names with percentages in clean format
            setValueFormatter(object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format(Locale.getDefault(), "%.0f%%", value)  // No decimals for cleaner look

                }

                override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                    // Clean single-line format: "Category - 40%"
                    return if (pieEntry != null) {
                        "${pieEntry.label} - ${String.format(Locale.getDefault(), "%.0f%%", value)}"
                    } else {
                        ""
                    }
                }
            })
            setValueTextSize(9f)  // Smaller size for less overlap
        }

        donutChart.apply {
            this.data = data

            // Dark background compatibility
            setBackgroundColor(android.graphics.Color.TRANSPARENT)

            // Donut style (modern look)
            setDrawHoleEnabled(true)
            holeRadius = 58f  // Slightly smaller
            transparentCircleRadius = 62f
            setHoleColor(android.graphics.Color.parseColor("#1A1A1A")) // very dark background
            setTransparentCircleColor(android.graphics.Color.parseColor("#1A1A1A"))

            // Center text configuration
            setDrawCenterText(true)
            setCenterText(getString(R.string.insights_chart_center_text))
            setCenterTextSize(16f)
            setCenterTextColor(android.graphics.Color.WHITE)

            // Chart configuration for CLEAN layout
            setDrawEntryLabels(true)  // Show entry labels (outside only)
            setUsePercentValues(true)
            legend.isEnabled = false
            description.isEnabled = false
            setTouchEnabled(false)

            // CLEAN SPACING - More room for outside labels to prevent overlap
            setExtraOffsets(40f, 20f, 40f, 20f)  // Optimized: horizontal 40f, vertical 20f

            // Ensure rotations don't hide small slices
            rotationAngle = 0f

            // Enable smooth rendering animation for clean load
            animateY(1000)

            invalidate()
        }
    }

    private fun updateImpactBar(corePercent: Int, uiPercent: Int, apiPercent: Int, docsPercent: Int) {
        // Update weights for proportional sizing
        (coreSegment.layoutParams as android.widget.LinearLayout.LayoutParams).weight = corePercent.toFloat()
        (uiSegment.layoutParams as android.widget.LinearLayout.LayoutParams).weight = uiPercent.toFloat()
        (apiSegment.layoutParams as android.widget.LinearLayout.LayoutParams).weight = apiPercent.toFloat()
        (docsSegment.layoutParams as android.widget.LinearLayout.LayoutParams).weight = docsPercent.toFloat()

        // Update colors based on percentage
        coreSegment.setBackgroundColor(getColorForPercentage(corePercent))
        uiSegment.setBackgroundColor(getColorForPercentage(uiPercent))
        apiSegment.setBackgroundColor(getColorForPercentage(apiPercent))
        docsSegment.setBackgroundColor(getColorForPercentage(docsPercent))

        // Request layout update
        coreSegment.requestLayout()
        uiSegment.requestLayout()
        apiSegment.requestLayout()
        docsSegment.requestLayout()
    }

    private fun setupContributors() {
        rvContributors.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ContributorAdapter(emptyList())
            isNestedScrollingEnabled = false
        }
    }

    private fun getColorForPercentage(percentage: Int): Int {
        return when (percentage) {
            0 -> "#37474F".toColorInt()  // Light Grey for 0%
            in 1..25 -> "#FF3D00".toColorInt()  // Red
            in 26..50 -> "#FF9100".toColorInt()  // Orange
            in 51..75 -> "#FFD600".toColorInt()  // Yellow
            in 76..100 -> "#00E676".toColorInt()  // Green
            else -> "#37474F".toColorInt()  // Default to grey
        }
    }
}

// UI model used by the Insights contributors list

data class Contributor(
    val rank: Int,
    val name: String,
    val role: String,
    val commitCount: String,
    val weeklyChange: String
)
