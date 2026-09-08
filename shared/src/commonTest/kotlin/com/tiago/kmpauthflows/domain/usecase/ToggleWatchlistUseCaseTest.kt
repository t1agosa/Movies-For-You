package com.tiago.kmpauthflows.domain.usecase

import app.cash.turbine.test
import com.tiago.kmpauthflows.domain.fake.FakeWatchlistRepository
import com.tiago.kmpauthflows.domain.util.Result
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ToggleWatchlistUseCaseTest {

    private val fakeRepository = FakeWatchlistRepository()
    private val useCase = ToggleWatchlistUseCase(fakeRepository)

    @Test
    fun `agrega la pelicula a por ver cuando no estaba marcada`() = runTest {
        val result = useCase(1)

        assertEquals(Result.Success(Unit), result)
        fakeRepository.observeWatchlist().test {
            assertEquals(listOf(1), awaitItem().map { it.movieId })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `quita la pelicula de por ver cuando ya estaba marcada`() = runTest {
        useCase(1)
        val result = useCase(1)

        assertEquals(Result.Success(Unit), result)
        fakeRepository.observeWatchlist().test {
            assertEquals(emptyList<Int>(), awaitItem().map { it.movieId })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `devuelve Error cuando el repository falla`() = runTest {
        fakeRepository.shouldThrowOnToggle = RuntimeException("firestore down")

        val result = useCase(1)

        assertIs<Result.Error>(result)
    }
}