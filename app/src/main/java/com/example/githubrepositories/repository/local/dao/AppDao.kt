package com.example.githubrepositories.repository.local.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.githubrepositories.repository.model.Repository

@Dao
interface AppDao {
    @Query("SELECT * FROM repository")
    fun getAll(): List<Repository>

    @Query("SELECT * FROM repository WHERE name = :name")
    fun searchRepositories(name: String): DataSource.Factory<Int, Repository>

    @Insert
    fun insert(repositories: List<Repository>)

    @Delete
    fun delete(repository: Repository)

    @Query("DELETE FROM repository WHERE name = :name")
    fun deleteByName(name: String)
}