package com.example.githubrepositories.repository.remote

import android.util.Log
import com.example.githubrepositories.repository.model.Result
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface GitHubService {

    @GET("/search/repositories?")
    fun searchListRepos(
        @Query("q") query: String,
        @Query("per_page") per_page: Int?,
        @Query("page") page: Int?
    ): Observable<Result>

    companion object {
        private const val BASE_URL = "https://api.github.com/"
        fun create(): GitHubService = create(HttpUrl.parse(BASE_URL)!!)
        fun create(httpUrl: HttpUrl): GitHubService {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("API", it)
            })
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            val gson = GsonBuilder().setLenient().create()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(GitHubService::class.java)
        }
    }
}