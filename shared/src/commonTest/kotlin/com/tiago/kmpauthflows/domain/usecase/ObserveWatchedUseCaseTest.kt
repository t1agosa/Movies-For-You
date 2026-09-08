package com.tiago.kmpauthflows.domain.usecase

import app.cash.turbine.test
import com.tiago.kmpauthflows.domain.fake.FakeWatchedRepository
import com.tiago.kmpauthflows.domain.model.Watched
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveWatchedUseCaseTest {

    private val fakeRepository = FakeWatchedRepository()
    private val useCase = ObserveWatchedUseCase(fakeRepository)

    @Test
    fun `emite la lista de vistas tal cual la expone el repository`() = runTest {
        useCase().test {
            assertEquals(emptyList<Watched>(), awaitItem())

            fakeRepository.toggleWatched(1)

            assertEquals(listOf(Watched(movieId = 1)), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}