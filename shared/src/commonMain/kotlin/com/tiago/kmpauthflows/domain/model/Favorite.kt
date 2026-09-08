package com.tiago.kmpauthflows.domain.model

data class Favorite(
    val movieId: Int,
    val createdAtMillis: Long = 0L
)