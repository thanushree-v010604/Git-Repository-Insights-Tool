package com.example.git_repo_4

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class InsightsFragment : Fragment() {

    private lateinit var donutChart: PieChart
    private lateinit var rvContributors: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_insights, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        donutChart    = view.findViewById(R.id.donutChart)
        rvContributors = view.findViewById(R.id.rvContributors)
        setupDonutChart()
        setupContributors()
    }

    private fun setupDonutChart() {
        val entries = listOf(
            PieEntry(45f, "Core"),
            PieEntry(25f, "UI/UX"),
            PieEntry(15f, "API"),
            PieEntry(15f, "Docs")
        )
        val colors = listOf(
            Color.parseColor("#7ED957"),
            Color.parseColor("#5FB942"),
            Color.parseColor("#3A7A24"),
            Color.parseColor("#254F17")
        )
        val dataSet = PieDataSet(entries, "").apply {
            setColors(colors)
            valueTextColor = Color.WHITE
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
            setHoleColor(Color.parseColor("#0F3A28"))
            setTransparentCircleColor(Color.parseColor("#0F3A28"))
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
        val list = listOf(
            Contributor(1, "Alex Rivera",     "Core Architecture", "482 commits", "+24 this week"),
            Contributor(2, "Sarah Chen",      "Lead Designer",     "321 commits", "+12 this week"),
            Contributor(3, "Marcus Wright",   "Backend API",       "214 commits", "+5 this week"),
            Contributor(4, "Elena Rodriguez", "DevOps & CI/CD",    "142 commits", "+31 this week")
        )
        rvContributors.apply {
            layoutManager          = LinearLayoutManager(requireContext())
            adapter                = ContributorAdapter(list)
            isNestedScrollingEnabled = false
        }
    }
}

data class Contributor(
    val rank       : Int,
    val name       : String,
    val role       : String,
    val commitCount: String,
    val weeklyChange: String
)
