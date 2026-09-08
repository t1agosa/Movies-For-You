package com.tiago.kmpauthflows.data.local

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao{

    @Upsert
    suspend fun upsertAll(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies ORDER BY voteAverage DESC")
    fun observeAll(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE id IN (:ids)")
    fun observeByIds(ids: List<Int>): Flow<List<MovieEntity>>
}