package com.tiago.kmpauthflows.domain.usecase

import app.cash.turbine.test
import com.tiago.kmpauthflows.domain.fake.FakeFavoriteRepository
import com.tiago.kmpauthflows.domain.model.Favorite
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveFavoritesUseCaseTest {

    private val fakeRepository = FakeFavoriteRepository()
    private val useCase = ObserveFavoritesUseCase(fakeRepository)

    @Test
    fun `emite la lista de favoritos tal cual la expone el repository`() = runTest {
        useCase().test {
            assertEquals(emptyList<Favorite>(), awaitItem())

            fakeRepository.toggleFavorite(1)

            assertEquals(listOf(Favorite(movieId = 1)), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}