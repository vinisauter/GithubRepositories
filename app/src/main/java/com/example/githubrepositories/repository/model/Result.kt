package com.example.githubrepositories.repository.model

data class Result(
    val incomplete_results: Boolean?,
    val items: List<Repository>?,
    val total_count: Int?
)