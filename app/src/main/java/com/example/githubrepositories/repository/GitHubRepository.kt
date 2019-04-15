package com.example.githubrepositories.repository

import com.example.githubrepositories.repository.remote.GitHubService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

open class GitHubRepository private constructor() {

    companion object {
        private val mInstance: GitHubRepository = GitHubRepository()

        @Synchronized
        fun getInstance(): GitHubRepository {
            return mInstance
        }
    }

    val remoteGitService: GitHubService

    init {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        remoteGitService = retrofit.create(GitHubService::class.java)
    }
}
