package com.tiago.kmpauthflows.domain.usecase

import app.cash.turbine.test
import com.tiago.kmpauthflows.domain.fake.FakeWatchlistRepository
import com.tiago.kmpauthflows.domain.model.Watchlist
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveWatchlistUseCaseTest {

    private val fakeRepository = FakeWatchlistRepository()
    private val useCase = ObserveWatchlistUseCase(fakeRepository)

    @Test
    fun `emite la lista de por ver tal cual la expone el repository`() = runTest {
        useCase().test {
            assertEquals(emptyList<Watchlist>(), awaitItem())

            fakeRepository.toggleWatchlist(1)

            assertEquals(listOf(Watchlist(movieId = 1)), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}