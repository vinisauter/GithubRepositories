package com.example.githubrepositories.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.githubrepositories.repository.GitHubRepositoryContract
import com.example.githubrepositories.repository.ServiceLocator
import com.example.githubrepositories.repository.model.Repository

private const val PAGE_SIZE = 10

class GitHubViewModel : ViewModel() {
    private val repository: GitHubRepositoryContract =
        ServiceLocator.instance().getRepository(GitHubRepositoryContract.Type.REMOTE)

    fun searchRepositories(query: String): LiveData<PagedList<Repository>> {
        return repository.searchRepositories(query, PAGE_SIZE).pagedList
    }
}