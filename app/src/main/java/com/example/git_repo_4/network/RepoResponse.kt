package com.example.git_repo_4.network

import com.google.gson.annotations.SerializedName

/**
 * Deprecated duplicate of model.RepoResponse kept temporarily for backwards compatibility.
 * Prefer using com.example.git_repo_4.model.RepoResponse instead.
 */
@Deprecated("Use com.example.git_repo_4.model.RepoResponse instead")
data class RepoResponse(
    val name: String?,
    val description: String?,
    @SerializedName("stargazers_count") val stargazersCount: Int?,
    @SerializedName("forks_count") val forksCount: Int?,
    @SerializedName("language") val primaryLanguage: String?
)
