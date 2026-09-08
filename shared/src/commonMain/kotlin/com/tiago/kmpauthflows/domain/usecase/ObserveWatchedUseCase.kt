package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.model.Watched
import com.tiago.kmpauthflows.domain.repository.WatchedRepository
import kotlinx.coroutines.flow.Flow

class ObserveWatchedUseCase(private val repository: WatchedRepository) {
    operator fun invoke(): Flow<List<Watched>> = repository.observeWatched()
}