package com.tiago.kmpauthflows.data.tmdb

import com.tiago.kmpauthflows.domain.model.MovieException
import com.tiago.kmpauthflows.shared.config.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CancellationException

class TmdbApiService(private val httpClient: HttpClient) {

    suspend fun getPopularMovies(): MovieListResponseDto =
        request { httpClient.get("$BASE_URL/movie/popular") { bearerAuth() }.body() }

    suspend fun searchMovies(query: String): MovieListResponseDto =
        request {
            httpClient.get("$BASE_URL/search/movie") {
                bearerAuth()
                parameter("query", query)
            }.body()
        }

    suspend fun getMovieDetail(movieId: Int): MovieDto =
        request { httpClient.get("$BASE_URL/movie/$movieId") { bearerAuth() }.body() }

    private fun HttpRequestBuilder.bearerAuth() {
        header(HttpHeaders.Authorization, "Bearer ${BuildKonfig.TMDB_READ_ACCESS_TOKEN}")
    }

    private suspend fun <T> request(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) throw MovieException.NotFound
            throw MovieException.Unknown(e)
        } catch (e: IOException) {
            throw MovieException.NetworkError
        } catch (e: Exception) {
            throw MovieException.Unknown(e)
        }
    }

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3"
    }
}