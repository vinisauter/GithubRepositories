package com.example.githubrepositories.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.githubrepositories.repository.GitHubRepositoryContract
import com.example.githubrepositories.repository.ServiceLocator

private const val PAGE_SIZE = 10

class GitHubViewModel : ViewModel() {
    private val repository: GitHubRepositoryContract =
        ServiceLocator.instance().getRepository(GitHubRepositoryContract.Type.REMOTE)

    private val query = MutableLiveData<String>()
    private val repoResult = Transformations.map(query) {
        repository.searchRepositories(it, PAGE_SIZE)
    }
    val posts = Transformations.switchMap(repoResult) { it.pagedList }!!
    val refreshState = Transformations.switchMap(repoResult) { it.refreshState }!!

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun setQuery(queryString: String): Boolean {
        if (query.value == queryString) {
            return false
        }
        query.value = queryString
        return true
    }

    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }

    fun currentQuery(): String? = query.value
}