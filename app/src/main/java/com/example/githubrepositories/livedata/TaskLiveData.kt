@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.example.githubrepositories.livedata

import androidx.lifecycle.*

open class TaskLiveData<T> : LiveData<TaskResult<T>> {

    fun setError(throwable: Throwable) {
        postValue(TaskResult(null, throwable))
    }

    fun setSuccess(t: T) {
        postValue(TaskResult(t, null))
    }

    constructor(pair: TaskResult<T>) {
        this.postValue(pair)
    }

    constructor()

    fun observeState(owner: LifecycleOwner, observer: Observer<State>): TaskLiveData<T> {
        state().observe(owner, observer)
        return this
    }

    fun state(): LiveData<State> {
        val statusLiveData = MutableLiveData<State>()
        statusLiveData.postValue(State.LOADING)
        return Transformations.switchMap(this) { input ->
            if (input != null) {
                if (input.error != null) {
                    statusLiveData.postValue(State.ERROR)
                } else {
                    statusLiveData.postValue(State.SUCCEEDED)
                }
            }
            statusLiveData
        }
    }

    fun observeValue(owner: LifecycleOwner, observer: Observer<T>): TaskLiveData<T> {
        value().observe(owner, observer)
        return this
    }

    fun value(): LiveData<T> {
        val tLiveData = MutableLiveData<T>()
        return Transformations.switchMap(this) { input ->
            if (input?.value != null) {
                tLiveData.postValue(input.value)
            }
            tLiveData
        }
    }

    fun observeError(owner: LifecycleOwner, observer: Observer<Throwable>): TaskLiveData<T> {
        error().observe(owner, observer)
        return this
    }

    fun error(): LiveData<Throwable> {
        val throwableLiveData = MutableLiveData<Throwable>()
        return Transformations.switchMap(this) { input ->
            if (input?.error != null) {
                throwableLiveData.postValue(input.error)
            }
            throwableLiveData
        }
    }

    companion object {
        fun <T> loading(): TaskLiveData<T> {
            return TaskLiveData(TaskResult<T>(null, null))
        }

        fun <T> error(throwable: Throwable): TaskLiveData<T> {
            return TaskLiveData(TaskResult<T>(null, throwable))
        }

        fun <T> success(t: T): TaskLiveData<T> {
            return TaskLiveData(TaskResult(t, null))
        }
    }
}