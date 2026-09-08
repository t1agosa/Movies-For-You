package com.tiago.kmpauthflows.domain.fake

import com.tiago.kmpauthflows.domain.model.Watchlist
import com.tiago.kmpauthflows.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWatchlistRepository : WatchlistRepository {

    private val watchlistFlow = MutableStateFlow<List<Watchlist>>(emptyList())
    var shouldThrowOnToggle: Exception? = null

    override fun observeWatchlist(): Flow<List<Watchlist>> = watchlistFlow

    override suspend fun toggleWatchlist(movieId: Int) {
        shouldThrowOnToggle?.let { throw it }
        val current = watchlistFlow.value
        watchlistFlow.value = if (current.any { it.movieId == movieId }) {
            current.filterNot { it.movieId == movieId }
        } else {
            current + Watchlist(movieId = movieId)
        }
    }
}