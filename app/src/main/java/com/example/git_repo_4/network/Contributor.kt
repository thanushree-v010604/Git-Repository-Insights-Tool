package com.example.git_repo_4.network

import com.google.gson.annotations.SerializedName

data class Contributor(
    val login: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("html_url") val htmlUrl: String?,
    @SerializedName("contributions") val contributions: Int?
)

