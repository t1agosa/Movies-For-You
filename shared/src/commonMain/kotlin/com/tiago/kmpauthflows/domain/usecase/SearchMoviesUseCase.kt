package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.model.Movie
import com.tiago.kmpauthflows.domain.repository.MovieRepository
import com.tiago.kmpauthflows.domain.util.Result
import kotlin.coroutines.cancellation.CancellationException

class SearchMoviesUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(query: String): Result<List<Movie>> {
        return try {
            Result.Success(repository.searchMovies(query))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
