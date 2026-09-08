package com.tiago.kmpauthflows.data.repository

import com.tiago.kmpauthflows.data.local.MovieDao
import com.tiago.kmpauthflows.data.local.toDomain
import com.tiago.kmpauthflows.data.tmdb.TmdbApiService
import com.tiago.kmpauthflows.data.tmdb.toDomain
import com.tiago.kmpauthflows.data.tmdb.toEntity
import com.tiago.kmpauthflows.domain.model.Movie
import com.tiago.kmpauthflows.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.cancellation.CancellationException

class MovieRepositoryImpl(
    private val tmdbApiService: TmdbApiService, private val movieDao: MovieDao
) : MovieRepository {

    override fun getPopularMovies(): Flow<List<Movie>> {
        return movieDao.observeAll().map { entities -> entities.map { it.toDomain() } }.onStart {
                try {
                    val response = tmdbApiService.getPopularMovies()
                    movieDao.upsertAll(response.results.map { it.toEntity() })
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // sin red o TMDB caido: la UI sigue mostrando lo que ya esta en cache
                }
            }
    }

    override fun getMoviesByIds(movieIds: List<Int>): Flow<List<Movie>> {
        return movieDao.observeByIds(movieIds).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getMovieDetail(movieId: Int): Movie {
        val dto = tmdbApiService.getMovieDetail(movieId)
        movieDao.upsertAll(listOf(dto.toEntity()))
        return dto.toDomain()
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        val response = tmdbApiService.searchMovies(query)
        movieDao.upsertAll(response.results.map { it.toEntity() })
        return response.results.map { it.toDomain() }
    }
}