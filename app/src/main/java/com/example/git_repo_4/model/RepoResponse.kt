package com.example.git_repo_4.model

import com.google.gson.annotations.SerializedName

/**
 * Model reflecting the subset of fields we need from /repos/{owner}/{repo}.
 */
data class RepoResponse(
    val name: String,
    val description: String,
    @SerializedName("stargazers_count") val stargazersCount: Int,
    @SerializedName("forks_count") val forksCount: Int,
    @SerializedName("updated_at") val updatedAt: String,
    val language: String
)
