package com.example.git_repo_4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContributorAdapter(private val contributors: List<Contributor>) :
    RecyclerView.Adapter<ContributorAdapter.ContributorViewHolder>() {

    class ContributorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRank           : TextView = itemView.findViewById(R.id.tvRank)
        val tvName           : TextView = itemView.findViewById(R.id.tvContributorName)
        val tvRole           : TextView = itemView.findViewById(R.id.tvContributorRole)
        val tvCommitCount    : TextView = itemView.findViewById(R.id.tvCommitCount)
        val tvWeeklyChange   : TextView = itemView.findViewById(R.id.tvWeeklyChange)
        val tvProfileInitial : TextView = itemView.findViewById(R.id.ivProfileImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contributor, parent, false)
        return ContributorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContributorViewHolder, position: Int) {
        val contributor = contributors[position]
        holder.tvRank.text        = contributor.rank.toString()
        holder.tvName.text        = contributor.name
        holder.tvRole.text        = contributor.role
        holder.tvCommitCount.text = contributor.commitCount
        holder.tvWeeklyChange.text = contributor.weeklyChange
        holder.tvProfileInitial.text = contributor.name.firstOrNull()?.uppercase() ?: "-"
    }

    override fun getItemCount(): Int = contributors.size
}
