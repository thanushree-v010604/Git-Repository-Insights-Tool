package com.example.git_repo_4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import androidx.core.graphics.toColorInt

class ActivityFragment : Fragment(R.layout.fragment_activity) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chart: LineChart = view.findViewById(R.id.lineChartCommits)
        val weeklyRecycler: RecyclerView = view.findViewById(R.id.recyclerWeeklyTrends)
        setupLineChart(chart)
        setupWeeklyTrends(weeklyRecycler)
    }

    private fun setupLineChart(chart: LineChart) {
        val points = listOf(
            Entry(0f, 28f), Entry(1f, 31f), Entry(2f, 37f), Entry(3f, 35f), Entry(4f, 42f),
            Entry(5f, 46f), Entry(6f, 39f), Entry(7f, 44f), Entry(8f, 52f), Entry(9f, 58f),
            Entry(10f, 61f), Entry(11f, 54f), Entry(12f, 49f), Entry(13f, 53f), Entry(14f, 62f),
            Entry(15f, 66f), Entry(16f, 57f), Entry(17f, 60f), Entry(18f, 72f), Entry(19f, 78f),
            Entry(20f, 74f), Entry(21f, 68f), Entry(22f, 71f), Entry(23f, 84f), Entry(24f, 76f),
            Entry(25f, 69f), Entry(26f, 64f), Entry(27f, 58f), Entry(28f, 62f), Entry(29f, 67f)
        )

        val dataSet = LineDataSet(points, "Commits").apply {
            color = "#7ED957".toColorInt()
            lineWidth = 2.5f
            setDrawCircles(false)
            setDrawCircleHole(false)
            setDrawFilled(true)
            fillColor = "#447ED957".toColorInt()
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(true)
            valueTextColor = "#7ED957".toColorInt()
            valueTextSize = 9f
            valueFormatter = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String {
                    return if (entry?.x == 23f) "PEAK: 84" else ""
                }
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
                textColor = "#B3D0C6".toColorInt()
                gridColor = "#334A7A66".toColorInt()
                axisMinimum = 0f
                axisMaximum = 90f
                setLabelCount(4, true)
            }

            axisRight.isEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = "#B3D0C6".toColorInt()
                gridColor = "#1A4A7A66".toColorInt()
                valueFormatter = IndexAxisValueFormatter(listOf("Sep 25", "", "", "", "Oct 5", "", "", "", "Oct 15", "", "", "", "Oct 25"))
                granularity = 2.5f
                labelCount = 5
            }

            invalidate()
        }
    }

    private fun setupWeeklyTrends(recyclerView: RecyclerView) {
        val rows = listOf(
            WeeklyTrend("Week 42", "Oct 19 - Oct 25", "294 commits", "+18%", true),
            WeeklyTrend("Week 41", "Oct 12 - Oct 18", "249 commits", "-4%", false)
        )

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
                if (item.positive) "#7ED957".toColorInt() else "#F4A640".toColorInt()
            )
        }
    }
}
