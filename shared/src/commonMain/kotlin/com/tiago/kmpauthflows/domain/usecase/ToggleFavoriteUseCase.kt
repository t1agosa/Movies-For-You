package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.repository.FavoriteRepository
import com.tiago.kmpauthflows.domain.util.Result
import kotlinx.coroutines.CancellationException

class ToggleFavoriteUseCase(private val repository: FavoriteRepository) {
    suspend operator fun invoke(movieId: Int): Result<Unit> {
        return try {
            repository.toggleFavorite(movieId)
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}