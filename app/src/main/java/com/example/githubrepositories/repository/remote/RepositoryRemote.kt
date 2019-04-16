package com.example.githubrepositories.repository.remote

import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.example.githubrepositories.repository.GitHubRepositoryContract
import com.example.githubrepositories.repository.GithubDataSourceFactory
import com.example.githubrepositories.repository.Listing
import com.example.githubrepositories.repository.model.Repository
import java.util.concurrent.Executor

class RepositoryRemote(
    private val gitHubService: GitHubService,
    private val fetchExecutor: Executor
) : GitHubRepositoryContract {
    override fun searchRepositories(query: String, pageSize: Int): Listing<Repository> {
        val sourceFactory = GithubDataSourceFactory(query, gitHubService, fetchExecutor)
        val livePagedList = sourceFactory.toLiveData(
            pageSize = pageSize,
            fetchExecutor = fetchExecutor
        )

        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initial
        }
        return Listing(
            pagedList = livePagedList,
            networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                it.network
            },
            retry = {
                sourceFactory.sourceLiveData.value?.retryAllFailed()
            },
            refresh = {
                sourceFactory.sourceLiveData.value?.invalidate()
            },
            refreshState = refreshState
        )
    }
}
