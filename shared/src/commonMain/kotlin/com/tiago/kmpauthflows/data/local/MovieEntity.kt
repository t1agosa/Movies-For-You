package com.tiago.kmpauthflows.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.tiago.kmpauthflows.domain.model.Movie

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val releaseDate: String?,
    val voteAverage: Double
)

fun MovieEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    releaseDate = releaseDate,
    voteAverage = voteAverage
)
