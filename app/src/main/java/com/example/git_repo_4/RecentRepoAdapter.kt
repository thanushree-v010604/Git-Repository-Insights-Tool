package com.example.git_repo_4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.git_repo_4.model.RecentRepo

/**
 * Adapter backing the "Recently Analyzed" list on the home screen.
 * Uses the existing item_repo_card layout to preserve design.
 */
class RecentRepoAdapter(
    private var items: List<RecentRepo>
) : RecyclerView.Adapter<RecentRepoAdapter.RecentRepoViewHolder>() {

    class RecentRepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.tvRepoName)
        val timeText: TextView = itemView.findViewById(R.id.tvRepoTime)
        val badgeText: TextView = itemView.findViewById(R.id.tvRepoBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentRepoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_repo_card, parent, false)
        return RecentRepoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentRepoViewHolder, position: Int) {
        val item = items[position]
        holder.nameText.text = item.name
        holder.timeText.text = holder.itemView.context.getString(R.string.analyzed_recently)
        // Keep status styling but ensure text matches the requirement.
        holder.badgeText.text = holder.itemView.context.getString(R.string.status_healthy)
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<RecentRepo>) {
        items = newItems
        notifyDataSetChanged()
    }
}
