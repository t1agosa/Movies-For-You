package com.tiago.kmpauthflows.presentation.mymovies

import com.tiago.kmpauthflows.presentation.common.MovieCardUiState
import org.jetbrains.compose.resources.StringResource

data class MyMoviesState(
    val favorites: List<MovieCardUiState> = emptyList(),
    val watched: List<MovieCardUiState> = emptyList(),
    val watchlist: List<MovieCardUiState> = emptyList()
)

sealed interface MyMoviesEvent {
    data class OnMovieClick(val movieId: Int) : MyMoviesEvent
    data class OnFavoriteToggle(val movieId: Int) : MyMoviesEvent
    data class OnWatchedToggle(val movieId: Int) : MyMoviesEvent
    data class OnWatchlistToggle(val movieId: Int) : MyMoviesEvent
}

sealed interface MyMoviesEffect {
    data class NavigateToDetail(val movieId: Int) : MyMoviesEffect
    data class ShowError(val messageRes: StringResource) : MyMoviesEffect
}