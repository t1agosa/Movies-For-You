package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.model.Movie
import com.tiago.kmpauthflows.domain.repository.MovieRepository
import com.tiago.kmpauthflows.domain.util.Result
import io.ktor.utils.io.CancellationException

class GetMovieDetailUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(movieId: Int): Result<Movie> {
        return try {
            Result.Success(repository.getMovieDetail(movieId))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}