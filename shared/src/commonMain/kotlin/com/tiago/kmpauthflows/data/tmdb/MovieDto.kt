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
    @SerialName("release_date") val releaseDate: String?,
    @SerialName("vote_average") val voteAverage: Double
)

private const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

private fun String?.toPosterUrl(): String? = this?.let { "$TMDB_IMAGE_BASE_URL$it" }

fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterPath.toPosterUrl(),
    releaseDate = releaseDate,
    voteAverage = voteAverage
)

fun MovieDto.toEntity(): MovieEntity = MovieEntity(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterPath.toPosterUrl(),
    releaseDate = releaseDate,
    voteAverage = voteAverage
)