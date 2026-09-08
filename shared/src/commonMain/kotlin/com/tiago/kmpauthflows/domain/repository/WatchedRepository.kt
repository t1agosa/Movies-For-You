package com.tiago.kmpauthflows.domain.repository

import com.tiago.kmpauthflows.domain.model.Watched
import kotlinx.coroutines.flow.Flow

interface WatchedRepository {
    fun observeWatched(): Flow<List<Watched>>
    suspend fun toggleWatched(movieId: Int)
}