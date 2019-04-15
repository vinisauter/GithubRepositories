@file:Suppress("unused")

package com.example.githubrepositories.livedata

/**
 * The current state of a unit of work.
 */
enum class State {
    LOADING,
    SUCCEEDED,
    ERROR;

    val isFinished: Boolean
        get() = this == SUCCEEDED || this == ERROR

}
