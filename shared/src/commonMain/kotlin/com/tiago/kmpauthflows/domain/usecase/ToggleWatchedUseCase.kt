package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.repository.WatchedRepository
import com.tiago.kmpauthflows.domain.util.Result
import kotlinx.coroutines.CancellationException

class ToggleWatchedUseCase(private val repository: WatchedRepository) {
    suspend operator fun invoke(movieId: Int): Result<Unit> {
        return try {
            repository.toggleWatched(movieId)
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}