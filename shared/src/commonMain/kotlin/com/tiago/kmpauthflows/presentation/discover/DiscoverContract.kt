package com.tiago.kmpauthflows.presentation.discover

import com.tiago.kmpauthflows.presentation.common.MovieCardUiState
import org.jetbrains.compose.resources.StringResource

data class DiscoverState(
    val searchQuery: String = "",
    val movies: List<MovieCardUiState> = emptyList(),
    val isLoading: Boolean = true,
    val error: StringResource? = null
)

sealed interface DiscoverEvent {
    data class OnSearchQueryChanged(val query: String) : DiscoverEvent
    data object OnSearchSubmit : DiscoverEvent
    data class OnMovieClick(val movieId: Int) : DiscoverEvent
    data class OnFavoriteToggle(val movieId: Int) : DiscoverEvent
    data class OnWatchedToggle(val movieId: Int) : DiscoverEvent
    data class OnWatchlistToggle(val movieId: Int) : DiscoverEvent
}

sealed interface DiscoverEffect {
    data class NavigateToDetail(val movieId: Int) : DiscoverEffect
    data class ShowError(val messageRes: StringResource) : DiscoverEffect
}