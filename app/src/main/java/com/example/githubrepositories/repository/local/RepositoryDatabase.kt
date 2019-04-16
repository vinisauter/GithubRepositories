package com.example.githubrepositories.repository.local

import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.example.githubrepositories.repository.GitHubRepositoryContract
import com.example.githubrepositories.repository.GithubDataSourceFactory
import com.example.githubrepositories.repository.Listing
import com.example.githubrepositories.repository.model.Repository
import com.example.githubrepositories.repository.remote.GitHubService
import java.util.concurrent.Executor

class RepositoryDatabase(
    private val db: AppDatabase,
    private val gitHubService: GitHubService,
    private val fetchExecutor: Executor
) : GitHubRepositoryContract {
    //TODO: DB_LISTING
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
