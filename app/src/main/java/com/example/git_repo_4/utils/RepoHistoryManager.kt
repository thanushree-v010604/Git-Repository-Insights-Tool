package com.example.git_repo_4.utils

import android.content.Context

/**
 * Simple helper for persisting a history of analyzed repositories using SharedPreferences.
 *
 * We store the set of repo full names ("owner/repo") as a StringSet. Order is handled when
 * reading via takeLast().reversed().
 */
object RepoHistoryManager {

    private const val PREF_NAME = "repo_history"
    private const val KEY_REPOS = "repos"

    /**
     * Save a repository identifier (e.g. "owner/repo") into history.
     * If the repo already exists, it is moved to the end (most recent).
     */
    fun saveRepo(context: Context, repo: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val list = prefs.getStringSet(KEY_REPOS, mutableSetOf()) ?: mutableSetOf()

        val updated = list.toMutableSet().also {
            // Ensure no duplicates; remove then add so it becomes most recent.
            it.remove(repo)
            it.add(repo)
        }

        prefs.edit().putStringSet(KEY_REPOS, updated).apply()
    }

    /**
     * Returns all stored repository identifiers as a list.
     */
    fun getRepos(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_REPOS, emptySet())?.toList() ?: emptyList()
    }
}

