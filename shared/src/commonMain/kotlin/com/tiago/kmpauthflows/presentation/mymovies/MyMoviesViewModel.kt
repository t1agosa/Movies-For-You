package com.tiago.kmpauthflows.presentation.mymovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiago.kmpauthflows.domain.model.MovieException
import com.tiago.kmpauthflows.domain.usecase.GetMoviesByIdsUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveFavoritesUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveWatchedUseCase
import com.tiago.kmpauthflows.domain.usecase.ObserveWatchlistUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleFavoriteUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleWatchedUseCase
import com.tiago.kmpauthflows.domain.usecase.ToggleWatchlistUseCase
import com.tiago.kmpauthflows.domain.util.Result
import com.tiago.kmpauthflows.presentation.common.MovieCardUiState
import com.tiago.kmpauthflows.presentation.common.toMessageRes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MyMoviesViewModel(
    private val getMoviesByIdsUseCase: GetMoviesByIdsUseCase,
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    observeWatchedUseCase: ObserveWatchedUseCase,
    observeWatchlistUseCase: ObserveWatchlistUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val toggleWatchedUseCase: ToggleWatchedUseCase,
    private val toggleWatchlistUseCase: ToggleWatchlistUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MyMoviesState())
    val state: StateFlow<MyMoviesState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MyMoviesEffect>()
    val effect: SharedFlow<MyMoviesEffect> = _effect.asSharedFlow()

    private val favoriteIds = observeFavoritesUseCase()
        .map { list -> list.map { it.movieId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private val watchedIds = observeWatchedUseCase()
        .map { list -> list.map { it.movieId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private val watchlistIds = observeWatchlistUseCase()
        .map { list -> list.map { it.movieId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    init {
        viewModelScope.launch {
            combine(
                buildSection(favoriteIds),
                buildSection(watchedIds),
                buildSection(watchlistIds)
            ) { favorites, watched, watchlist ->
                MyMoviesState(favorites = favorites, watched = watched, watchlist = watchlist, isLoading = false)
            }.collect { newState -> _state.value = newState }
        }
    }

    private fun buildSection(sectionIds: Flow<Set<Int>>): Flow<List<MovieCardUiState>> {
        return combine(
            sectionIds.flatMapLatest { ids -> getMoviesByIdsUseCase(ids.toList()) },
            favoriteIds,
            watchedIds,
            watchlistIds
        ) { movies, favIds, watchIds, watchlistIds ->
            movies.map { movie ->
                MovieCardUiState(
                    movie = movie,
                    isFavorite = movie.id in favIds,
                    isWatched = movie.id in watchIds,
                    isInWatchlist = movie.id in watchlistIds
                )
            }
        }
    }

    fun onEvent(event: MyMoviesEvent) {
        when (event) {
            is MyMoviesEvent.OnMovieClick -> onMovieClick(event.movieId)
            is MyMoviesEvent.OnFavoriteToggle -> onFavoriteToggle(event.movieId)
            is MyMoviesEvent.OnWatchedToggle -> onWatchedToggle(event.movieId)
            is MyMoviesEvent.OnWatchlistToggle -> onWatchlistToggle(event.movieId)
        }
    }

    private fun onMovieClick(movieId: Int) {
        viewModelScope.launch { _effect.emit(MyMoviesEffect.NavigateToDetail(movieId)) }
    }

    private fun onFavoriteToggle(movieId: Int) {
        viewModelScope.launch {
            val result = toggleFavoriteUseCase(movieId)
            if (result is Result.Error) {
                val movieException = result.exception as? MovieException ?: MovieException.Unknown(result.exception)
                _effect.emit(MyMoviesEffect.ShowError(movieException.toMessageRes()))
            }
        }
    }

    private fun onWatchedToggle(movieId: Int) {
        viewModelScope.launch {
            val result = toggleWatchedUseCase(movieId)
            if (result is Result.Error) {
                val movieException = result.exception as? MovieException ?: MovieException.Unknown(result.exception)
                _effect.emit(MyMoviesEffect.ShowError(movieException.toMessageRes()))
            }
        }
    }

    private fun onWatchlistToggle(movieId: Int) {
        viewModelScope.launch {
            val result = toggleWatchlistUseCase(movieId)
            if (result is Result.Error) {
                val movieException = result.exception as? MovieException ?: MovieException.Unknown(result.exception)
                _effect.emit(MyMoviesEffect.ShowError(movieException.toMessageRes()))
            }
        }
    }
}