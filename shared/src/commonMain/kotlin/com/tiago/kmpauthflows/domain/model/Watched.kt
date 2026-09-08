package com.tiago.kmpauthflows.domain.model

data class Watched(
    val movieId: Int,
    val createdAtMillis: Long = 0L
)