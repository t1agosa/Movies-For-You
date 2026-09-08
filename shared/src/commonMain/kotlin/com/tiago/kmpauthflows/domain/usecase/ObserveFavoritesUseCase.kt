package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.model.Favorite
import com.tiago.kmpauthflows.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoritesUseCase(private val repository: FavoriteRepository) {
    operator fun invoke(): Flow<List<Favorite>> = repository.observeFavorites()
}