package com.example.githubrepositories.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.example.githubrepositories.livedata.TaskResult
import com.example.githubrepositories.repository.model.Result

data class Listing<T>(
    val pagedList: LiveData<PagedList<T>>,
    val queryState: LiveData<TaskResult<Result>>,
    val refreshState: LiveData<TaskResult<Result>>,
    val refresh: () -> Unit,
    val retry: () -> Unit
)