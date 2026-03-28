package com.example.git_repo_4.utils

/**
 * Helper to parse GitHub repository URLs into (owner, repo).
 * Example: https://github.com/facebook/react -> ("facebook", "react")
 */
object GitHubUrlParser {

    fun parse(url: String): Pair<String, String>? {
        return try {
            val cleanUrl = url
                .removePrefix("https://")
                .removePrefix("http://")
                .removePrefix("www.")
                .replace("github.com/", "")
                .trim('/')

            val parts = cleanUrl.split("/")
            if (parts.size < 2) return null
            val owner = parts[0]
            val repo = parts[1]
            if (owner.isBlank() || repo.isBlank()) null else Pair(owner, repo)
        } catch (e: Exception) {
            null
        }
    }
}

