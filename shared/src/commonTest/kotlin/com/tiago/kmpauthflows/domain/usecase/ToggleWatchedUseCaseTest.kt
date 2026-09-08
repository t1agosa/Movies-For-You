package com.tiago.kmpauthflows.domain.usecase

import app.cash.turbine.test
import com.tiago.kmpauthflows.domain.fake.FakeWatchedRepository
import com.tiago.kmpauthflows.domain.util.Result
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ToggleWatchedUseCaseTest {

    private val fakeRepository = FakeWatchedRepository()
    private val useCase = ToggleWatchedUseCase(fakeRepository)

    @Test
    fun `agrega la pelicula a vistas cuando no estaba marcada`() = runTest {
        val result = useCase(1)

        assertEquals(Result.Success(Unit), result)
        fakeRepository.observeWatched().test {
            assertEquals(listOf(1), awaitItem().map { it.movieId })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `quita la pelicula de vistas cuando ya estaba marcada`() = runTest {
        useCase(1)
        val result = useCase(1)

        assertEquals(Result.Success(Unit), result)
        fakeRepository.observeWatched().test {
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