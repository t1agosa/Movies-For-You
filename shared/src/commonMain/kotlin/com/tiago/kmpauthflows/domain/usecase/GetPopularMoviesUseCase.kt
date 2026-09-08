package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.model.Movie
import com.tiago.kmpauthflows.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetPopularMoviesUseCase(private val repository: MovieRepository) {
    operator fun invoke(): Flow<List<Movie>> = repository.getPopularMovies()
}