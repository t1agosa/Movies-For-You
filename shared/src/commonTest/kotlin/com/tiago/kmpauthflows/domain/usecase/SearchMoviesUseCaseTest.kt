package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.fake.FakeMovieRepository
import com.tiago.kmpauthflows.domain.model.Movie
import com.tiago.kmpauthflows.domain.util.Result
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SearchMoviesUseCaseTest {

    private val fakeRepository = FakeMovieRepository()
    private val useCase = SearchMoviesUseCase(fakeRepository)

    @Test
    fun `devuelve Success con los resultados de busqueda del repository`() = runTest {
        val movie = Movie(
            id = 1,
            title = "Interstellar",
            overview = "Un grupo de exploradores viaja a traves de un agujero de gusano",
            posterUrl = null,
            releaseDate = "2014-11-06",
            voteAverage = 8.6
        )
        fakeRepository.searchResults = listOf(movie)

        val result = useCase("Interstellar")

        assertEquals(Result.Success(listOf(movie)), result)
    }

    @Test
    fun `devuelve Error cuando el repository falla`() = runTest {
        fakeRepository.shouldThrowOnSearch = RuntimeException("network down")

        val result = useCase("Interstellar")

        assertIs<Result.Error>(result)
    }
}