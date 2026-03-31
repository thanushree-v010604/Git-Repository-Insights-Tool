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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class ActivityFragment : Fragment(R.layout.fragment_activity) {

    private val repoViewModel: SharedRepoViewModel by activityViewModels()
    private var currentMode: String = "30"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contentView: NestedScrollView = view.findViewById(R.id.activityContent)
        val emptyStateText: TextView = view.findViewById(R.id.tvEmptyState)
        val chart: LineChart = view.findViewById(R.id.lineChartCommits)
        val weeklyRecycler: RecyclerView = view.findViewById(R.id.recyclerWeeklyTrends)
        val totalCommitsText: TextView = view.findViewById(R.id.tvTotalNumber)
        val peakText: TextView = view.findViewById(R.id.tvPeakValue)
        val avgText: TextView = view.findViewById(R.id.tvAvgValue)
        val dateRangeText: TextView = view.findViewById(R.id.tvDateRange)

        repoViewModel.isRepoLoaded.observe(viewLifecycleOwner) { loaded ->
            val hasRepo = loaded == true
            contentView.isVisible = hasRepo
            emptyStateText.isVisible = !hasRepo
        }

        repoViewModel.commitCount.observe(viewLifecycleOwner) { total ->
            totalCommitsText.text = total?.toString() ?: "--"
        }

        repoViewModel.peakDay.observe(viewLifecycleOwner) { value ->
            peakText.text = value?.toString() ?: "--"
        }

        repoViewModel.avgPerDay.observe(viewLifecycleOwner) { value ->
            avgText.text = if (value != null) {
                String.format(java.util.Locale.getDefault(), "%.1f", value)
            } else {
                "--"
            }
        }

        repoViewModel.dateRange.observe(viewLifecycleOwner) { range ->
            dateRangeText.text = if (range.isNullOrBlank()) "--" else range
        }

        repoViewModel.dailyCommits30.observe(viewLifecycleOwner) { data30 ->
            if (currentMode == "30") {
                updateChart(chart, data30.orEmpty())
            }
        }

        repoViewModel.dailyCommits90.observe(viewLifecycleOwner) { data90 ->
            if (currentMode == "90") {
                updateChart(chart, data90.orEmpty())
            }
        }

        currentMode = "30"
        updateChart(chart, repoViewModel.dailyCommits30.value.orEmpty())

        repoViewModel.weeklyCommits.observe(viewLifecycleOwner) { weekly ->
            setupWeeklyTrends(weeklyRecycler, weekly)
        }
    }

    private fun updateChart(chart: LineChart, dailyData: List<Int>) {
        if (dailyData.isEmpty()) {
            chart.clear()
            chart.invalidate()
            return
        }

        val points = dailyData.mapIndexed { index, value -> Entry(index.toFloat(), value.toFloat()) }

        val dataSet = LineDataSet(points, "Commits").apply {
            color = "#00E5FF".toColorInt()
            lineWidth = 2.5f
            setDrawCircles(false)
            setDrawCircleHole(false)
            setDrawFilled(true)
            fillColor = "#3300E5FF".toColorInt()
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(true)
            valueTextColor = "#00E5FF".toColorInt()
            valueTextSize = 9f
            valueFormatter = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String = ""
            }
        }

        chart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)
            setViewPortOffsets(20f, 24f, 20f, 28f)

            axisLeft.apply {
                textColor = "#7AA6B2".toColorInt()
                gridColor = "#1F2A33".toColorInt()
                axisMinimum = 0f
                axisMaximum = 90f
                setLabelCount(4, true)
            }

            axisRight.isEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = "#7AA6B2".toColorInt()
                gridColor = "#1F2A33".toColorInt()
                valueFormatter = IndexAxisValueFormatter(listOf("Sep 25", "", "", "", "Oct 5", "", "", "", "Oct 15", "", "", "", "Oct 25"))
                granularity = 2.5f
                labelCount = 5
            }

            invalidate()
        }
    }

    private fun setupWeeklyTrends(recyclerView: RecyclerView, weeklyCommits: List<Int>?) {
        val rows = weeklyCommits.orEmpty().mapIndexed { index, total ->
            WeeklyTrend(
                week = "Week ${index + 1}",
                range = "Recent period",
                commits = "$total commits",
                delta = "",
                positive = total >= 0
            )
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = WeeklyTrendAdapter(rows)
        recyclerView.isNestedScrollingEnabled = false
    }
}

data class WeeklyTrend(
    val week: String,
    val range: String,
    val commits: String,
    val delta: String,
    val positive: Boolean
)

private class WeeklyTrendAdapter(
    private val items: List<WeeklyTrend>
) : RecyclerView.Adapter<WeeklyTrendAdapter.WeeklyTrendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyTrendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weekly_trend, parent, false)
        return WeeklyTrendViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeeklyTrendViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class WeeklyTrendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvWeek: TextView = view.findViewById(R.id.tvWeek)
        private val tvRange: TextView = view.findViewById(R.id.tvWeekRange)
        private val tvCommits: TextView = view.findViewById(R.id.tvWeekCommits)
        private val tvDelta: TextView = view.findViewById(R.id.tvWeekDelta)

        fun bind(item: WeeklyTrend) {
            tvWeek.text = item.week
            tvRange.text = item.range
            tvCommits.text = item.commits
            tvDelta.text = item.delta
            tvDelta.setTextColor(
                if (item.positive) "#00E5FF".toColorInt() else "#7AA6B2".toColorInt()
            )
        }
    }
}
