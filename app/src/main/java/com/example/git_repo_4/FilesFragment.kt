package com.example.git_repo_4

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.git_repo_4.network.RetrofitClient
import com.example.git_repo_4.viewmodel.SharedRepoViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FilesFragment : Fragment(R.layout.fragment_files) {

    private val repoViewModel: SharedRepoViewModel by activityViewModels()
    private lateinit var fileUpdateAdapter: FileUpdateAdapter
    private var searchDialog: AlertDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contentView: NestedScrollView = view.findViewById(R.id.filesContent)
        val emptyStateText: TextView = view.findViewById(R.id.tvEmptyState)
        val totalChangesText: TextView = view.findViewById(R.id.tvTotalChangesValue)

        // Top files rows
        val topFileNameViews = listOf<TextView>(
            view.findViewById(R.id.topFileName1),
            view.findViewById(R.id.topFileName2),
            view.findViewById(R.id.topFileName3),
            view.findViewById(R.id.topFileName4)
        )
        val topFileChangesViews = listOf<TextView>(
            view.findViewById(R.id.topFileChanges1),
            view.findViewById(R.id.topFileChanges2),
            view.findViewById(R.id.topFileChanges3),
            view.findViewById(R.id.topFileChanges4)
        )
        val topFileProgressViews = listOf<ProgressBar>(
            view.findViewById(R.id.topFileProgress1),
            view.findViewById(R.id.topFileProgress2),
            view.findViewById(R.id.topFileProgress3),
            view.findViewById(R.id.topFileProgress4)
        )

        val recentRecycler: RecyclerView = view.findViewById(R.id.recyclerRecentFiles)
        recentRecycler.layoutManager = LinearLayoutManager(requireContext())
        fileUpdateAdapter = FileUpdateAdapter(emptyList())
        recentRecycler.adapter = fileUpdateAdapter

        repoViewModel.isRepoLoaded.observe(viewLifecycleOwner) { loaded ->
            val hasRepo = loaded == true
            contentView.isVisible = hasRepo
            emptyStateText.isVisible = !hasRepo

            if (!hasRepo) {
                totalChangesText.text = "0"
                clearTopFileRows(topFileNameViews, topFileChangesViews, topFileProgressViews)
                fileUpdateAdapter.submitItems(emptyList())
            }
        }

        // STEP 6 — TOTAL CHANGES UI
        repoViewModel.totalChanges.observe(viewLifecycleOwner) { total ->
            totalChangesText.text = (total ?: 0).toString()
        }

        // STEP 7 — MOST MODIFIED FILES (mapped into existing 4 rows with progress)
        repoViewModel.topFiles.observe(viewLifecycleOwner) { files ->
            val safeFiles = files.orEmpty()
            if (safeFiles.isEmpty()) {
                clearTopFileRows(topFileNameViews, topFileChangesViews, topFileProgressViews)
                return@observe
            }

            val max = safeFiles.maxOfOrNull { it.value } ?: 1
            topFileNameViews.forEachIndexed { index, tvName ->
                if (index < safeFiles.size) {
                    val entry = safeFiles[index]
                    tvName.text = entry.key
                    topFileChangesViews[index].text = getString(R.string.files_top_file_changes, entry.value)
                    val percent = (entry.value * 100 / max).coerceIn(0, 100)
                    topFileProgressViews[index].progress = percent
                } else {
                    tvName.text = "-"
                    topFileChangesViews[index].text = getString(R.string.files_top_file_changes, 0)
                    topFileProgressViews[index].progress = 0
                }
            }
        }

        // STEP 8 — RECENT UPDATES (REAL DATA) shown through existing RecyclerView
        repoViewModel.recentFiles.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) {
                fileUpdateAdapter.submitItems(emptyList())
                return@observe
            }
            val items = list.map { fu ->
                FileUpdate(
                    fileName = fu.name,
                    updatedText = "Updated ${fu.time}",
                    additions = fu.change.substringBefore("-"),
                    deletions = "-${fu.change.substringAfter("-", "0")}",
                    type = FileUpdateType.CODE
                )
            }
            fileUpdateAdapter.submitItems(items)
        }

        // Trigger load when we know which repo is currently selected
        repoViewModel.repoName.observe(viewLifecycleOwner) { fullName ->
            if (repoViewModel.isRepoLoaded.value != true || fullName.isNullOrBlank()) return@observe
            val parts = fullName.split("/")
            if (parts.size == 2) {
                loadFileActivity(parts[0], parts[1])
            }
        }
    }

    private fun clearTopFileRows(
        topFileNameViews: List<TextView>,
        topFileChangesViews: List<TextView>,
        topFileProgressViews: List<ProgressBar>
    ) {
        topFileNameViews.forEachIndexed { index, tvName ->
            tvName.text = "-"
            topFileChangesViews[index].text = getString(R.string.files_top_file_changes, 0)
            topFileProgressViews[index].progress = 0
        }
    }

    private fun showSearchDialog() {
        if (searchDialog?.isShowing == true) return

        val searchView = SearchView(requireContext()).apply {
            queryHint = "Search files"
            isIconified = false
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    fileUpdateAdapter.filter(query.orEmpty())
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    fileUpdateAdapter.filter(newText.orEmpty())
                    return true
                }
            })
        }

        searchDialog = AlertDialog.Builder(requireContext())
            .setView(searchView)
            .setOnDismissListener {
                fileUpdateAdapter.filter("")
            }
            .create()
        searchDialog?.show()
    }

    // STEP 2 — MAIN LOGIC
    private fun loadFileActivity(owner: String, repo: String) {
        RetrofitClient.apiService.getCommits(owner, repo)
            .enqueue(object : Callback<List<com.example.git_repo_4.model.CommitResponse>> {
                override fun onResponse(
                    call: Call<List<com.example.git_repo_4.model.CommitResponse>>,
                    response: Response<List<com.example.git_repo_4.model.CommitResponse>>
                ) {
                    if (response.isSuccessful) {
                        val commits = response.body() ?: emptyList()
                        processFiles(owner, repo, commits)
                    }
                }

                override fun onFailure(
                    call: Call<List<com.example.git_repo_4.model.CommitResponse>>,
                    t: Throwable
                ) {
                    // ignore for now
                }
            })
    }

    // STEP 3 — PROCESS FILE DATA (real file names, async-safe)
    private fun processFiles(
        owner: String,
        repo: String,
        commits: List<com.example.git_repo_4.model.CommitResponse>
    ) {
        var totalChanges = 0
        val fileMap = mutableMapOf<String, Int>()
        val recentList = mutableListOf<SharedRepoViewModel.FileUpdate>()

        val selectedCommits = commits.take(5)
        if (selectedCommits.isEmpty()) {
            // No commits, push empty but valid state
            repoViewModel.totalChanges.postValue(0)
            repoViewModel.topFiles.postValue(emptyList())
            repoViewModel.recentFiles.postValue(emptyList())
            return
        }

        var completed = 0

        selectedCommits.forEach { commit ->
            RetrofitClient.apiService
                .getCommitDetails(owner, repo, commit.sha)
                .enqueue(object : Callback<com.example.git_repo_4.model.CommitDetailResponse> {
                    override fun onResponse(
                        call: Call<com.example.git_repo_4.model.CommitDetailResponse>,
                        response: Response<com.example.git_repo_4.model.CommitDetailResponse>
                    ) {
                        val detail = response.body()
                        detail?.files?.forEach { file ->
                            val name = file.filename
                            val additions = file.additions
                            val deletions = file.deletions
                            val changes = additions + deletions

                            totalChanges += changes
                            fileMap[name] = fileMap.getOrDefault(name, 0) + changes

                            val timeAgo = getTimeAgo(commit.commit.author.date)
                            recentList.add(
                                SharedRepoViewModel.FileUpdate(
                                    name = name,
                                    time = timeAgo,
                                    change = "+$additions-$deletions"
                                )
                            )
                        }

                        completed++
                        if (completed == selectedCommits.size) {
                            val topFiles = fileMap.entries
                                .sortedByDescending { it.value }
                                .take(4)

                            repoViewModel.totalChanges.postValue(totalChanges)
                            repoViewModel.topFiles.postValue(topFiles)
                            repoViewModel.recentFiles.postValue(recentList.take(4))
                        }
                    }

                    override fun onFailure(
                        call: Call<com.example.git_repo_4.model.CommitDetailResponse>,
                        t: Throwable
                    ) {
                        completed++
                        if (completed == selectedCommits.size) {
                            val topFiles = fileMap.entries
                                .sortedByDescending { it.value }
                                .take(4)

                            repoViewModel.totalChanges.postValue(totalChanges)
                            repoViewModel.topFiles.postValue(topFiles)
                            repoViewModel.recentFiles.postValue(recentList.take(4))
                        }
                    }
                })
        }
    }

    // STEP 4 — TIME FORMAT
    private fun getTimeAgo(date: String): String {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val past = format.parse(date) ?: return ""
            val now = Date()
            val diff = now.time - past.time
            val hours = diff / (1000 * 60 * 60)
            val days = diff / (1000 * 60 * 60 * 24)
            if (hours < 24) "${hours}h ago" else "${days}d ago"
        } catch (_: Exception) {
            ""
        }
    }
}
