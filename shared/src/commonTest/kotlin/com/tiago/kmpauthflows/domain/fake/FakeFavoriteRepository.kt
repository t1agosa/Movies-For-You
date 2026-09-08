package com.tiago.kmpauthflows.domain.fake

import com.tiago.kmpauthflows.domain.model.Favorite
import com.tiago.kmpauthflows.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeFavoriteRepository : FavoriteRepository {

    private val favoritesFlow = MutableStateFlow<List<Favorite>>(emptyList())
    var shouldThrowOnToggle: Exception? = null

    override fun observeFavorites(): Flow<List<Favorite>> = favoritesFlow

    override suspend fun toggleFavorite(movieId: Int) {
        shouldThrowOnToggle?.let { throw it }
        val current = favoritesFlow.value
        favoritesFlow.value = if (current.any { it.movieId == movieId }) {
            current.filterNot { it.movieId == movieId }
        } else {
            current + Favorite(movieId = movieId)
        }
    }
}