package com.example.git_repo_4.network

import com.example.git_repo_4.model.Commit
import com.example.git_repo_4.model.CommitDetailResponse
import com.example.git_repo_4.model.CommitResponse
import com.example.git_repo_4.model.Contributor
import com.example.git_repo_4.model.RepoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApiService {

    @GET("repos/{owner}/{repo}")
    fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Call<RepoResponse>

    @GET("repos/{owner}/{repo}/contributors")
    fun getContributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Call<List<Contributor>>

    @GET("repos/{owner}/{repo}/commits")
    fun getCommits(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Call<List<CommitResponse>>

    @GET("repos/{owner}/{repo}/commits/{sha}")
    fun getCommitDetails(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("sha") sha: String
    ): Call<CommitDetailResponse>

    // Language statistics for impact distribution
    @GET("repos/{owner}/{repo}/languages")
    fun getLanguages(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Call<Map<String, Int>>

    // Lightweight commits list when we only need the count for insights
    @GET("repos/{owner}/{repo}/commits")
    fun getCommitsForInsights(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Call<List<CommitResponse>>

    // Raw README content for documentation quality analysis
    @GET("repos/{owner}/{repo}/readme")
    fun getReadme(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Call<String>
}
