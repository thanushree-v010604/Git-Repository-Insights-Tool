package com.example.git_repo_4.repository

import com.example.git_repo_4.model.Commit
import com.example.git_repo_4.model.Contributor
import com.example.git_repo_4.model.RepoResponse

/**
 * Simple in-memory holder for the currently analyzed GitHub repository and related data.
 */
object GitHubRepository {

    var currentRepo: RepoResponse? = null
    var contributors: List<Contributor> = emptyList()
    var commits: List<Commit> = emptyList()
    var lastAnalyzedRepoKey: String? = null

    fun cacheRepo(repoKey: String, repo: RepoResponse) {
        lastAnalyzedRepoKey = repoKey
        currentRepo = repo
    }

    fun getCachedRepo(repoKey: String): RepoResponse? {
        return if (lastAnalyzedRepoKey == repoKey) currentRepo else null
    }
}
