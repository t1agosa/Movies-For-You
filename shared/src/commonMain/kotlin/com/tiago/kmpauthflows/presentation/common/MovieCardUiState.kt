package com.tiago.kmpauthflows.presentation.common

import com.tiago.kmpauthflows.domain.model.Movie

data class MovieCardUiState(
    val movie: Movie,
    val isFavorite: Boolean,
    val isWatched: Boolean,
    val isInWatchlist: Boolean
)