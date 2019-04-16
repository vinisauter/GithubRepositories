package com.example.githubrepositories.repository

import com.example.githubrepositories.repository.model.Repository

interface GitHubRepositoryContract {
    fun searchRepositories(query: String, pageSize: Int): Listing<Repository>

    enum class Type {
        REMOTE,
        LOCAL
    }
}