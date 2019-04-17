package com.example.githubrepositories.repository.local

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.githubrepositories.livedata.TaskResult
import com.example.githubrepositories.repository.GitHubRepositoryContract
import com.example.githubrepositories.repository.Listing
import com.example.githubrepositories.repository.model.Repository
import com.example.githubrepositories.repository.model.Result
import com.example.githubrepositories.repository.remote.GitHubService
import java.util.concurrent.Executor

class RepositoryBoundaryCallback : PagedList.BoundaryCallback<Repository>() {
    val queryState = MutableLiveData<TaskResult<Result>>()
    fun retryAllFailed() {}
    override fun onZeroItemsLoaded() {}
    override fun onItemAtFrontLoaded(itemAtFront: Repository) {}
    override fun onItemAtEndLoaded(itemAtEnd: Repository) {}
}

class RepositoryDatabase(
    private val db: AppDatabase,
    private val gitHubService: GitHubService,
    private val fetchExecutor: Executor,
    private val defaultPageSize: Int = 10
) : GitHubRepositoryContract {
    override fun searchRepositories(query: String, pageSize: Int): Listing<Repository> {
        val boundaryCallback = RepositoryBoundaryCallback()
        val livePagedList = db.appDao().searchRepositories(query).toLiveData(
            pageSize = pageSize,
            boundaryCallback = boundaryCallback
        )

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh(query)
        }

        return Listing(
            pagedList = livePagedList,
            queryState = boundaryCallback.queryState,
            retry = {
                boundaryCallback.retryAllFailed()
            },
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }

    @MainThread
    private fun refresh(query: String): LiveData<TaskResult<Result>> {
        val state = MutableLiveData<TaskResult<Result>>()
        state.value = TaskResult.loading()
        gitHubService.searchListRepos(query, defaultPageSize, 0)
            .subscribe({ responseBody ->
                val items = responseBody?.items ?: emptyList()
                fetchExecutor.execute {
                    db.runInTransaction {
                        db.appDao().deleteByName(query)
                        db.appDao().insert(items)
                    }
                    state.postValue(TaskResult.success(responseBody))
                }
            }, { errorMessage ->
                state.value = TaskResult.error(errorMessage)
            })
        return state
    }
}
