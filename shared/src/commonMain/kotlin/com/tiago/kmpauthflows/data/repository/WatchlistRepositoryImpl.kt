package com.tiago.kmpauthflows.data.repository

import com.tiago.kmpauthflows.data.firestore.PersonalListFirestoreService
import com.tiago.kmpauthflows.domain.model.Watchlist
import com.tiago.kmpauthflows.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchlistRepositoryImpl(
    private val firestoreService: PersonalListFirestoreService
) : WatchlistRepository {

    override fun observeWatchlist(): Flow<List<Watchlist>> =
        firestoreService.observeItems().map { items ->
            items.map { Watchlist(movieId = it.movieId, createdAtMillis = it.createdAtMillis) }
        }

    override suspend fun toggleWatchlist(movieId: Int) {
        firestoreService.toggleItem(movieId)
    }
}