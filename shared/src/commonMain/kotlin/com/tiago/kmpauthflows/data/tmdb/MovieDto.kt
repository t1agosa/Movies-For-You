package com.tiago.kmpauthflows.data.tmdb

import com.tiago.kmpauthflows.data.local.MovieEntity
import com.tiago.kmpauthflows.domain.model.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String?,
    @SerialName("vote_average") val voteAverage: Double
)

private const val TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"
private const val TMDB_BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780"

private fun String?.toImageUrl(baseUrl: String): String? = this?.let { "$baseUrl$it" }

fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterPath.toImageUrl(TMDB_POSTER_BASE_URL),
    backdropUrl = backdropPath.toImageUrl(TMDB_BACKDROP_BASE_URL),
    releaseDate = releaseDate,
    voteAverage = voteAverage
)

fun MovieDto.toEntity(): MovieEntity = MovieEntity(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterPath.toImageUrl(TMDB_POSTER_BASE_URL),
    backdropUrl = backdropPath.toImageUrl(TMDB_BACKDROP_BASE_URL),
    releaseDate = releaseDate,
    voteAverage = voteAverage
)