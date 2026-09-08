package com.tiago.kmpauthflows.domain.repository

import com.tiago.kmpauthflows.domain.model.Watchlist
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun observeWatchlist(): Flow<List<Watchlist>>
    suspend fun toggleWatchlist(movieId: Int)
}
