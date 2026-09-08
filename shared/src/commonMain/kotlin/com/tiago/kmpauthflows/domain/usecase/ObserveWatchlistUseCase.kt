package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.model.Watchlist
import com.tiago.kmpauthflows.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow

class ObserveWatchlistUseCase(private val repository: WatchlistRepository) {
    operator fun invoke(): Flow<List<Watchlist>> = repository.observeWatchlist()
}