package com.tiago.kmpauthflows.domain.repository

import com.tiago.kmpauthflows.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getPopularMovies(): Flow<List<Movie>>
    fun getMoviesByIds(movieIds: List<Int>): Flow<List<Movie>>
    suspend fun getMovieDetail(movieId: Int): Movie
    suspend fun searchMovies(query: String): List<Movie>
}