package com.tiago.kmpauthflows.domain.fake

import com.tiago.kmpauthflows.domain.model.Watched
import com.tiago.kmpauthflows.domain.repository.WatchedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWatchedRepository : WatchedRepository {

    private val watchedFlow = MutableStateFlow<List<Watched>>(emptyList())
    var shouldThrowOnToggle: Exception? = null

    override fun observeWatched(): Flow<List<Watched>> = watchedFlow

    override suspend fun toggleWatched(movieId: Int) {
        shouldThrowOnToggle?.let { throw it }
        val current = watchedFlow.value
        watchedFlow.value = if (current.any { it.movieId == movieId }) {
            current.filterNot { it.movieId == movieId }
        } else {
            current + Watched(movieId = movieId)
        }
    }
}