package com.example.githubrepositories.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.githubrepositories.repository.model.Repository
import com.example.githubrepositories.repository.remote.GitHubService
import com.example.githubrepositories.repository.remote.RemoteGithubPageKeyedDataSource
import java.util.concurrent.Executor

class GithubDataSourceFactory(
    private val searchQuery: String,
    private val gitHubService: GitHubService,
    private val retryExecutor: Executor
) : DataSource.Factory<Int, Repository>() {
    val sourceLiveData = MutableLiveData<RemoteGithubPageKeyedDataSource>()
    override fun create(): DataSource<Int, Repository> {
        val source = RemoteGithubPageKeyedDataSource(searchQuery, gitHubService, retryExecutor)
        sourceLiveData.postValue(source)
        return source
    }
}