package com.tiago.kmpauthflows.domain.usecase

import app.cash.turbine.test
import com.tiago.kmpauthflows.domain.fake.FakeFavoriteRepository
import com.tiago.kmpauthflows.domain.util.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class ToggleFavoriteUseCaseTest {

    private val fakeRepository = FakeFavoriteRepository()
    private val useCase = ToggleFavoriteUseCase(fakeRepository)

    @Test
    fun `agrega la pelicula a favoritos cuando no estaba marcada`() = runTest {
        val result = useCase(1)

        assertEquals(Result.Success(Unit), result)
        fakeRepository.observeFavorites().test {
            assertEquals(listOf(1), awaitItem().map { it.movieId })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `quita la pelicula de favoritos cuando ya estaba marcada`() = runTest {
        useCase(1)
        val result = useCase(1)

        assertEquals(Result.Success(Unit), result)
        fakeRepository.observeFavorites().test {
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

    @Test
    fun `relanza CancellationException en vez de envolverla en Result Error`() = runTest {
        fakeRepository.shouldThrowOnToggle = CancellationException("cancelado")

        assertFailsWith<CancellationException> {
            useCase(1)
        }
    }
}