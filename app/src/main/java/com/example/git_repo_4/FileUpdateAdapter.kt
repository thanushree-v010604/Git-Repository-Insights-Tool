package com.example.git_repo_4

import androidx.core.graphics.toColorInt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

enum class FileUpdateType { JS, CODE, DOC, CSS }

data class FileUpdate(
    val fileName: String,
    val updatedText: String,
    val additions: String,
    val deletions: String,
    val type: FileUpdateType
)

class FileUpdateAdapter(items: List<FileUpdate>) :
    RecyclerView.Adapter<FileUpdateAdapter.FileUpdateViewHolder>() {

    private val allItems = items.toMutableList()
    private val filteredItems = items.toMutableList()
    private var currentQuery: String = ""

    fun submitItems(items: List<FileUpdate>) {
        allItems.clear()
        allItems.addAll(items)
        applyFilter(currentQuery)
    }

    fun filter(query: String) {
        currentQuery = query.trim()
        applyFilter(currentQuery)
    }

    private fun applyFilter(query: String) {
        filteredItems.clear()
        if (query.isEmpty()) {
            filteredItems.addAll(allItems)
        } else {
            filteredItems.addAll(allItems.filter { it.fileName.contains(query, ignoreCase = true) })
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileUpdateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file_update, parent, false)
        return FileUpdateViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileUpdateViewHolder, position: Int) {
        holder.bind(filteredItems[position])
    }

    override fun getItemCount(): Int = filteredItems.size

    class FileUpdateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val iconBg: View = view.findViewById(R.id.viewIconBg)
        private val icon: ImageView = view.findViewById(R.id.ivFileType)
        private val tvName: TextView = view.findViewById(R.id.tvFileName)
        private val tvUpdated: TextView = view.findViewById(R.id.tvUpdatedAt)
        private val tvAdditions: TextView = view.findViewById(R.id.tvAdditions)
        private val tvDeletions: TextView = view.findViewById(R.id.tvDeletions)

        fun bind(item: FileUpdate) {
            tvName.text = item.fileName
            tvUpdated.text = item.updatedText
            tvAdditions.text = item.additions
            tvDeletions.text = item.deletions

            // Icon + background based on type (using system icons as placeholders)
            when (item.type) {
                FileUpdateType.JS -> {
                    icon.setImageResource(android.R.drawable.ic_menu_manage)
                }
                FileUpdateType.CODE -> {
                    icon.setImageResource(android.R.drawable.ic_menu_edit)
                }
                FileUpdateType.DOC -> {
                    icon.setImageResource(android.R.drawable.ic_menu_agenda)
                }
                FileUpdateType.CSS -> {
                    icon.setImageResource(android.R.drawable.ic_menu_sort_by_size)
                }
            }

            // Accent square background
            iconBg.setBackgroundResource(R.drawable.bg_file_icon_square)

            // Color additions/deletions text using KTX extension
            tvAdditions.setTextColor("#57D66A".toColorInt())
            tvDeletions.setTextColor("#FF4A4A".toColorInt())
        }
    }
}
