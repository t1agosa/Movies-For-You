package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.repository.WatchlistRepository
import com.tiago.kmpauthflows.domain.util.Result
import kotlinx.coroutines.CancellationException

class ToggleWatchlistUseCase(private val repository: WatchlistRepository) {
    suspend operator fun invoke(movieId: Int): Result<Unit> {
        return try {
            repository.toggleWatchlist(movieId)
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}