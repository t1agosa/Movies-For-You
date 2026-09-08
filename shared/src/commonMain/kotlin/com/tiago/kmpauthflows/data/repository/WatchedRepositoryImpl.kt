package com.tiago.kmpauthflows.data.repository

import com.tiago.kmpauthflows.data.firestore.PersonalListFirestoreService
import com.tiago.kmpauthflows.domain.model.Watched
import com.tiago.kmpauthflows.domain.repository.WatchedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchedRepositoryImpl(
    private val firestoreService: PersonalListFirestoreService
) : WatchedRepository {

    override fun observeWatched(): Flow<List<Watched>> =
        firestoreService.observeItems().map { items ->
            items.map { Watched(movieId = it.movieId, createdAtMillis = it.createdAtMillis) }
        }

    override suspend fun toggleWatched(movieId: Int) {
        firestoreService.toggleItem(movieId)
    }
}