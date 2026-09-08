package com.tiago.kmpauthflows.domain.repository

import com.tiago.kmpauthflows.domain.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<Favorite>>
    suspend fun toggleFavorite(movieId: Int)
}