package com.tiago.kmpauthflows.domain.usecase

import app.cash.turbine.test
import com.tiago.kmpauthflows.domain.fake.FakeMovieRepository
import com.tiago.kmpauthflows.domain.model.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetMoviesByIdsUseCaseTest {

    private val fakeRepository = FakeMovieRepository()
    private val useCase = GetMoviesByIdsUseCase(fakeRepository)

    @Test
    fun `emite la lista de peliculas filtrada por ids tal cual la expone el repository`() = runTest {
        val movie = Movie(
            id = 1,
            title = "Interstellar",
            overview = "Un grupo de exploradores viaja a traves de un agujero de gusano",
            posterUrl = null,
            releaseDate = "2014-11-06",
            voteAverage = 8.6
        )

        useCase(listOf(1)).test {
            assertEquals(emptyList<Movie>(), awaitItem())

            fakeRepository.setMoviesByIds(listOf(movie))

            assertEquals(listOf(movie), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}