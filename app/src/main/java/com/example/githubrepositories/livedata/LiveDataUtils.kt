package com.example.githubrepositories.livedata

import rx.Observable

object LiveDataUtils {
    fun <T> asLiveData(observable: Observable<T>): TaskLiveData<T> {
        return object : TaskLiveData<T>() {
            var subscription = observable.subscribe(
                { t -> postValue(TaskResult(t, null)) },
                { throwable -> postValue(TaskResult(null, throwable)) }
            )

            override fun onInactive() {
                super.onInactive()
                subscription.unsubscribe()
            }
        }
    }
}
