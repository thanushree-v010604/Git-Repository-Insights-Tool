package com.example.git_repo_4.utils

import android.content.Context
import androidx.core.content.edit
import com.example.git_repo_4.model.RecentRepo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Manages the persistent list of recently analyzed repositories.
 *
 * Responsibilities:
 * - Add new repo at the top
 * - Maintain max MAX_ITEMS items
 * - Remove duplicates by repo name
 * - Persist data via SharedPreferences as JSON
 */
object RecentRepoManager {

    private const val PREF_NAME = "recent_repos"
    private const val KEY_LIST = "repo_list"
    private const val MAX_ITEMS = 4

    private val gson = Gson()
    private val typeToken = object : TypeToken<List<RecentRepo>>() {}.type

    /**
     * Save or update a repository in the recent list.
     */
    fun saveRepo(context: Context, repoName: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val current = getRepos(context).toMutableList()

        // Remove any existing entry with the same name (dedupe)
        val filtered = current.filterNot { recent -> recent.name == repoName }.toMutableList()

        // Add new repo at the top (use positional arg to avoid any confusion)
        filtered.add(0, RecentRepo(repoName))

        // Keep only MAX_ITEMS
        val finalList = filtered.take(MAX_ITEMS)

        // Use KTX extension to edit SharedPreferences
        prefs.edit {
            putString(KEY_LIST, gson.toJson(finalList, typeToken))
        }
    }

    /**
     * Returns the current list of recent repositories (already ordered newest first).
     */
    fun getRepos(context: Context): List<RecentRepo> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_LIST, null) ?: return emptyList()

        return try {
            @Suppress("UNCHECKED_CAST")
            gson.fromJson<List<RecentRepo>>(json, typeToken) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
