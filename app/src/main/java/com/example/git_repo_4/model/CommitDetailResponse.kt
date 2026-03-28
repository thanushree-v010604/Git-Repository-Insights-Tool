package com.example.git_repo_4.model

// Minimal model to capture commit file-level statistics from GitHub
// https://docs.github.com/en/rest/commits/commits?apiVersion=2022-11-28#get-a-commit

data class CommitDetailResponse(
    val files: List<CommitFile>?
)

data class CommitFile(
    val filename: String,
    val additions: Int,
    val deletions: Int
)

