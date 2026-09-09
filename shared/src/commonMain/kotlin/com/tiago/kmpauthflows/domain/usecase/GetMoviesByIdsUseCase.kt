package com.tiago.kmpauthflows.domain.usecase

import com.tiago.kmpauthflows.domain.model.Movie
import com.tiago.kmpauthflows.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMoviesByIdsUseCase(private val repository: MovieRepository) {
    operator fun invoke(movieIds: List<Int>): Flow<List<Movie>> = repository.getMoviesByIds(movieIds)
}