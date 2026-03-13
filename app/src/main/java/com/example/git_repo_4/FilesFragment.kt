package com.example.git_repo_4

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FilesFragment : Fragment(R.layout.fragment_files) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler: RecyclerView = view.findViewById(R.id.recyclerRecentFiles)
        val items = listOf(
            FileUpdate("index.js", "Updated 2h ago", "+142", "-12", FileUpdateType.JS),
            FileUpdate("auth_service.py", "Updated 5h ago", "+89", "-45", FileUpdateType.CODE),
            FileUpdate("README.md", "Updated 1d ago", "+12", "-0", FileUpdateType.DOC),
            FileUpdate("globals.css", "Updated 2d ago", "+24", "-18", FileUpdateType.CSS)
        )

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = FileUpdateAdapter(items)
        recycler.isNestedScrollingEnabled = false
    }
}
