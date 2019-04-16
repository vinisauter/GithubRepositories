package com.example.githubrepositories.repository.local

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.githubrepositories.livedata.TaskResult
import com.example.githubrepositories.repository.model.Repository
import com.example.githubrepositories.repository.model.Result
import com.example.githubrepositories.repository.remote.GitHubService
import java.util.concurrent.Executor

class LocalGithubPageKeyedDataSource(
    private val searchQuery: String,
    private val apiService: GitHubService,
    private val retryExecutor: Executor
) : PageKeyedDataSource<Int, Repository>() {
    var retry: (() -> Any)? = null
    val network = MutableLiveData<TaskResult<Result>>()
    val initial = MutableLiveData<TaskResult<Result>>()

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Repository>
    ) {
        // ignored, since we only ever append to our initial load
    }


    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let { retry ->
            retryExecutor.execute { retry() }
        }
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Repository>
    ) {

        val currentPage = 1
        val nextPage = currentPage + 1

        postInitialState(TaskResult.loading())
        apiService.searchListRepos(
            query = searchQuery,
            page = currentPage,
            per_page = params.requestedLoadSize
        ).subscribe({ responseBody ->
            val items = responseBody?.items ?: emptyList()
            retry = null
            postInitialState(TaskResult.success(responseBody))
            callback.onResult(items, null, nextPage)
        }, { errorMessage ->
            retry = { loadInitial(params, callback) }
            postInitialState(TaskResult.error(errorMessage))
        })
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Repository>
    ) {

        val currentPage = params.key
        val nextPage = currentPage + 1

        postInitialState(TaskResult.loading())
        apiService.searchListRepos(
            query = searchQuery,
            page = currentPage,
            per_page = params.requestedLoadSize
        ).subscribe({ responseBody ->
            val items = responseBody?.items ?: emptyList()
            retry = null
            callback.onResult(items, nextPage)
            postAfterState(TaskResult.success(responseBody))
        }, { errorMessage ->
            retry = { loadAfter(params, callback) }
            postAfterState(TaskResult.error(errorMessage))
        })
    }

    private fun postInitialState(state: TaskResult<Result>) {
        network.postValue(state)
        initial.postValue(state)
    }

    private fun postAfterState(state: TaskResult<Result>) {
        network.postValue(state)
    }
}