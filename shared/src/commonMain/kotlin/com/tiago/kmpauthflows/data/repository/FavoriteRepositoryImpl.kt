package com.tiago.kmpauthflows.data.repository

import com.tiago.kmpauthflows.data.firestore.PersonalListFirestoreService
import com.tiago.kmpauthflows.domain.model.Favorite
import com.tiago.kmpauthflows.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val firestoreService: PersonalListFirestoreService
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<Favorite>> =
        firestoreService.observeItems().map { items ->
            items.map { Favorite(movieId = it.movieId, createdAtMillis = it.createdAtMillis) }
        }

    override suspend fun toggleFavorite(movieId: Int) {
        firestoreService.toggleItem(movieId)
    }
}