package com.tiago.kmpauthflows.presentation.detail

import com.tiago.kmpauthflows.domain.model.Movie
import org.jetbrains.compose.resources.StringResource

data class DetailState(
    val movie: Movie? = null,
    val isFavorite: Boolean = false,
    val isWatched: Boolean = false,
    val isInWatchlist: Boolean = false,
    val isLoading: Boolean = true,
    val error: StringResource? = null
)

sealed interface DetailEvent {
    data object OnFavoriteToggle : DetailEvent
    data object OnWatchedToggle : DetailEvent
    data object OnWatchlistToggle : DetailEvent
    data object OnRetry : DetailEvent
}

sealed interface DetailEffect {
    data class ShowError(val messageRes: StringResource) : DetailEffect
}