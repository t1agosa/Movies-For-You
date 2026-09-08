package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.fake.FakeMovieRepository
import com.tiago.kmpauthflows.domain.model.Movie
import com.tiago.kmpauthflows.domain.util.Result
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetMovieDetailUseCaseTest {

    private val fakeRepository = FakeMovieRepository()
    private val useCase = GetMovieDetailUseCase(fakeRepository)

    @Test
    fun `devuelve Success con la pelicula cuando el repository la encuentra`() = runTest {
        val movie = Movie(
            id = 1,
            title = "Interstellar",
            overview = "Un grupo de exploradores viaja a traves de un agujero de gusano",
            posterUrl = null,
            releaseDate = "2014-11-06",
            voteAverage = 8.6
        )
        fakeRepository.setMovieDetail(movie)

        val result = useCase(1)

        assertEquals(Result.Success(movie), result)
    }

    @Test
    fun `devuelve Error cuando el repository falla`() = runTest {
        fakeRepository.shouldThrowOnDetail = RuntimeException("network down")

        val result = useCase(1)

        assertIs<Result.Error>(result)
    }
}