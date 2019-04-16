package com.example.githubrepositories.repository

import android.app.Application
import androidx.annotation.VisibleForTesting
import com.example.githubrepositories.BaseApplication
import com.example.githubrepositories.repository.local.AppDatabase
import com.example.githubrepositories.repository.local.RepositoryDatabase
import com.example.githubrepositories.repository.remote.GitHubService
import com.example.githubrepositories.repository.remote.RepositoryRemote
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

interface ServiceLocator {
    companion object {
        private val LOCK = Any()
        private var instance: ServiceLocator? = null
        fun instance(): ServiceLocator {
            synchronized(LOCK) {
                if (instance == null) {
                    instance = DefaultServiceLocator(
                        app = BaseApplication.getInstance(),
                        useInMemoryDb = false
                    )
                }
                return instance!!
            }
        }

        @VisibleForTesting
        fun swap(locator: ServiceLocator) {
            instance = locator
        }
    }

    fun getRepository(type: GitHubRepositoryContract.Type): GitHubRepositoryContract

    fun getNetworkExecutor(): Executor

    fun getDiskIOExecutor(): Executor

    fun getGitHubService(): GitHubService
}

open class DefaultServiceLocator(val app: Application, val useInMemoryDb: Boolean) : ServiceLocator {

    @Suppress("PrivatePropertyName")
    private val DISK_IO = Executors.newSingleThreadExecutor()

    @Suppress("PrivatePropertyName")
    private val NETWORK_IO = Executors.newFixedThreadPool(5)

    private val db by lazy {
        AppDatabase.create(app, useInMemoryDb)
    }

    private val api by lazy {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofit.create(GitHubService::class.java)
    }

    override fun getRepository(type: GitHubRepositoryContract.Type): GitHubRepositoryContract {
        return when (type) {
            GitHubRepositoryContract.Type.REMOTE -> RepositoryRemote(
                gitHubService = getGitHubService(),
                fetchExecutor = getNetworkExecutor()
            )
            GitHubRepositoryContract.Type.LOCAL -> RepositoryDatabase(
                db = db,
                gitHubService = getGitHubService(),
                fetchExecutor = getDiskIOExecutor()
            )
        }
    }

    override fun getNetworkExecutor(): Executor = NETWORK_IO

    override fun getDiskIOExecutor(): Executor = DISK_IO

    override fun getGitHubService(): GitHubService = api
}