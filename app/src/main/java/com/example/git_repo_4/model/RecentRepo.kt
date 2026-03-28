package com.example.git_repo_4.model

// Simple model for a recently analyzed repository entry.
// Name is the full repo id ("owner/repo"). Status is a fixed label for now but can
// be extended later if needed.
data class RecentRepo(
    val name: String,
    val status: String = "Healthy"
)
