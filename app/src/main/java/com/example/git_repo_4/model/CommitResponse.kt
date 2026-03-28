package com.example.git_repo_4.model

// Real commit payload subset for date-based analytics and file insights
// Matches the parts of GitHub /repos/{owner}/{repo}/commits response we care about.
data class CommitResponse(
    val sha: String,
    val commit: CommitData
)

data class CommitData(
    val author: Author
)

data class Author(
    val date: String
)
