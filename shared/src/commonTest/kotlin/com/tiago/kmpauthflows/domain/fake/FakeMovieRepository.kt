package com.tiago.kmpauthflows.domain.fake

import com.tiago.kmpauthflows.domain.model.Movie
import com.tiago.kmpauthflows.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeMovieRepository : MovieRepository {

    private val popularMoviesFlow = MutableStateFlow<List<Movie>>(emptyList())
    private val movieDetails = mutableMapOf<Int, Movie>()
    var shouldThrowOnDetail: Exception? = null
    var shouldThrowOnSearch: Exception? = null
    var searchResults: List<Movie> = emptyList()

    fun setPopularMovies(movies: List<Movie>) {
        popularMoviesFlow.value = movies
    }

    fun setMovieDetail(movie: Movie) {
        movieDetails[movie.id] = movie
    }

    override fun getPopularMovies(): Flow<List<Movie>> = popularMoviesFlow

    override suspend fun getMovieDetail(movieId: Int): Movie {
        shouldThrowOnDetail?.let { throw it }
        return movieDetails[movieId] ?: throw NoSuchElementException("Movie $movieId not found")
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        shouldThrowOnSearch?.let { throw it }
        return searchResults
    }

}