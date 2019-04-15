package com.example.githubrepositories.repository.remote

import com.example.githubrepositories.model.Repo
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

interface GitHubService {

    @GET("/search/repositories?")
    fun listRepos(
        @Query("q") repos: String,
        @Query("per_page") per_page: Int?,
        @Query("page") page: Int?
    ): Observable<Repo>

    @GET("users/{user}/repos")
    fun listUsersRepos(@Path("user") user: String): Observable<List<Repo>>
}