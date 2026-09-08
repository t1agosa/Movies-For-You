package com.tiago.kmpauthflows.domain.model

data class Watchlist(
    val movieId: Int,
    val createdAtMillis: Long = 0L
)