package com.tiago.kmpauthflows.data.tmdb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MovieDtoMapperTest {

    private val dto = MovieDto(
        id = 1,
        title = "Interstellar",
        overview = "Un grupo de exploradores viaja a traves de un agujero de gusano",
        posterPath = "/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
        releaseDate = "2014-11-06",
        voteAverage = 8.6
    )

    @Test
    fun `toDomain arma la url completa del poster a partir del path relativo`() {
        val movie = dto.toDomain()

        assertEquals(
            "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
            movie.posterUrl
        )
    }

    @Test
    fun `toDomain no rompe si posterPath es null`() {
        val movie = dto.copy(posterPath = null).toDomain()

        assertNull(movie.posterUrl)
    }

    @Test
    fun `toEntity construye la misma url de poster que toDomain`() {
        val entity = dto.toEntity()

        assertEquals("https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg", entity.posterUrl)
    }
}